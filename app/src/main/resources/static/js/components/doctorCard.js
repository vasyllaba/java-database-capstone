/* doctorCard.js */

import { showBookingOverlay } from '../loggedPatient.js';
import { deleteDoctor } from '../services/doctorServices.js';
import { getPatientData } from '../services/patientServices.js';

export function createDoctorCard(doctor) {
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    const role = localStorage.getItem("userRole");

    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    const name = document.createElement("h3");
    name.textContent = doctor.name;

    const specialization = document.createElement("p");
    specialization.textContent = "Specialty: " + doctor.specialty;

    const email = document.createElement("p");
    email.textContent = "Email: " + doctor.email;

    const availability = document.createElement("p");
    const times = doctor.availableTimes && doctor.availableTimes.length > 0 
                    ? doctor.availableTimes.join(", ") 
                    : "Not specified";
    availability.textContent = "Availability: " + times;

    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    // === ADMIN ROLE ACTIONS ===
    if (role === "admin") {
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";
        
        removeBtn.addEventListener("click", async () => {
            if (confirm("Are you sure you want to delete this doctor?")) {
                const token = localStorage.getItem("token");
                
                try {
                    const success = await deleteDoctor(doctor.id, token);
                    if (success) {
                        alert("Doctor deleted successfully.");
                        card.remove();
                    } else {
                        alert("Failed to delete doctor.");
                    }
                } catch (error) {
                    console.error("Error deleting doctor:", error);
                    alert("An error occurred while deleting the doctor.");
                }
            }
        });
        
        actionsDiv.appendChild(removeBtn);
    } 
    // === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
    else if (role === "patient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.addEventListener("click", () => {
            alert("Patient needs to login first.");
        });
        
        actionsDiv.appendChild(bookNow);
    } 
    // === LOGGED-IN PATIENT ROLE ACTIONS ===
    else if (role === "loggedPatient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.addEventListener("click", async (e) => {
            const token = localStorage.getItem("token");
            if (!token) {
                alert("Session expired. Please log in again.");
                window.location.href = "/";
                return;
            }
            
            try {
                const patientData = await getPatientData(token);
                if (!patientData) throw new Error("Patient details were not returned by the server.");
                showBookingOverlay(e, doctor, patientData);
            } catch (error) {
                console.error("Error fetching patient data:", error);
            }
        });
        
        actionsDiv.appendChild(bookNow);
    }

    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    return card;
}
