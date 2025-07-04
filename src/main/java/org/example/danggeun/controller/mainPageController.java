package org.example.danggeun.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class mainPageController {

    @GetMapping("/")
    public String mainPage() {
        return "main";
    }
}
