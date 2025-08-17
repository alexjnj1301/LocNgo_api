package com.locngo.dto;

public record RegisterRequest(
    int id, String firstname, String lastname, String email, String phone, String password) {}
