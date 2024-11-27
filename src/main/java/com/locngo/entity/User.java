package com.locngo.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String lastname;
    private String firstname;
    private String email;
    private String phone;
    private String password;

    @OneToMany(mappedBy = "user")
    private List<UserRoleMapping> roles;
}
