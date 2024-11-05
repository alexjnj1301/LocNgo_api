package com.locngo.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String firstname;
    private String email;
    private String password;

    @OneToMany(mappedBy = "user")
    private List<UserRoleMapping> roles;

    // Getters and Setters
}
