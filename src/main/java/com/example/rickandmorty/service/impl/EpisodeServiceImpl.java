package com.example.rickandmorty.service.impl;

import com.example.rickandmorty.model.dto.PageResult;
import com.example.rickandmorty.model.record.CharacterMini;
import com.example.rickandmorty.model.record.EpisodeDetail;
import com.example.rickandmorty.model.record.EpisodeView;
import com.example.rickandmorty.service.EpisodeService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.rickandmortyapi.ApiException;
import com.rickandmortyapi.ApiRequest;
import com.rickandmortyapi.Episode;
import com.rickandmortyapi.util.Jsons;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;

@Service
public class EpisodeServiceImpl implements EpisodeService {

    @Override
    public PageResult<EpisodeView> getEpisodesPage(int page, String name) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        if (page > 0) params.put("page", page);
        if (name != null && !name.isBlank()) params.put("name", name.trim());

        ApiRequest req = new ApiRequest("GET", "/episode");
        req.setParameters(params);

        JsonObject root = req.execute();
        JsonObject info = root.getAsJsonObject("info");
        int count = info.get("count").getAsInt();
        int pages = info.get("pages").getAsInt();

        JsonArray results = root.getAsJsonArray("results");

        Type listType = new TypeToken<List<Episode>>() {}.getType();
        List<Episode> items = Jsons.provider().fromJson(results, listType);
        List<EpisodeView> episodes = items.stream().map(episode ->
                new EpisodeView(episode.getId(), episode.getName(), episode.getNumber(), episode.getShowDate().toString())).toList();
        return new PageResult<>(episodes, page, pages, count);

    }

    @Override
    public EpisodeDetail getEpisode(int id) throws ApiException {
        ApiRequest req = new ApiRequest("GET", "/episode/" + id);
        JsonObject json = req.execute();

        int eid = json.get("id").getAsInt();
        String name = asString(json, "name");
        String code = asString(json, "episode");
        String airDate = asString(json, "air_date");

        List<CharacterMini> characters = new ArrayList<>();
        if (json.has("characters") && json.get("characters").isJsonArray()) {
            JsonArray arr = json.getAsJsonArray("characters");
            Set<String> characterNames = new HashSet<>();
            arr.forEach(character -> {
                String asString = character.getAsString();
                characterNames.add(String.valueOf(asString.charAt(asString.length() - 1)));
            });
            String ids = String.join(",", characterNames);
            ApiRequest cReq = new ApiRequest("GET", "/character/" + ids);
            JsonArray characterArray = cReq.execute();
            for (JsonElement jsonElement : characterArray) {
                characters.add(new CharacterMini(
                        jsonElement.getAsJsonObject().get("id").getAsInt(),
                        jsonElement.getAsJsonObject().get("name").getAsString(),
                        jsonElement.getAsJsonObject().get("image").getAsString(),
                        jsonElement.getAsJsonObject().get("status").getAsString()
                ));
            }
        }

        return new EpisodeDetail(eid, name, code, airDate, characters);
    }

    private static String asString(JsonObject o, String field) {
        JsonElement el = o.get(field);
        return (el != null && !el.isJsonNull()) ? el.getAsString() : null;
    }
}
