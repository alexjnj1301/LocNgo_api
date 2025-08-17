package com.locngo.dto;

import com.locngo.entity.User;

public record UserRegisterDto(User user, String token) {}
