package com.locngo.dto;

public record CreateLieuDto(int id, String name, String address, String city, String postal_code,
                            String price, String description, String favorite_picture) {
}
