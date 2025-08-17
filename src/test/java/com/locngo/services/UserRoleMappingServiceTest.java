package com.locngo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.locngo.dto.UserRoleMappingDto;
import com.locngo.entity.Role;
import com.locngo.entity.User;
import com.locngo.entity.UserRoleMapping;
import com.locngo.entity.UserRoleMappingId;
import com.locngo.exceptions.RoleNotFoundException;
import com.locngo.exceptions.UserNotFoundException;
import com.locngo.repository.RoleRepository;
import com.locngo.repository.UserRepository;
import com.locngo.repository.UserRoleMappingRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserRoleMappingServiceTest {

  @Mock private UserRoleMappingRepository userRoleMappingRepository;

  @Mock private UserRepository userRepository;

  @Mock private RoleRepository roleRepository;

  @InjectMocks private UserRoleMappingService userRoleMappingService;

  private User user;
  private Role role;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User();
    user.setId(1);
    user.setEmail("test@example.com");

    role = new Role();
    role.setId(2);
    role.setName("ROLE_USER");
  }

  @Test
  void testCreateMapping_success() {
    UserRoleMappingDto dto = new UserRoleMappingDto(user.getId(), role.getId());

    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));

    UserRoleMapping mapping =
        new UserRoleMapping(new UserRoleMappingId(user.getId(), role.getId()), user, role);

    when(userRoleMappingRepository.save(any(UserRoleMapping.class))).thenReturn(mapping);

    UserRoleMapping result = userRoleMappingService.createMapping(dto);

    assertNotNull(result);
    assertEquals(user.getId(), result.getUser().getId());
    assertEquals(role.getId(), result.getRole().getId());

    verify(userRepository, times(1)).findById(user.getId());
    verify(roleRepository, times(1)).findById(role.getId());
    verify(userRoleMappingRepository, times(1)).save(any(UserRoleMapping.class));
  }

  @Test
  void testCreateMapping_userNotFound() {
    UserRoleMappingDto dto = new UserRoleMappingDto(99, role.getId());

    when(userRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userRoleMappingService.createMapping(dto));

    verify(userRepository, times(1)).findById(99);
    verify(roleRepository, never()).findById(anyInt());
    verify(userRoleMappingRepository, never()).save(any());
  }

  @Test
  void testCreateMapping_roleNotFound() {
    UserRoleMappingDto dto = new UserRoleMappingDto(user.getId(), 88);

    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(roleRepository.findById(88)).thenReturn(Optional.empty());

    assertThrows(RoleNotFoundException.class, () -> userRoleMappingService.createMapping(dto));

    verify(userRepository, times(1)).findById(user.getId());
    verify(roleRepository, times(1)).findById(88);
    verify(userRoleMappingRepository, never()).save(any());
  }
}
