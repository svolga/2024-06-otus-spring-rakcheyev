package ru.otus.hw.pages;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthorPageController {

    private final static String INDEX_FILE = "views/author/index";
    private final static String EDIT_FILE = "views/author/edit";

    @GetMapping("/author")
    public String listAuthorsPage() {
        return INDEX_FILE;
    }

    @GetMapping("/author/create")
    public String create() {
        return EDIT_FILE;
    }

    @GetMapping("/author/edit")
    public String editPage(@RequestParam("id") String id, Model model) {
        model.addAttribute("id", id);
        return EDIT_FILE;
    }
}