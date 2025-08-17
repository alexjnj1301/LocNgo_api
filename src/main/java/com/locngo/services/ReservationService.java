package com.locngo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locngo.constants.ReservationStatus;
import com.locngo.dto.AllReservationsByUserIdDto;
import com.locngo.dto.CreateReservation;
import com.locngo.dto.LieuDto;
import com.locngo.dto.ReservationAttendeeDto;
import com.locngo.dto.ReservationDto;
import com.locngo.dto.UpdateReservation;
import com.locngo.entity.Attendees;
import com.locngo.entity.Reservation;
import com.locngo.entity.User;
import com.locngo.exceptions.ReservationNotFoundException;
import com.locngo.repository.ReservationRepository;
import com.locngo.utils.JwtUtils;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {
  @Autowired ReservationRepository reservationRepository;
  private final ObjectMapper objectMapper;
  @Autowired AttendeesService attendeesService;
  @Autowired ReservationAttendeeService reservationAttendeeService;
  @Autowired JwtUtils jwtUtils;
  @Autowired UserService userService;

  public ReservationService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public ReservationDto findById(int id) {
    var reservation =
        reservationRepository
            .findById(id)
            .orElseThrow(
                (() ->
                    new ReservationNotFoundException(
                        String.format("Reservation with id %s not found", id))));

    var lieu = reservation.getLieu();
    var lieuDto =
        new LieuDto(
            lieu.getId(),
            lieu.getName(),
            lieu.getAddress(),
            lieu.getCity(),
            lieu.getPostal_code(),
            lieu.getPrice(),
            lieu.getDescription(),
            lieu.getFavorite_picture());

    List<ReservationAttendeeDto> attendeesDto =
        reservation.getAttendees().stream()
            .map(
                reservationAttendee -> {
                  var attendee = reservationAttendee.getAttendee();
                  return new ReservationAttendeeDto(
                      attendee.getId(), attendee.getName(), attendee.getFirstname());
                })
            .collect(Collectors.toList());

    return new ReservationDto(
        reservation.getId(),
        lieuDto,
        reservation.getStart_date(),
        reservation.getEnd_date(),
        reservation.getNb_person(),
        reservation.getReference(),
        reservation.getStatus(),
        attendeesDto);
  }

  public List<ReservationDto> findAll() {
    var reservations = new ArrayList<ReservationDto>();
    reservationRepository
        .findAll()
        .forEach(
            reservation -> {
              reservations.add(this.objectMapper.convertValue(reservation, ReservationDto.class));
            });
    return reservations;
  }

  public ReservationDto createReservation(CreateReservation reservationDto, String token) {
    var reservation =
        new Reservation(
            reservationDto.id(),
            reservationDto.lieu(),
            null,
            reservationDto.start_date(),
            reservationDto.end_date(),
            reservationDto.attendees() != null ? reservationDto.attendees().size() : 0,
            reservationDto.reference(),
            ReservationStatus.PENDING.name(),
            null);

    reservation.setUser(userService.findByEmail(jwtUtils.getEmailFromJwtToken(token)));
    reservation = reservationRepository.save(reservation);

    if (reservationDto.attendees() != null) {
      Reservation finalReservation = reservation;
      reservationDto
          .attendees()
          .forEach(
              attendeeDto -> {
                Attendees attendee =
                    new Attendees(
                        attendeeDto.id(),
                        finalReservation.getUser().getLastname(),
                        attendeeDto.firstname(),
                        null,
                        null);
                var createdAttendee = attendeesService.createAttendees(attendee);
                attendee.setId(createdAttendee.id());

                reservationAttendeeService.createMapping(finalReservation, attendee);
              });
    }

    return this.objectMapper.convertValue(reservation, ReservationDto.class);
  }

  @Transactional
  public void deleteById(int id) {
    var reservation =
        reservationRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Reservation not found"));

    reservation.getAttendees().removeIf(attendee -> true);

    reservationRepository.delete(reservation);
  }

  @Transactional
  public void updateById(UpdateReservation reservation) {
    var existingReservation =
        reservationRepository
            .findById(reservation.id())
            .orElseThrow(
                () ->
                    new ReservationNotFoundException(
                        String.format("Reservation with id %s not found", reservation.id())));

    var updatedReservation =
        new Reservation(
            reservation.id(),
            reservation.lieu(),
            reservation.user(),
            reservation.start_date(),
            reservation.end_date(),
            reservation.attendees() != null ? reservation.attendees().size() : 0,
            existingReservation.getReference(),
            reservation.status(),
            existingReservation.getAttendees());

    reservationAttendeeService.deleteAll(existingReservation.getAttendees());
    existingReservation.getAttendees().clear();

    reservationRepository.flush();
    attendeesService.deleteOrphans();

    assert reservation.attendees() != null;
    reservation
        .attendees()
        .forEach(
            attendees -> {
              var newAttendee = attendeesService.createAttendees(attendees);
              Attendees attendee =
                  new Attendees(
                      newAttendee.id(), newAttendee.name(), newAttendee.firstname(), null, null);
              reservationAttendeeService.createMapping(updatedReservation, attendee);
            });

    reservationRepository.save(updatedReservation);
  }

  @Transactional
  public void deleteByLieuId(int lieuId) {
    List<Reservation> reservations = reservationRepository.findAllByLieuId(lieuId);

    reservations.forEach(reservation -> deleteById(reservation.getId()));
  }

  @Transactional(readOnly = true)
  public List<AllReservationsByUserIdDto> getReservationsByUserId(int userId, String token)
      throws AccessDeniedException {
    String emailFromToken = jwtUtils.getEmailFromJwtToken(token);
    List<String> rolesFromToken = jwtUtils.getRolesFromJwtToken(token);

    User connectedUser = userService.findByEmail(emailFromToken);

    if (connectedUser.getId() != userId && !rolesFromToken.contains("ROLE_ADMIN")) {
      throw new AccessDeniedException("You do not have permission to access these reservations");
    }

    return reservationRepository.findByUserIdOrderByIdDesc(userId).stream()
        .map(
            reservation ->
                new AllReservationsByUserIdDto(
                    reservation.getId(),
                    reservation.getLieu().getFavorite_picture(),
                    reservation.getStart_date(),
                    reservation.getEnd_date(),
                    reservation.getReference(),
                    reservation.getLieu(),
                    reservation.getStatus()))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ReservationDto> getReservationsByLieuId(int lieuId, String token)
      throws AccessDeniedException {
    String emailFromToken = jwtUtils.getEmailFromJwtToken(token);
    List<String> rolesFromToken = jwtUtils.getRolesFromJwtToken(token);

    User connectedUser = userService.findByEmail(emailFromToken);

    if (!rolesFromToken.contains("ROLE_ADMIN")
        && connectedUser.getReservations().stream()
            .noneMatch(reservation -> reservation.getLieu().getId() == lieuId)) {
      throw new AccessDeniedException("You do not have permission to access these reservations");
    }

    List<ReservationDto> res =
        reservationRepository.findAllByLieuId(lieuId).stream()
            .map(reservation -> this.objectMapper.convertValue(reservation, ReservationDto.class))
            .collect(Collectors.toList());

    res.forEach(
        reservation -> {
          List<ReservationAttendeeDto> attendeesDto =
              reservation.attendees().stream()
                  .map(
                      reservationAttendee ->
                          new ReservationAttendeeDto(
                              reservationAttendee.attendeeId(),
                              reservationAttendee.name(),
                              reservationAttendee.firstname()))
                  .collect(Collectors.toList());
          attendeesDto.addAll(reservation.attendees());
        });
    return res;
  }

  @Transactional
  public ReservationDto updateReservationStatus(int id, ReservationStatus status) {
    var reservation =
        reservationRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ReservationNotFoundException(
                        String.format("Reservation with id %s not found", id)));

    reservation.setStatus(status.name());
    reservationRepository.save(reservation);

    return this.objectMapper.convertValue(reservation, ReservationDto.class);
  }
}
