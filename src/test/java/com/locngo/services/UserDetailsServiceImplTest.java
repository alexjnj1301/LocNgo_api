package com.locngo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.locngo.entity.Role;
import com.locngo.entity.User;
import com.locngo.entity.UserRoleMapping;
import com.locngo.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class UserDetailsServiceImplTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserDetailsServiceImpl userDetailsService;

  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Préparation d’un utilisateur avec un rôle
    Role role = new Role();
    role.setId(1);
    role.setName("ROLE_USER");

    user = new User();
    user.setId(1);
    user.setEmail("test@example.com");
    user.setPassword("password123");

    var userRoleMapping = new UserRoleMapping();
    userRoleMapping.setUser(user);
    userRoleMapping.setRole(role);

    user.setRoles(List.of(userRoleMapping));
  }

  @Test
  void testLoadUserByUsername_success() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

    UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

    assertNotNull(userDetails);
    assertEquals(user.getEmail(), userDetails.getUsername());
    assertEquals(user.getPassword(), userDetails.getPassword());
    assertTrue(
        userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

    verify(userRepository, times(1)).findByEmail("test@example.com");
  }

  @Test
  void testLoadUserByUsername_notFound() {
    when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class,
        () -> userDetailsService.loadUserByUsername("notfound@example.com"));

    verify(userRepository, times(1)).findByEmail("notfound@example.com");
  }
}
