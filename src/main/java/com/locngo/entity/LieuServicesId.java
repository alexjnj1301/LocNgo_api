package com.locngo.entity;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Embeddable
public class LieuServicesId implements java.io.Serializable {
  private int lieuId;
  private int servicesId;

  public LieuServicesId() {}

  public LieuServicesId(int lieuId, int servicesId) {
    this.lieuId = lieuId;
    this.servicesId = servicesId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LieuServicesId that = (LieuServicesId) o;
    return lieuId == that.lieuId && servicesId == that.servicesId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(lieuId, servicesId);
  }
}
