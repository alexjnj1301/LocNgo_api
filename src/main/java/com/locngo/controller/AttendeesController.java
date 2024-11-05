package com.locngo.controller;

import com.locngo.dto.AttendeeDto;
import com.locngo.entity.Attendees;
import com.locngo.services.AttendeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendees")
public class AttendeesController {
    @Autowired
    private AttendeesService attendeesService;

    @GetMapping("/{id}")
    public AttendeeDto getAttendeesById(@PathVariable int id) {
        return attendeesService.findById(id);
    }

    @GetMapping
    public List<AttendeeDto> getAllAttendees() {
        return attendeesService.findAll();
    }

    @PostMapping
    public AttendeeDto createAttendees(@RequestBody Attendees attendees) {
        return attendeesService.createAttendees(attendees);
    }
}
