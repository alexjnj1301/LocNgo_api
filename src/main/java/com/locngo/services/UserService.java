package com.locngo.services;

import com.locngo.constants.RoleConstants;
import com.locngo.dto.LoginRequest;
import com.locngo.dto.RegisterRequest;
import com.locngo.dto.UserRoleMappingDto;
import com.locngo.entity.User;
import com.locngo.entity.UserRoleMapping;
import com.locngo.exceptions.EmailAlreadyUsedException;
import com.locngo.exceptions.RoleNotFoundException;
import com.locngo.exceptions.UserNotFoundException;
import com.locngo.repository.RoleRepository;
import com.locngo.repository.UserRepository;
import com.locngo.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleMappingService userRoleMappingService;

    @Autowired
    private JwtUtils jwtUtils;

    public User register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new EmailAlreadyUsedException("Email is already in use");
        }

        var userRole = roleRepository.findByName(RoleConstants.ROLE_USER);
        if (userRole == null) {
            throw new RoleNotFoundException("Role not found");
        }

        var user = new User(registerRequest.id(), registerRequest.lastname(), registerRequest.firstname(),
                registerRequest.email(), registerRequest.phone(), passwordEncoder.encode(registerRequest.password()), null, new ArrayList<>());

        var savedUser = userRepository.save(user);

        var mapping = userRoleMappingService.createMapping(new UserRoleMappingDto(savedUser.getId(), userRole.getId()));

        var rolesList = new ArrayList<UserRoleMapping>();
        rolesList.add(mapping);

        return userRepository.save(new User(
                savedUser.getId(),
                savedUser.getLastname(),
                savedUser.getFirstname(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getPassword(),
                null,
                rolesList
        ));
    }

    public String login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtUtils.generateJwtToken(authentication);
    }

    public User findById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
}
