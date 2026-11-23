package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    // 2. Constructor Injection for Dependencies
    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 4. getDoctorAvailability Method
    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return Collections.emptyList();
        }

        Doctor doctor = doctorOpt.get();
        List<String> availableSlots = new ArrayList<>(doctor.getAvailableTimes());

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> bookedAppointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);

        // Extract booked times in "HH:mm" format to compare with available slots
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        Set<String> bookedTimes = bookedAppointments.stream()
                .map(app -> app.getAppointmentTime().toLocalTime().format(timeFormatter))
                .collect(Collectors.toSet());

        // Filter out slots that are already booked (checking if slot starts with the booked time, e.g., "09:00-10:00" starts with "09:00")
        return availableSlots.stream()
                .filter(slot -> bookedTimes.stream().noneMatch(slot::startsWith))
                .collect(Collectors.toList());
    }

    // 5. saveDoctor Method
    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            Doctor existingDoctor = doctorRepository.findByEmail(doctor.getEmail());
            if (existingDoctor != null) {
                return -1; // Conflict: doctor already exists
            }
            doctorRepository.save(doctor);
            return 1; // Success
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Internal error
        }
    }

    // 6. updateDoctor Method
    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            if (doctor.getId() == null || !doctorRepository.existsById(doctor.getId())) {
                return -1; // Doctor not found
            }
            doctorRepository.save(doctor);
            return 1; // Success
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Internal error
        }
    }

    // 7. getDoctors Method
    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        // Force initialization of lazy-loaded availableTimes collection within transaction
        doctors.forEach(d -> d.getAvailableTimes().size());
        return doctors;
    }

    // 8. deleteDoctor Method
    @Transactional
    public int deleteDoctor(long id) {
        try {
            if (!doctorRepository.existsById(id)) {
                return -1; // Doctor not found
            }
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1; // Success
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Internal error
        }
    }

    // 9. validateDoctor Method
    public ResponseEntity> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(login.getEmail());

        if (doctor != null && doctor.getPassword().equals(login.getPassword())) {
            String token = tokenService.generateToken(doctor.getEmail());
            response.put("token", token);
            response.put("message", "Login successful");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.put("message", "Invalid email or password");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // 10. findDoctorByName Method
    @Transactional(readOnly = true)
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        doctors.forEach(d -> d.getAvailableTimes().size()); // Eagerly load times
        result.put("doctors", doctors);
        return result;
    }

    // 11. filterDoctorsByNameSpecilityandTime Method
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        List<Doctor> filtered = filterDoctorByTime(doctors, amOrPm);
        result.put("doctors", filtered);
        return result;
    }

    // 12. filterDoctorByTime (Private helper method)
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (amOrPm == null || amOrPm.trim().isEmpty() || amOrPm.equalsIgnoreCase("null")) {
            doctors.forEach(d -> d.getAvailableTimes().size());
            return doctors;
        }

        boolean isAm = amOrPm.equalsIgnoreCase("AM");

        return doctors.stream().filter(doctor -> {
            List<String> times = doctor.getAvailableTimes();
            if (times == null || times.isEmpty()) return false;

            return times.stream().anyMatch(slot -> {
                try {
                    // Extract start hour from formats like "09:00-10:00" or "14:00"
                    String startHourStr = slot.split(":")[0].trim();
                    int hour = Integer.parseInt(startHourStr);
                    return isAm ? (hour < 12) : (hour >= 12);
                } catch (Exception e) {
                    // Fallback if slot explicitly contains "AM" or "PM" text
                    return slot.toUpperCase().contains(amOrPm.toUpperCase());
                }
            });
        }).collect(Collectors.toList());
    }

    // 13. filterDoctorByNameAndTime Method
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        List<Doctor> filtered = filterDoctorByTime(doctors, amOrPm);
        result.put("doctors", filtered);
        return result;
    }

    // 14. filterDoctorByNameAndSpecility Method
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specilty) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specilty);
        doctors.forEach(d -> d.getAvailableTimes().size());
        result.put("doctors", doctors);
        return result;
    }

    // 15. filterDoctorByTimeAndSpecility Method
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specilty, String amOrPm) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specilty);
        List<Doctor> filtered = filterDoctorByTime(doctors, amOrPm);
        result.put("doctors", filtered);
        return result;
    }

    // 16. filterDoctorBySpecility Method
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorBySpecility(String specilty) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specilty);
        doctors.forEach(d -> d.getAvailableTimes().size());
        result.put("doctors", doctors);
        return result;
    }

    // 17. filterDoctorsByTime Method
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findAll();
        List<Doctor> filtered = filterDoctorByTime(doctors, amOrPm);
        result.put("doctors", filtered);
        return result;
    }
}