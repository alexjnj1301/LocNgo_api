package com.locngo.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
public class UserRoleMapping {
    @EmbeddedId
    private UserRoleMappingId id;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @ManyToOne
    @MapsId("roleId")
    private Role role;
}

@Setter
@Getter
@Embeddable
class UserRoleMappingId implements java.io.Serializable {
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
