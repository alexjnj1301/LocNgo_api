package com.locngo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locngo.dto.*;
import com.locngo.entity.Attendees;
import com.locngo.entity.LieuImage;
import com.locngo.entity.Reservation;
import com.locngo.entity.User;
import com.locngo.exceptions.ReservationNotFoundException;
import com.locngo.exceptions.UserNotFoundException;
import com.locngo.repository.ReservationRepository;
import com.locngo.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    private final ObjectMapper objectMapper;
    @Autowired
    private AttendeesService attendeesService;
    @Autowired
    private ReservationAttendeeService reservationAttendeeService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService userService;

    public ReservationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ReservationDto findById(int id) {
        var reservation = reservationRepository.findById(id).orElseThrow((() ->
                new ReservationNotFoundException(String.format("Reservation with id %s not found", id))));

        var lieu = reservation.getLieu();
        var lieuDto = new LieuDto(lieu.getId(), lieu.getName(), lieu.getAddress(), lieu.getCity(), lieu.getPostal_code(),
                lieu.getPrice(), lieu.getDescription(), lieu.getFavorite_picture());

        List<ReservationAttendeeDto> attendeesDto = reservation.getAttendees().stream()
                .map(reservationAttendee -> {
                    var attendee = reservationAttendee.getAttendee();
                    return new ReservationAttendeeDto(attendee.getId(), attendee.getName(), attendee.getFirstname());
                })
                .collect(Collectors.toList());

        return new ReservationDto(reservation.getId(), lieuDto, reservation.getStart_date(), reservation.getEnd_date(),
                reservation.getNb_person(),
                reservation.getReference(), attendeesDto
        );
    }


    public List<ReservationDto> findAll() {
        var reservations = new ArrayList<ReservationDto>();
        reservationRepository.findAll().forEach(reservation -> {
            reservations.add(this.objectMapper.convertValue(reservation, ReservationDto.class));
        });
        return reservations;
    }

    public ReservationDto createReservation(CreateReservation reservationDto, String token) {
        var reservation = new Reservation(reservationDto.id(), reservationDto.lieu(), null, reservationDto.start_date(), reservationDto.end_date(),
                reservationDto.attendees() != null ? reservationDto.attendees().size() : 0,
                reservationDto.reference(), null);

        reservation.setUser(userService.findByEmail(jwtUtils.getEmailFromJwtToken(token)));
        reservation = reservationRepository.save(reservation);

        if (reservationDto.attendees() != null) {
            Reservation finalReservation = reservation;
            reservationDto.attendees().forEach(attendeeDto -> {
                Attendees attendee = new Attendees(attendeeDto.id(), attendeeDto.name(), attendeeDto.firstname(), null, null);
                var createdAttendee = attendeesService.createAttendees(attendee);
                attendee.setId(createdAttendee.id());

                reservationAttendeeService.createMapping(finalReservation, attendee);
            });
        }

        return this.objectMapper.convertValue(reservation, ReservationDto.class);
    }

    @Transactional
    public void deleteById(int id) {
        var reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.getAttendees().removeIf(attendee -> true);

        reservationRepository.delete(reservation);
    }

    @Transactional
    public void updateById(UpdateReservation reservation) {
        var existingReservation = reservationRepository.findById(reservation.id())
                .orElseThrow(() -> new ReservationNotFoundException(
                        String.format("Reservation with id %s not found", reservation.id())
                ));

        var updatedReservation = new Reservation(reservation.id(), reservation.lieu(), reservation.user(), reservation.start_date(), reservation.end_date(),
                reservation.attendees() != null ? reservation.attendees().size() : 0,
                existingReservation.getReference(), existingReservation.getAttendees());

        reservationAttendeeService.deleteAll(existingReservation.getAttendees());
        existingReservation.getAttendees().clear();

        reservationRepository.flush();
        attendeesService.deleteOrphans();

        assert reservation.attendees() != null;
        reservation.attendees().forEach(
                attendees -> {
                    var newAttendee = attendeesService.createAttendees(attendees);
                    Attendees attendee = new Attendees(newAttendee.id(), newAttendee.name(), newAttendee.firstname(), null, null);
                    reservationAttendeeService.createMapping(updatedReservation, attendee);
                }
        );

        reservationRepository.save(updatedReservation);
    }

    @Transactional
    public void deleteByLieuId(int lieuId) {
        List<Reservation> reservations = reservationRepository.findAllByLieuId(lieuId);

        reservations.forEach(reservation -> deleteById(reservation.getId()));
    }

    @Transactional(readOnly = true)
    public List<AllReservationsByUserIdDto> getReservationsByUserId(int userId, String token) throws AccessDeniedException {
        String emailFromToken = jwtUtils.getEmailFromJwtToken(token);
        List<String> rolesFromToken = jwtUtils.getRolesFromJwtToken(token);

        User connectedUser = userService.findByEmail(emailFromToken);

        if (connectedUser.getId() != userId || !rolesFromToken.contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("You do not have permission to access these reservations");
        }

        return reservationRepository.findByUserId(userId).stream()
            .map(
                    reservation -> {
                        List<String> lieuImages = reservation.getLieu().getImages().stream()
                                .map(LieuImage::getImageUrl)
                                .collect(Collectors.toList());
                        return new AllReservationsByUserIdDto(reservation.getId(), lieuImages, reservation.getStart_date(), reservation.getEnd_date(), reservation.getReference());
                    }
            )
            .collect(Collectors.toList());
    }
}
