package com.huawei.ibooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
public class IndexController {
    @RequestMapping("/")
    public String booking() {
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.setAttribute("user", null);
        return "index";
    }
}
