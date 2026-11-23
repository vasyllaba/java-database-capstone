package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.back_end.services.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final Service service; 

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService,
                              Service service) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.service = service;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional
    public ResponseEntity> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existingOpt = appointmentRepository.findById(appointment.getId());
        if (existingOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Appointment existingAppointment = existingOpt.get();

        if (appointment.getPatient() != null && existingAppointment.getPatient() != null &&
            !existingAppointment.getPatient().getId().equals(appointment.getPatient().getId())) {
            response.put("message", "Unauthorized: Patient ID mismatch");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        int validationStatus = service.validateAppointment(appointment);
        if (validationStatus == -1) {
            response.put("message", "Invalid doctor ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else if (validationStatus == 0) {
            response.put("message", "Appointment slot is already booked or doctor is unavailable");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        try {
            existingAppointment.setAppointmentTime(appointment.getAppointmentTime());
            existingAppointment.setStatus(appointment.getStatus());
            if (appointment.getDoctor() != null) {
                existingAppointment.setDoctor(appointment.getDoctor());
            }
            appointmentRepository.save(existingAppointment);
            
            response.put("message", "Appointment updated successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Error updating appointment");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existingOpt = appointmentRepository.findById(id);
        if (existingOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Appointment appointment = existingOpt.get();

        String email = tokenService.extractEmail(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null || !appointment.getPatient().getId().equals(patient.getId())) {
            response.put("message", "Unauthorized to cancel this appointment");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        try {
            appointmentRepository.delete(appointment);
            response.put("message", "Appointment canceled successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Error canceling appointment");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> result = new HashMap<>();

        String email = tokenService.extractEmail(token);
        Doctor doctor = doctorRepository.findByEmail(email);

        if (doctor == null) {
            result.put("appointments", List.of());
            return result;
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> appointments;
        if (pname != null && !pname.trim().isEmpty() && !pname.equalsIgnoreCase("null")) {
            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                    doctor.getId(), pname, startOfDay, endOfDay);
        } else {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                    doctor.getId(), startOfDay, endOfDay);
        }

        result.put("appointments", appointments);
        return result;
    }

    @Transactional
    public void changeStatus(int status, long id) {
        appointmentRepository.updateStatus(status, id);
    }
}