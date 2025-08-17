package com.locngo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleMapping {
  @EmbeddedId private UserRoleMappingId id;

  @ManyToOne
  @MapsId("userId")
  @JsonIgnoreProperties({"roles", "users"})
  private User user;

  @ManyToOne
  @MapsId("roleId")
  @JsonIgnoreProperties({"users", "roles"})
  private Role role;
}
