package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // 1. Retrieve appointments for a specific doctor within a given time range (fetching doctor and availability)
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH d.availableTimes WHERE a.doctor.id = :doctorId AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId, 
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end
    );

    // 2. Filter appointments by doctor ID, partial patient name, and time range (fetching patient and doctor)
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.patient p LEFT JOIN FETCH a.doctor d WHERE a.doctor.id = :doctorId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%')) AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId, 
            @Param("patientName") String patientName, 
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end
    );

    // 3. Delete all appointments related to a specific doctor
    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);

    // 4. Find all appointments for a specific patient
    List<Appointment> findByPatientId(Long patientId);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.patient p JOIN FETCH a.doctor d WHERE p.id = :patientId AND d.email = :doctorEmail ORDER BY a.appointmentTime ASC")
    List<Appointment> findByPatientIdAndDoctorEmail(
            @Param("patientId") Long patientId,
            @Param("doctorEmail") String doctorEmail
    );

    // 5. Retrieve appointments for a patient by status, ordered by appointment time
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    // 6. Search appointments by partial doctor name and patient ID
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))")
    List<Appointment> filterByDoctorNameAndPatientId(
            @Param("doctorName") String doctorName, 
            @Param("patientId") Long patientId
    );

    // 7. Filter appointments by doctor name, patient ID, and status
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.status = :status AND LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(
            @Param("doctorName") String doctorName, 
            @Param("patientId") Long patientId, 
            @Param("status") int status
    );

    // 8. Update the status of a specific appointment based on its ID
    @Modifying
    @Transactional
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :id")
    void updateStatus(@Param("status") int status, @Param("id") long id);
}
