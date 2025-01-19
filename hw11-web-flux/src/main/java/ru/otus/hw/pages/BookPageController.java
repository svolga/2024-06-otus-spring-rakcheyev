package ru.otus.hw.pages;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BookPageController {

    private final static String INDEX_FILE = "views/book/index";
    private final static String EDIT_FILE = "views/book/edit";

    @GetMapping("/")
    public String index() {
        return INDEX_FILE;
    }

    @GetMapping("/book/create")
    public String create() {
        return EDIT_FILE;
    }

    @GetMapping("/book/edit")
    public String editPage(@RequestParam("id") String id, Model model) {
        model.addAttribute("id", id);
        return EDIT_FILE;
    }
}
