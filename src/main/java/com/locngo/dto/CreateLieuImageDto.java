package com.locngo.dto;

import com.locngo.entity.Lieu;

public record CreateLieuImageDto(int id, String url, Lieu lieu) {}
