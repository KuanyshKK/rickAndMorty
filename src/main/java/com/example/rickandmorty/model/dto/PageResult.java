package com.example.rickandmorty.model.dto;

import com.example.rickandmorty.model.record.EpisodeView;
import com.example.rickandmorty.model.record.LocationView;
import lombok.Getter;

import java.util.Collections;
import java.util.List;


@Getter
public class PageResult<T> {
    private final List<T> items;      // никогда не null
    private final int page;           // 1-based
    private final int totalPages;

    public PageResult(List<T> items, int page, int totalPages, int count) {
        this.items = (items == null) ? Collections.emptyList() : items;
        this.page = Math.max(1, page);
        this.totalPages = Math.max(1, totalPages);
    }

    /** То, что кладём в модель и рендерим в thymeleaf */
    public List<T> getContent() { return items; }

    public int getPage() { return page; }
    public int getTotalPages() { return totalPages; }
}
