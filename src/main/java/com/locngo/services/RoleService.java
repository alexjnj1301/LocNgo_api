package com.locngo.services;

import com.locngo.entity.Role;
import com.locngo.exceptions.RoleNotFoundException;
import com.locngo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    public Role getById(int id) {
        return roleRepository.findById(id).orElseThrow((() ->
                new RoleNotFoundException(String.format("Role with id %s not found", id))));
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }
}
