package com.locngo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locngo.dto.AttendeeDto;
import com.locngo.dto.SimpleReservationDto;
import com.locngo.entity.Attendees;
import com.locngo.repository.AttendeesRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AttendeesService {
  private final AttendeesRepository attendeesRepository;
  private final ObjectMapper objectMapper;

  public AttendeesService(ObjectMapper objectMapper, AttendeesRepository attendeesRepository) {
    this.objectMapper = objectMapper;
    this.attendeesRepository = attendeesRepository;
  }

  public AttendeeDto findById(int id) {
    var attendee = attendeesRepository.findById(id);
    var listReservationDto = new ArrayList<SimpleReservationDto>();
    attendee
        .getReservations()
        .forEach(
            reservation -> {
              var reservationDto =
                  new SimpleReservationDto(
                      reservation.getReservation().getId(),
                      reservation.getReservation().getStart_date(),
                      reservation.getReservation().getEnd_date(),
                      reservation.getReservation().getNb_person(),
                      reservation.getReservation().getReference());
              listReservationDto.add(reservationDto);
            });
    return new AttendeeDto(
        attendee.getId(), attendee.getName(), attendee.getFirstname(), listReservationDto);
  }

  public List<AttendeeDto> findAll() {
    var attendees = new ArrayList<AttendeeDto>();
    attendeesRepository
        .findAll()
        .forEach(
            attendee -> {
              var attendeeDto =
                  new AttendeeDto(
                      attendee.getId(), attendee.getName(), attendee.getFirstname(), null);
              attendees.add(attendeeDto);
            });
    return attendees;
  }

  public AttendeeDto createAttendees(Attendees attendees) {
    Attendees saved = attendeesRepository.save(attendees);
    return new AttendeeDto(saved.getId(), saved.getName(), saved.getFirstname(), null);
  }

  @Transactional
  public void deleteOrphans() {
    List<Attendees> orphans =
        attendeesRepository.findAll().stream()
            .filter(attendee -> attendee.getReservations().isEmpty())
            .collect(Collectors.toList());
    attendeesRepository.deleteAll(orphans);

    attendeesRepository.flush();
  }
}
