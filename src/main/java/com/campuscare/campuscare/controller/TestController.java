package com.campuscare.campuscare.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public endpoint works!";
    }

    @GetMapping("/secured")
    public String securedEndpoint() {
        return "Secured endpoint works!";
    }
}