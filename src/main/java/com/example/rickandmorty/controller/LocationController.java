package com.example.rickandmorty.controller;

import com.example.rickandmorty.model.dto.PageResult;
import com.example.rickandmorty.model.record.EpisodeDetail;
import com.example.rickandmorty.model.record.EpisodeView;
import com.example.rickandmorty.model.record.LocationRow;
import com.example.rickandmorty.model.record.LocationView;
import com.example.rickandmorty.service.LocationService;
import com.rickandmortyapi.ApiException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocationController {

    LocationService locationService;

    @GetMapping("/locations")
    public String locations(@RequestParam(name = "page", defaultValue = "1") int page,
                           @RequestParam(name = "name", required = false) String name,
                           Model model) throws ApiException {
        PageResult<LocationRow> lr = locationService.getLocationsPage(page, name);

        model.addAttribute("locations", lr.getItems());
        model.addAttribute("page", lr.getPage());
        model.addAttribute("totalPages", lr.getTotalPages());
        model.addAttribute("name", name);

        int startPage = Math.max(1, page - 2);
        int endPage = Math.min(lr.getTotalPages(), startPage + 4);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "locations";
    }

    @GetMapping("/location/{id}")
    public String location(@PathVariable int id, Model model) throws ApiException {
        LocationView loc = locationService.getLocation(id);
        model.addAttribute("loc", loc);
        return "location";
    }
}
