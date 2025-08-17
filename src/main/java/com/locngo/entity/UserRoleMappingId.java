package com.locngo.entity;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Embeddable
public class UserRoleMappingId implements java.io.Serializable {
  private int userId;
  private int roleId;

  public UserRoleMappingId() {}

  public UserRoleMappingId(int userId, int roleId) {
    this.userId = userId;
    this.roleId = roleId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserRoleMappingId that = (UserRoleMappingId) o;
    return userId == that.userId && roleId == that.roleId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, roleId);
  }
}
