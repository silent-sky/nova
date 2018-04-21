package com.nova.saas.hr.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
	@RequestMapping("/")
    String home() {
        return "Hello Demo!";
    }
}


