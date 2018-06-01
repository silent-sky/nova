package com.nova.saas.hr.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@Slf4j
public class HomeController {
	@RequestMapping("/")
    String home() {
        return "Welcome to the PowerSaaS World!";
    }
}


