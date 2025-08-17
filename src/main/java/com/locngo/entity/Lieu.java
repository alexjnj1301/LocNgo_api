package com.locngo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lieu {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String name;
  private String address;
  private String city;
  private String postal_code;
  private String price;
  private String description;
  private String favorite_picture;
  private Double proprietor;

  @OneToMany(mappedBy = "lieu")
  @JsonIncludeProperties({"id", "nb_person", "start_date", "end_date", "reference"})
  private List<Reservation> reservations;

  @OneToMany(mappedBy = "lieu")
  @JsonIgnoreProperties({"lieu"})
  private List<LieuImage> images;

  @OneToMany(mappedBy = "lieu", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<LieuServices> services;
}
