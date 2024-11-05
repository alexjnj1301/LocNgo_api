package com.locngo.entity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Lieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String address;
    private String city;
    private String postalCode;

//    @JsonManagedReference
    @OneToMany(mappedBy = "lieu")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "lieu")
    private List<LieuImage> images;

    @OneToMany(mappedBy = "lieu")
    private List<LieuService> services;
}
