package com.locngo.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
public class LieuServices {
    @EmbeddedId
    private LieuServicesId id;

    @ManyToOne
    @MapsId("lieuId")
    @JsonIgnore
    private Lieu lieu;

    @ManyToOne
    @MapsId("servicesId")
    @JsonIncludeProperties({"id", "name"})
    private Services services;
}
