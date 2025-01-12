package com.locngo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locngo.dto.AttendeeDto;
import com.locngo.dto.ReservationDto;
import com.locngo.dto.SimpleReservationDto;
import com.locngo.entity.Attendees;
import com.locngo.repository.AttendeesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendeesService {
    @Autowired
    private AttendeesRepository attendeesRepository;
    private final ObjectMapper objectMapper;

    public AttendeesService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public AttendeeDto findById(int id) {
        var attendee = attendeesRepository.findById(id);
        var listReservationDto = new ArrayList<SimpleReservationDto>();
        attendee.getReservations().forEach(reservation -> {
            var reservationDto = new SimpleReservationDto(reservation.getReservation().getId(), reservation.getReservation().getStart_date(), reservation.getReservation().getEnd_date(), reservation.getReservation().getNb_person(), reservation.getReservation().getReference());
            listReservationDto.add(reservationDto);
        });
        return new AttendeeDto(attendee.getId(), attendee.getName(), attendee.getFirstname(), listReservationDto);
    }

    public List<AttendeeDto> findAll() {
        var attendees = new ArrayList<AttendeeDto>();
        attendeesRepository.findAll().forEach(attendee -> {
            var attendeeDto = new AttendeeDto(attendee.getId(), attendee.getName(), attendee.getFirstname(), null);
            attendees.add(attendeeDto);
        });
        return attendees;
    }

    public AttendeeDto createAttendees(Attendees attendees) {
        return this.objectMapper.convertValue(attendeesRepository.save(attendees), AttendeeDto.class);
    }

    @Transactional
    public void deleteOrphans() {
        List<Attendees> orphans = attendeesRepository.findAll().stream()
                .filter(attendee -> attendee.getReservations().isEmpty())
                .collect(Collectors.toList());
        attendeesRepository.deleteAll(orphans);

        attendeesRepository.flush();
    }
}
