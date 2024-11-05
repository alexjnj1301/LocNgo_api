package com.locngo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locngo.entity.Attendees;
import com.locngo.repository.AttendeesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttendeesService {
    @Autowired
    private AttendeesRepository attendeesRepository;

    public Attendees findById(int id) {
        return attendeesRepository.findById(id);
    }
}
