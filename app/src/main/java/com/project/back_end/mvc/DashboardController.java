package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private com.project.back_end.services.Service service; 

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        ResponseEntity> response = service.validateToken(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            return "admin/adminDashboard";
        }
        return "redirect:/";
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        ResponseEntity> response = service.validateToken(token, "doctor");
        if (response.getStatusCode().is2xxSuccessful()) {
            return "doctor/doctorDashboard";
        }
        return "redirect:/";
    }
}