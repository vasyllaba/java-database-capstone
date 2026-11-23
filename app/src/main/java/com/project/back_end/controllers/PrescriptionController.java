package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final com.project.back_end.services.Service service;
    private final AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService,
                                  com.project.back_end.services.Service service,
                                  AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/{token}")
    public ResponseEntity<?> savePrescription(
            @Valid @RequestBody Prescription prescription,
            @PathVariable String token) {

        ResponseEntity<?> tokenValidation = service.validateToken(token, "doctor");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        ResponseEntity<?> saveResponse = prescriptionService.savePrescription(prescription);
        
        if (saveResponse.getStatusCode().is2xxSuccessful() && prescription.getAppointmentId() != null) {
            appointmentService.changeStatus(1, prescription.getAppointmentId());
        }

        return saveResponse;
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        ResponseEntity<?> tokenValidation = service.validateToken(token, "doctor");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> errorResponse = new HashMap<>(tokenValidation.getBody());
            return new ResponseEntity<>(errorResponse, tokenValidation.getStatusCode());
        }

        return prescriptionService.getPrescription(appointmentId);
    }
}