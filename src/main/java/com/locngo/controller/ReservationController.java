package com.locngo.controller;

import com.locngo.dto.CreateReservation;
import com.locngo.dto.ReservationDto;
import com.locngo.dto.UpdateReservation;
import com.locngo.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@PreAuthorize("hasRole(T(com.locngo.constants.RoleConstants).ROLE_USER)")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @GetMapping("/{id}")
    public ReservationDto getReservationById(@PathVariable int id) {
        return reservationService.findById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole(T(com.locngo.constants.RoleConstants).ROLE_ADMIN)")
    public List<ReservationDto> getAllReservations() {
        return reservationService.findAll();
    }

    @PostMapping
    public ReservationDto createReservation(@RequestBody CreateReservation createReservationDto, @RequestHeader("Authorization") String token) throws AccessDeniedException {
        token = token.substring(7);
        return reservationService.createReservation(createReservationDto, token);
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable int id) {
        reservationService.deleteById(id);
    }

    @PutMapping("/{id}")
    public void updateReservation(@PathVariable int id, @RequestBody UpdateReservation reservation) {
        var updateReservation = new UpdateReservation(id, reservation.lieu(), reservation.user(), reservation.start_date(), reservation.end_date(), reservation.nb_person(), reservation.attendees());
        reservationService.updateById(updateReservation);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationDto>> getReservationsByUserId(@PathVariable int userId, @RequestHeader("Authorization") String authorizationHeader) throws AccessDeniedException {
        var token = authorizationHeader.substring(7);
        List<ReservationDto> reservations = reservationService.getReservationsByUserId(userId, token);
        return ResponseEntity.ok(reservations);
    }
}
