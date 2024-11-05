package com.locngo.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
public class LieuService {
    @EmbeddedId
    private LieuServiceId id;

    @ManyToOne
    @MapsId("lieuId")
    private Lieu lieu;

    @ManyToOne
    @MapsId("serviceId")
    private Service service;

    private boolean isPresent;
}

@Setter
@Getter
@Embeddable
class LieuServiceId implements java.io.Serializable {
    private int lieuId;
    private int serviceId;

    public LieuServiceId() {}

    public LieuServiceId(int lieuId, int serviceId) {
        this.lieuId = lieuId;
        this.serviceId = serviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LieuServiceId that = (LieuServiceId) o;
        return lieuId == that.lieuId && serviceId == that.serviceId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lieuId, serviceId);
    }
}
