package com.example.oauth2.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("api")
public class ApiController {

    @GetMapping("list")
    public Object list() {
        return Collections.EMPTY_LIST;
    }
}
