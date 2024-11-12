package com.galaxy13.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientController {

    @GetMapping("/")
    public String getIndexPage() {
        return "index.html";
    }

    @GetMapping("/client")
    public String getClientsPage() {
        return "client.html";
    }
}
