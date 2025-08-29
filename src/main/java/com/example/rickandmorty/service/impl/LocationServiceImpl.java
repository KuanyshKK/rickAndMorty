package com.example.rickandmorty.service.impl;

import com.example.rickandmorty.model.dto.PageResult;
import com.example.rickandmorty.model.record.CharacterMini;
import com.example.rickandmorty.model.record.LocationRow;
import com.example.rickandmorty.model.record.LocationView;
import com.example.rickandmorty.service.LocationService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.rickandmortyapi.ApiException;
import com.rickandmortyapi.ApiRequest;
import com.rickandmortyapi.Location;
import com.rickandmortyapi.util.Jsons;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;

@Service
public class LocationServiceImpl implements LocationService {

    @Override
    public PageResult<LocationRow> getLocationsPage(int page, String name) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        if (page > 0) params.put("page", page);
        if (name != null && !name.isBlank()) params.put("name", name.trim());

        ApiRequest req = new ApiRequest("GET", "/location");
        req.setParameters(params);

        JsonObject root = req.execute();
        JsonObject info = root.getAsJsonObject("info");
        int count = info.get("count").getAsInt();
        int pages = info.get("pages").getAsInt();

        JsonArray results = root.getAsJsonArray("results");

        Type listType = new TypeToken<List<Location>>() {
        }.getType();
        List<Location> items = Jsons.provider().fromJson(results, listType);
        List<LocationRow> locations = items.stream().map(location ->
                new LocationRow(location.getId(), location.getName(), location.getType(), location.getDimension())).toList();
        return new PageResult<>(locations, page, pages, count);
    }

    @Override
    public LocationView getLocation(int id) throws ApiException {
        ApiRequest req = new ApiRequest("GET", "/location/" + id);
        JsonObject json = req.execute();

        int lid = json.get("id").getAsInt();
        String name = asString(json, "name");
        String type = asString(json, "type");
        String dimension = asString(json, "dimension");

        List<CharacterMini> characters = new ArrayList<>();
        if (json.has("residents") && json.get("residents").isJsonArray()) {
            JsonArray arr = json.getAsJsonArray("residents");
            Set<String> characterNames = new HashSet<>();
            arr.forEach(character -> {
                String asString = character.getAsString();
                characterNames.add(String.valueOf(asString.charAt(asString.length() - 1)));
            });
            String ids = String.join(",", characterNames);
            if (!ids.isBlank()) {
                ApiRequest cReq = new ApiRequest("GET", "/character/" + ids);
                if (characterNames.size() > 1) {
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
                if(characterNames.size() == 1) {
                    JsonObject characterJson = cReq.execute();

                    int chId = characterJson.get("id").getAsInt();
                    String chName = asString(characterJson, "name");
                    String chImage = asString(characterJson, "image");
                    String chStatus = asString(characterJson, "status");

                    characters.add(new CharacterMini(chId, chName, chImage, chStatus));
                }
            }
        }

        return new LocationView(lid, name, type, dimension, characters);
    }

    private static String asString(JsonObject o, String field) {
        if (o == null || !o.has(field)) return null;
        JsonElement el = o.get(field);
        return (el != null && !el.isJsonNull()) ? el.getAsString() : null;
    }
}
