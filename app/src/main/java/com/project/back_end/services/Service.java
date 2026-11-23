package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // 3. validateToken Method
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean isValid = tokenService.validateToken(token, user);
            if (!isValid) {
                response.put("message", "Invalid or expired token");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Token validation failed");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    // 4. validateAdmin Method
    public ResponseEntity<?> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (admin != null && admin.getPassword().equals(receivedAdmin.getPassword())) {
                String token = tokenService.generateToken(admin.getUsername());
                response.put("token", token);
                response.put("message", "Login successful");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            response.put("message", "Invalid username or password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Internal server error during admin login");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 5. filterDoctor Method
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        boolean hasName = name != null && !name.trim().isEmpty() && !name.equalsIgnoreCase("null");
        boolean hasSpecialty = specialty != null && !specialty.trim().isEmpty() && !specialty.equalsIgnoreCase("null");
        boolean hasTime = time != null && !time.trim().isEmpty() && !time.equalsIgnoreCase("null");

        if (hasName && hasSpecialty && hasTime) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        } else if (hasName && hasSpecialty) {
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        } else if (hasName && hasTime) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        } else if (hasSpecialty && hasTime) {
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        } else if (hasName) {
            return doctorService.findDoctorByName(name);
        } else if (hasSpecialty) {
            return doctorService.filterDoctorBySpecility(specialty);
        } else if (hasTime) {
            return doctorService.filterDoctorsByTime(time);
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("doctors", doctorService.getDoctors());
            return result;
        }
    }

    // 6. validateAppointment Method
    public int validateAppointment(Appointment appointment) {
        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            return -1;
        }

        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctor().getId());
        if (doctorOpt.isEmpty()) {
            return -1; // Doctor doesn't exist
        }

        List<String> availableSlots = doctorService.getDoctorAvailability(
                appointment.getDoctor().getId(),
                appointment.getAppointmentTime().toLocalDate()
        );

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String requestedTime = appointment.getAppointmentTime().toLocalTime().format(timeFormatter);

        boolean isSlotAvailable = availableSlots.stream()
                .anyMatch(slot -> slot.startsWith(requestedTime));

        return isSlotAvailable ? 1 : 0;
    }

    // 7. validatePatient Method
    public boolean validatePatient(Patient patient) {
        Patient existing = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        return existing == null; // true if does not exist, false if exists
    }

    // 8. validatePatientLogin Method
    public ResponseEntity<?> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getEmail());
            if (patient != null && patient.getPassword().equals(login.getPassword())) {
                String token = tokenService.generateToken(patient.getEmail());
                response.put("token", token);
                response.put("message", "Login successful");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            response.put("message", "Invalid email or password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Internal server error during patient login");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 9. filterPatient Method
    public ResponseEntity<?> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("message", "Patient not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            boolean hasCondition = condition != null && !condition.trim().isEmpty() && !condition.equalsIgnoreCase("null");
            boolean hasName = name != null && !name.trim().isEmpty() && !name.equalsIgnoreCase("null");

            if (hasCondition && hasName) {
                return patientService.filterByDoctorAndCondition(condition, name, patient.getId());
            } else if (hasCondition) {
                return patientService.filterByCondition(condition, patient.getId());
            } else if (hasName) {
                return patientService.filterByDoctor(name, patient.getId());
            } else {
                return patientService.getPatientAppointment(patient.getId(), token);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Error filtering patient appointments");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
