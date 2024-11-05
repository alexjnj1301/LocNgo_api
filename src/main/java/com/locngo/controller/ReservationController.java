package com.locngo.controller;

import com.locngo.dto.CreateReservation;
import com.locngo.dto.Reservation;
import com.locngo.dto.UpdateReservation;
import com.locngo.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable int id) {
        return reservationService.findById(id);
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.findAll();
    }

    @PostMapping
    public void createReservation(@RequestBody CreateReservation createReservationDto) {
        reservationService.createReservation(createReservationDto);
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable int id) {
        reservationService.deleteById(id);
    }

    @PutMapping("/{id}")
    public void updateReservation(@PathVariable int id, @RequestBody UpdateReservation reservation) {
        var updateReservation = new UpdateReservation(id, reservation.lieu(), reservation.start_date(), reservation.end_date(), reservation.nb_person(), reservation.attendees_id());
        reservationService.updateById(updateReservation);
    }
}
