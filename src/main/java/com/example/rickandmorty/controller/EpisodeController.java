package com.example.rickandmorty.controller;

import com.example.rickandmorty.model.dto.PageResult;
import com.example.rickandmorty.model.record.EpisodeDetail;
import com.example.rickandmorty.model.record.EpisodeView;
import com.example.rickandmorty.service.EpisodeService;
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
public class EpisodeController {

    EpisodeService episodeService;

    @GetMapping("/episodes")
    public String episodes(@RequestParam(name = "page", defaultValue = "1") int page,
                           @RequestParam(name = "name", required = false) String name,
                           Model model) throws ApiException {
        PageResult<EpisodeView> pr = episodeService.getEpisodesPage(page, name);

        model.addAttribute("episodes", pr.getItems());   // если у тебя getContent(), поменяй тут
        model.addAttribute("page", pr.getPage());
        model.addAttribute("totalPages", pr.getTotalPages());
        model.addAttribute("name", name);

        int startPage = Math.max(1, page - 2);
        int endPage = Math.min(pr.getTotalPages(), startPage + 4);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "episodes";
    }

    @GetMapping("/episode/{id}")
    public String episode(@PathVariable int id, Model model) throws ApiException {
        EpisodeDetail e = episodeService.getEpisode(id);
        model.addAttribute("e", e);
        return "episode";
    }
}
