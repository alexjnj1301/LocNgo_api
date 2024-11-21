package com.locngo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locngo.dto.*;
import com.locngo.entity.Attendees;
import com.locngo.entity.Reservation;
import com.locngo.exceptions.ReservationNotFoundException;
import com.locngo.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public ReservationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ReservationDto findById(int id) {
        var reservation = reservationRepository.findById(id).orElseThrow((() ->
                new ReservationNotFoundException(String.format("Reservation with id %s not found", id))));

        var lieu = reservation.getLieu();
        var lieuDto = new LieuDto(lieu.getId(), lieu.getName(), lieu.getAddress(), lieu.getCity(), lieu.getPostal_code());

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

    public ReservationDto createReservation(CreateReservation reservationDto) {
        var reservation = new Reservation(reservationDto.id(), reservationDto.lieu(), reservationDto.start_date(), reservationDto.end_date(),
                reservationDto.attendees() != null ? reservationDto.attendees().size() : 0,
                reservationDto.reference(), null);

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

        var updatedReservation = new Reservation(reservation.id(), reservation.lieu(), reservation.start_date(), reservation.end_date(),
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

    public void deleteByLieuId(int lieuId) {
        // Rechercher toutes les réservations associées au lieu
        List<Reservation> reservations = reservationRepository.findAllByLieuId(lieuId);

        // Supprimer chaque réservation une par une
        reservations.forEach(reservation -> deleteById(reservation.getId()));
    }
}
