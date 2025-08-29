package com.example.rickandmorty.model.dto;

import com.example.rickandmorty.model.record.EpisodeView;
import com.rickandmortyapi.Episode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class CharacterProfile {
    private int id;
    private String name;
    private String status;
    private String species;
    private String type;
    private String gender;
    private String image;
    private String originName;
    private String locationName;

    private List<EpisodeView> episodes = new ArrayList<>();
}
