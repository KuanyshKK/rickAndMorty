package com.example.rickandmorty.service;

import com.example.rickandmorty.model.dto.CharacterProfile;
import com.example.rickandmorty.model.dto.PageResult;
import com.rickandmortyapi.ApiException;
import com.rickandmortyapi.Character;

public interface CharacterService {

    PageResult<Character> getCharactersPage(int page, String name) throws ApiException;

    CharacterProfile getCharacterProfile(int id);

    String getMoreText(int id) throws ApiException;
}
