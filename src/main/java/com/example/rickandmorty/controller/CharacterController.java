package com.example.rickandmorty.controller;

import com.example.rickandmorty.model.dto.CharacterProfile;
import com.example.rickandmorty.model.dto.PageResult;
import com.example.rickandmorty.service.CharacterService;
import com.rickandmortyapi.ApiException;
import com.rickandmortyapi.Character;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CharacterController {

    CharacterService characterService;

    @GetMapping({"/", "/characters"})
    public String characters(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(required = false) String name,
                             Model model) throws ApiException {

        PageResult<Character> pr = characterService.getCharactersPage(page, name);

        // данные для твоего index.html
        model.addAttribute("characters", pr.getContent());
        model.addAttribute("page", pr.getPage());
        model.addAttribute("totalPages", pr.getTotalPages());
        model.addAttribute("name", name);

        // окно пагинации (как в шаблоне)
        int startPage = Math.max(1, page - 2);
        int endPage = Math.min(pr.getTotalPages(), startPage + 4);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "index";
    }

    @GetMapping("/character/{id}")
    public String characterProfile(@PathVariable int id, Model model) throws ApiException {
        CharacterProfile ch = characterService.getCharacterProfile(id);
        model.addAttribute("ch", ch);

        return "character";
    }

    @GetMapping(value = "/character/{id}/more", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String characterMore(@PathVariable int id) throws ApiException {
        return characterService.getMoreText(id);
    }
}
