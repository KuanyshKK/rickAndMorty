package com.example.rickandmorty.service.impl;

import com.example.rickandmorty.model.dto.CharacterProfile;
import com.example.rickandmorty.model.dto.PageResult;
import com.example.rickandmorty.model.record.EpisodeView;
import com.example.rickandmorty.service.CharacterService;
import com.example.rickandmorty.service.OpenAIChatService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.rickandmortyapi.ApiException;
import com.rickandmortyapi.ApiRequest;
import com.rickandmortyapi.Character;
import com.rickandmortyapi.Episode;
import com.rickandmortyapi.util.Jsons;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CharacterServiceImpl implements CharacterService {

    OpenAIChatService openAIChatService;

    @Override
    public PageResult<Character> getCharactersPage(int page, String name) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        if (page > 0) params.put("page", page);
        if (name != null && !name.isBlank()) params.put("name", name.trim());

        ApiRequest req = new ApiRequest("GET", "/character");
        req.setParameters(params);

        JsonObject root = req.execute();
        JsonObject info = root.getAsJsonObject("info");
        int count = info.get("count").getAsInt();
        int pages = info.get("pages").getAsInt();

        JsonArray results = root.getAsJsonArray("results");

        Type listType = new TypeToken<List<Character>>() {
        }.getType();
        List<Character> items = Jsons.provider().fromJson(results, listType);
        // List<Character> items = new ArrayList<>(Jsons.asCollection(results, listType));

        return new PageResult<>(items, page, pages, count);
    }

    @Override
    public CharacterProfile getCharacterProfile(int id) {
        try {
            // 1) Читаем /character/{id}
            ApiRequest req = new ApiRequest("GET", "/character/" + id);
            JsonObject jsonObject = req.execute();

            String name = getCheckedName(jsonObject, "name");
            String status = getCheckedName(jsonObject, "status");
            String species = getCheckedName(jsonObject, "species");
            String type = getCheckedName(jsonObject, "type");
            String gender = getCheckedName(jsonObject, "gender");
            String image = getCheckedName(jsonObject, "image");

            String originName = nestedName(jsonObject, "origin");
            String locationName = nestedName(jsonObject, "location");

            // 2) Эпизоды: из URL берём id и тянем пачкой
            List<Integer> epIds = extractIdsFromUrls(jsonObject.getAsJsonArray("episode"));
            List<EpisodeView> episodes = new ArrayList<>();
            if (!epIds.isEmpty()) {
                Integer[] arr = epIds.toArray(new Integer[0]);
                Collection<Episode> eps = new Episode().get(arr); // /episode/1,2,3
                episodes = eps.stream().map(episode ->
                        new EpisodeView(episode.getId(), episode.getName(), episode.getNumber(), episode.getShowDate().toString())).toList();
            }

            return new CharacterProfile(id, name, status, species, type, gender, image, originName, locationName, episodes);
        } catch (Exception e) {
            // На сбой вернём заглушку (чтобы страница не падала)
            return new CharacterProfile(
                    id, "Unknown", "Unknown", "Unknown", "", "Unknown", "",
                    "Unknown", "Unknown", List.of()
            );
        }
    }

    @Override
    public String getMoreText(int id) throws ApiException {
        ApiRequest req = new ApiRequest("GET", "/character/" + id);
        JsonObject jsonObject = req.execute();

        String name = getCheckedName(jsonObject, "name");

        String question = "Get more information about " + name;

        return openAIChatService.getChatMessage(question);
    }

    private static String getCheckedName(JsonObject o, String key) {
        return (o.has(key) && !o.get(key).isJsonNull()) ? o.get(key).getAsString() : "";
    }

    private static String nestedName(JsonObject o, String objKey) {
        if (o.has(objKey) && o.get(objKey).isJsonObject()) {
            JsonObject n = o.getAsJsonObject(objKey);
            if (n.has("name") && !n.get("name").isJsonNull()) return n.get("name").getAsString();
        }
        return "";
    }

    private static List<Integer> extractIdsFromUrls(JsonArray arr) {
        List<Integer> ids = new ArrayList<>();
        if (arr == null) return ids;
        arr.forEach(el -> {
            String url = el.getAsString();            // .../episode/28
            int slash = url.lastIndexOf('/');
            if (slash > -1) {
                try {
                    ids.add(Integer.parseInt(url.substring(slash + 1)));
                } catch (NumberFormatException ignored) {
                }
            }
        });
        return ids;
    }

}
