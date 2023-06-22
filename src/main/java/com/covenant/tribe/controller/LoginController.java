package com.covenant.tribe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @RequestMapping("/sw-login")
    public String login() {
        return "login";
    }

}
