/* patientDashboard.js */

import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';
import { patientSignup, patientLogin } from './services/patientServices.js';

document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();

    // Search and Filter Logic setup
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
    if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
    if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
});

async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Failed to load doctors:", error);
    }
}

async function filterDoctorsOnChange() {
    const searchBar = document.getElementById("searchBar")?.value.trim();
    const filterTime = document.getElementById("filterTime")?.value;
    const filterSpecialty = document.getElementById("filterSpecialty")?.value;

    const name = searchBar && searchBar.length > 0 ? searchBar : null;
    const time = filterTime && filterTime.length > 0 ? filterTime : null;
    const specialty = filterSpecialty && filterSpecialty.length > 0 ? filterSpecialty : null;

    try {
        const response = await filterDoctors(name, time, specialty);
        // Note: checking if response has a 'doctors' property, or if it is the array itself
        const doctors = response.doctors || response; 
        
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Failed to filter doctors:", error);
        alert("❌ An error occurred while filtering doctors.");
    }
}

function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) return;
    
    contentDiv.innerHTML = "";

    if (doctors && doctors.length > 0) {
        doctors.forEach(doctor => {
            const card = createDoctorCard(doctor);
            contentDiv.appendChild(card);
        });
    } else {
        contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
    }
}

window.signupPatient = async function (event) {
    if (event) event.preventDefault(); // Prevent form reload
    
    try {
        // Updated to use the correct HTML input IDs that are likely used in the modal
        const name = document.getElementById("patientSignupName")?.value || document.getElementById("name")?.value;
        const email = document.getElementById("patientSignupEmail")?.value || document.getElementById("email")?.value;
        const password = document.getElementById("patientSignupPassword")?.value || document.getElementById("password")?.value;
        const phone = document.getElementById("patientSignupPhone")?.value || document.getElementById("phone")?.value;
        const address = document.getElementById("patientSignupAddress")?.value || document.getElementById("address")?.value;

        const data = { name, email, password, phone, address };
        const { success, message } = await patientSignup(data);
        
        if (success) {
            alert(message || "Signup completed successfully!");
            const modal = document.getElementById("modal");
            if (modal) modal.style.display = "none";
            window.location.reload();
        } else {
            alert(message || "Signup failed.");
        }
    } catch (error) {
        console.error("Signup failed:", error);
        alert("❌ An error occurred while signing up.");
    }
};

window.loginPatient = async function (event) {
    if (event) event.preventDefault();
    
    try {
        const email = document.getElementById("patientLoginEmail")?.value || document.getElementById("email")?.value;
        const password = document.getElementById("patientLoginPassword")?.value || document.getElementById("password")?.value;

        const data = { email, password };
        const response = await patientLogin(data);
        
        if (response.ok) {
            const result = await response.json();

            localStorage.setItem('token', result.token);
            if (typeof window.selectRole === 'function') {
                window.selectRole('loggedPatient');
            } else {
                localStorage.setItem("userRole", "loggedPatient");
                window.location.href = '/pages/loggedPatientDashboard.html';
            }
        } else {
            alert('❌ Invalid credentials!');
        }
    } catch (error) {
        console.error("Error :: loginPatient :: ", error);
        alert("❌ Failed to Login. Please try again.");
    }
};
