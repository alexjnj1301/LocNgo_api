package com.locngo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locngo.dto.CreateReservation;
import com.locngo.dto.Reservation;
import com.locngo.dto.UpdateReservation;
import com.locngo.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public Reservation findById(int id) {
        var reservation = reservationRepository.findById(id);
        return this.objectMapper.convertValue(reservation, Reservation.class);
    }

    public List<Reservation> findAll() {
        var test = reservationRepository.findAll();
        var reservations = new ArrayList<Reservation>();
        test.forEach(reservation -> {
            reservations.add(this.objectMapper.convertValue(reservation, Reservation.class));
        });
        return reservations;
    }

    public void createReservation(CreateReservation createReservationDto) {
        var request = this.objectMapper.convertValue(createReservationDto, com.locngo.entity.Reservation.class);
        request.setNb_person(createReservationDto.attendees_id().size());
        var reservation = reservationRepository.save(request);

        createReservationDto.attendees_id().forEach(
                attendeeId -> reservationAttendeeService.createMapping(
                        reservation,
                        attendeesService.findById(attendeeId)
                )
        );
    }

    public void deleteById(int id) {
        reservationRepository.deleteById(id);
    }

    public void updateById(UpdateReservation reservation) {
        var request = this.objectMapper.convertValue(reservation, com.locngo.entity.Reservation.class);
        request.setNb_person(reservation.attendees_id().size());
        reservationRepository.save(request);

        reservationAttendeeService.deleteByReservationId(reservation.id());
        reservation.attendees_id().forEach(
                attendeeId -> reservationAttendeeService.createMapping(
                        request,
                        attendeesService.findById(attendeeId)
                )
        );
    }
}
