package com.locngo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.locngo.constants.RoleConstants;
import com.locngo.dto.LoginRequest;
import com.locngo.dto.RegisterRequest;
import com.locngo.dto.UserRoleMappingDto;
import com.locngo.entity.Role;
import com.locngo.entity.User;
import com.locngo.entity.UserRoleMapping;
import com.locngo.exceptions.EmailAlreadyUsedException;
import com.locngo.exceptions.RoleNotFoundException;
import com.locngo.exceptions.UserNotFoundException;
import com.locngo.repository.RoleRepository;
import com.locngo.repository.UserRepository;
import com.locngo.utils.JwtUtils;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest {

  @Mock private AuthenticationManager authenticationManager;
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private RoleRepository roleRepository;
  @Mock private UserRoleMappingService userRoleMappingService;
  @Mock private JwtUtils jwtUtils;

  @InjectMocks private UserService userService;

  private User user;
  private Role role;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user =
        new User(
            1,
            "Doe",
            "John",
            "john@example.com",
            "1234567890",
            "encodedPass",
            null,
            new ArrayList<>());
    role = new Role();
    role.setId(2);
    role.setName(RoleConstants.ROLE_USER);
  }

  @Test
  void testRegister_success() {
    RegisterRequest request =
        new RegisterRequest(0, "Doe", "John", "john@example.com", "1234567890", "password");

    when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
    when(roleRepository.findByName(RoleConstants.ROLE_USER)).thenReturn(role);
    when(passwordEncoder.encode("password")).thenReturn("encodedPass");
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userRoleMappingService.createMapping(any(UserRoleMappingDto.class)))
        .thenReturn(new UserRoleMapping(null, user, role));

    User result = userService.register(request);

    assertNotNull(result);
    assertEquals("john@example.com", result.getEmail());
    verify(userRepository, atLeastOnce()).save(any(User.class));
    verify(userRoleMappingService, times(1)).createMapping(any(UserRoleMappingDto.class));
  }

  @Test
  void testRegister_emailAlreadyUsed() {
    RegisterRequest request =
        new RegisterRequest(0, "Doe", "John", "john@example.com", "1234567890", "password");

    when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

    assertThrows(EmailAlreadyUsedException.class, () -> userService.register(request));

    verify(userRepository, times(1)).findByEmail(request.email());
    verify(roleRepository, never()).findByName(anyString());
  }

  @Test
  void testRegister_roleNotFound() {
    RegisterRequest request =
        new RegisterRequest(0, "Doe", "John", "john@example.com", "1234567890", "password");

    when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
    when(roleRepository.findByName(RoleConstants.ROLE_USER)).thenReturn(null);

    assertThrows(RoleNotFoundException.class, () -> userService.register(request));

    verify(roleRepository, times(1)).findByName(RoleConstants.ROLE_USER);
    verify(userRepository, never()).save(any());
  }

  @Test
  void testLogin_success() {
    LoginRequest loginRequest = new LoginRequest("john@example.com", "password");
    Authentication authentication = mock(Authentication.class);

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");

    String token = userService.login(loginRequest);

    assertEquals("jwt-token", token);
    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtUtils, times(1)).generateJwtToken(authentication);
  }

  @Test
  void testFindById_success() {
    when(userRepository.findById(1)).thenReturn(Optional.of(user));

    User result = userService.findById(1);

    assertNotNull(result);
    assertEquals(1, result.getId());
    verify(userRepository, times(1)).findById(1);
  }

  @Test
  void testFindById_notFound() {
    when(userRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.findById(99));

    verify(userRepository, times(1)).findById(99);
  }

  @Test
  void testFindByEmail_success() {
    when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

    User result = userService.findByEmail("john@example.com");

    assertNotNull(result);
    assertEquals("john@example.com", result.getEmail());
    verify(userRepository, times(1)).findByEmail("john@example.com");
  }

  @Test
  void testFindByEmail_notFound() {
    when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

    assertThrows(
        UserNotFoundException.class, () -> userService.findByEmail("notfound@example.com"));

    verify(userRepository, times(1)).findByEmail("notfound@example.com");
  }
}
