package com.locngo.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Attendees {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String firstname;
    private String email;
    private String phone;
    private Date createdAt;

    @OneToMany(mappedBy = "attendee")
    @JsonIgnoreProperties("attendees")
    private List<ReservationAttendee> reservations;
}