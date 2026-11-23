package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private Service sharedService; 

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        Map<String, String> validationResult = sharedService.validateToken(token, "admin");
        
        if (validationResult.isEmpty()) {
            return "admin/adminDashboard";
        }
        
        return "redirect:/";
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        Map<String, String> validationResult = sharedService.validateToken(token, "doctor");
        
        if (validationResult.isEmpty()) {
            return "doctor/doctorDashboard";
        }
        
        return "redirect:/";
    }
}