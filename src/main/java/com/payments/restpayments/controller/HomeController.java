package com.payments.restpayments.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class HomeController {

    @GetMapping("/home.html")
    public ModelAndView showHomePage() {
        return new ModelAndView("home");
    }
}