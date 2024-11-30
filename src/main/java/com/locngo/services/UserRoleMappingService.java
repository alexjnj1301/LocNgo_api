package com.locngo.services;

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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleMappingService {
    @Autowired
    private UserRoleMappingRepository userRoleMappingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public UserRoleMapping createMapping(UserRoleMappingDto dto) {
        var user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + dto.userId()));
        var role = roleRepository.findById(dto.roleId())
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + dto.roleId()));

        var mappingId = new UserRoleMappingId(user.getId(), role.getId());

        var mapping = new UserRoleMapping(mappingId, user, role);

        return userRoleMappingRepository.save(mapping);
    }
}
