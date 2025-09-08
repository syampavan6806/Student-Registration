package com.rushi.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        String clientIP = "";
        String serverIP = "";
        try {
            clientIP = request.getRemoteAddr();
            serverIP = request.getLocalAddr();
            logger.info("Rendering registration form for client IP: {} And server IP {}", clientIP, serverIP);
        } catch (Exception e) {
            logger.error("Exception while getting clinet and server IPS", e);
            logger.warn("Rendering registration form for client IP: {} And server IP {}", clientIP, serverIP);
        }
        model.addAttribute("clientIp", clientIP);
        model.addAttribute("serverIp", serverIP);
        return "registration";
    }
}
