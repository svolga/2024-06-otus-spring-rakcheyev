package ru.otus.hw.pages;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class GenrePageController {

    private final static String INDEX_FILE = "views/genre/index";
    private final static String EDIT_FILE = "views/genre/edit";

    @GetMapping("/genre")
    public String listGenresPage() {
        return INDEX_FILE;
    }

    @GetMapping("/genre/create")
    public String create() {
        return EDIT_FILE;
    }

    @GetMapping("/genre/edit")
    public String editPage(@RequestParam("id") String id, Model model) {
        model.addAttribute("id", id);
        return EDIT_FILE;
    }
}