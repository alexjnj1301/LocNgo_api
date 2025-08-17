package com.locngo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LieuDto(
    int id,
    String name,
    String address,
    String city,
    String postal_code,
    String price,
    String description,
    String favorite_picture) {}
