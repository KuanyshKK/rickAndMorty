package com.example.rickandmorty.model.record;

public record CharacterCardView(
        int id,
        String name,
        String status,
        String species,
        String gender,
        String image,
        String locationName
) {}