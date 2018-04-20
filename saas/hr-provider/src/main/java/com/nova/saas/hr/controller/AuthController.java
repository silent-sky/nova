package com.nova.saas.hr.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * zhenghaibo
 * 2018/4/9 11:21
 */
@RestController
@RequestMapping("/paas/auth")
public class AuthController {
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping(String msg) {

        return msg;
    }

}
