package com.nova.saas.hr.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
	@RequestMapping("/")
    String home() {
        return "Welcome to the PowerHr World!";
    }
}


