package com.example.rickandmorty.service;

import com.example.rickandmorty.model.dto.PageResult;
import com.example.rickandmorty.model.record.EpisodeDetail;
import com.example.rickandmorty.model.record.EpisodeView;
import com.rickandmortyapi.ApiException;

public interface EpisodeService {
    PageResult<EpisodeView> getEpisodesPage(int page, String name) throws ApiException;
    EpisodeDetail getEpisode(int id) throws ApiException;
}
