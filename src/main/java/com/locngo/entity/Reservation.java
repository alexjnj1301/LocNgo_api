package com.locngo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  @JoinColumn(name = "lieu_id", nullable = false)
  @JsonIgnoreProperties({"reservations", "images", "services"})
  private Lieu lieu;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIgnoreProperties({"reservations"})
  private User user;

  @Temporal(TemporalType.DATE)
  private Date start_date;

  @Temporal(TemporalType.DATE)
  private Date end_date;

  private int nb_person;

  private String reference;

  private String status;

  @OneToMany(
      mappedBy = "reservation",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @JsonIgnoreProperties("reservation")
  private List<ReservationAttendee> attendees;
}
