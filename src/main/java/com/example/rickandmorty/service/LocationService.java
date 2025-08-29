package com.example.rickandmorty.service;

import com.example.rickandmorty.model.dto.PageResult;
import com.example.rickandmorty.model.record.LocationRow;
import com.example.rickandmorty.model.record.LocationView;
import com.rickandmortyapi.ApiException;

public interface LocationService {

    PageResult<LocationRow> getLocationsPage(int page, String name) throws ApiException;
    LocationView getLocation(int id) throws ApiException;
}
