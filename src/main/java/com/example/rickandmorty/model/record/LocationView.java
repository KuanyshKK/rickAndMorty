package com.example.rickandmorty.model.record;

import java.util.List;

public record LocationView(int id, String name, String type, String dimension,
                           List<CharacterMini> residents) {}