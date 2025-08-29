package com.example.rickandmorty.model.record;

import java.util.List;

public record EpisodeDetail (
        int id,
        String name,
        String episode,
        String airDate,
        List<CharacterMini> characters
){
}
