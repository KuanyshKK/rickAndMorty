package com.example.rickandmorty.util;

import lombok.experimental.UtilityClass;
import org.springframework.ui.Model;

@UtilityClass
public class Pagination {

    public static void addWindowedPagination(Model model, int current, int total) {
        int span = 2; // сколько страниц показывать слева/справа
        int start = Math.max(1, current - span);
        int end   = Math.min(total, current + span);

        // добиваем окно до 5 элементов, если возможно
        int desired = span * 2; // без учёта текущей
        while ((end - start) < desired && (start > 1 || end < total)) {
            if (start > 1) start--;
            if ((end - start) < desired && end < total) end++;
            else break;
        }

        model.addAttribute("startPage", start);
        model.addAttribute("endPage", end);
    }
}
