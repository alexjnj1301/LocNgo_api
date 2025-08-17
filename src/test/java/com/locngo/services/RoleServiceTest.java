package com.locngo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.locngo.entity.Role;
import com.locngo.exceptions.RoleNotFoundException;
import com.locngo.repository.RoleRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RoleServiceTest {

  @Mock private RoleRepository roleRepository;

  @InjectMocks private RoleService roleService;

  private Role role;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    role = new Role();
    role.setId(1);
    role.setName("ROLE_USER");
  }

  @Test
  void testGetAll_success() {
    when(roleRepository.findAll()).thenReturn(Collections.singletonList(role));

    List<Role> roles = roleService.getAll();

    assertEquals(1, roles.size());
    assertEquals("ROLE_USER", roles.getFirst().getName());
    verify(roleRepository, times(1)).findAll();
  }

  @Test
  void testGetById_success() {
    when(roleRepository.findById(1)).thenReturn(Optional.of(role));

    Role result = roleService.getById(1);

    assertNotNull(result);
    assertEquals(role.getId(), result.getId());
    assertEquals(role.getName(), result.getName());
    verify(roleRepository, times(1)).findById(1);
  }

  @Test
  void testGetById_notFound() {
    when(roleRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(RoleNotFoundException.class, () -> roleService.getById(99));
    verify(roleRepository, times(1)).findById(99);
  }

  @Test
  void testCreateRole_success() {
    when(roleRepository.save(role)).thenReturn(role);

    Role result = roleService.createRole(role);

    assertNotNull(result);
    assertEquals(role.getId(), result.getId());
    assertEquals(role.getName(), result.getName());
    verify(roleRepository, times(1)).save(role);
  }
}
