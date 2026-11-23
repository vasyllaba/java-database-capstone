/* adminDashboard.js */

import { openModal } from '../components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

document.addEventListener("DOMContentLoaded", () => {
    // 1. Load all doctor cards on page load
    loadDoctorCards();

    // 2. Attach a click listener to the "Add Doctor" button (using event delegation since it's dynamically rendered in header.js)
    document.body.addEventListener('click', (e) => {
        if (e.target && e.target.id === 'addDocBtn') {
            openModal('addDoctor');
        }
    });

    // 3. Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
    const searchBar = document.getElementById("searchBar");
    // Supporting both the lab instructions IDs and the HTML IDs we built earlier
    const timeFilter = document.getElementById("timeFilter") || document.getElementById("filterTime");
    const specialtyFilter = document.getElementById("specialtyFilter") || document.getElementById("filterSpecialty");

    if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
    if (timeFilter) timeFilter.addEventListener("change", filterDoctorsOnChange);
    if (specialtyFilter) specialtyFilter.addEventListener("change", filterDoctorsOnChange);
});

// Function: loadDoctorCards
async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error loading doctors:", error);
    }
}

// Function: renderDoctorCards
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
        contentDiv.innerHTML = "<p class='noPatientRecord'>No doctors found.</p>";
    }
}

// Function: filterDoctorsOnChange
async function filterDoctorsOnChange() {
    const searchBar = document.getElementById("searchBar");
    const timeFilter = document.getElementById("timeFilter") || document.getElementById("filterTime");
    const specialtyFilter = document.getElementById("specialtyFilter") || document.getElementById("filterSpecialty");

    const name = searchBar ? searchBar.value.trim() : null;
    const time = timeFilter && timeFilter.value ? timeFilter.value : null;
    const specialty = specialtyFilter && specialtyFilter.value ? specialtyFilter.value : null;

    try {
        const filteredDoctors = await filterDoctors(name, time, specialty);
        
        if (filteredDoctors && filteredDoctors.length > 0) {
            renderDoctorCards(filteredDoctors);
        } else {
            const contentDiv = document.getElementById("content");
            contentDiv.innerHTML = "<p class='noPatientRecord'>No doctors found with the given filters.</p>";
        }
    } catch (error) {
        console.error("Error filtering doctors:", error);
        alert("An error occurred while filtering doctors.");
    }
}

// Function: adminAddDoctor
// Making it globally accessible so the modal's inline "onsubmit" or "onclick" can call it
window.adminAddDoctor = async function(event) {
    if (event) event.preventDefault(); // Prevent form submission reload

    // Collect input values from the modal form
    const name = document.getElementById("docName")?.value;
    const email = document.getElementById("docEmail")?.value;
    const phone = document.getElementById("docPhone")?.value;
    const password = document.getElementById("docPassword")?.value;
    const specialty = document.getElementById("docSpecialty")?.value;

    // Collect checkbox values for available times
    const timeCheckboxes = document.querySelectorAll('input[name="docAvailability"]:checked');
    const availableTimes = Array.from(timeCheckboxes).map(cb => cb.value);

    // Retrieve the authentication token from localStorage
    const token = localStorage.getItem("token");
    if (!token) {
        alert("Session expired. Please log in again.");
        window.location.href = "/";
        return;
    }

    // Build a doctor object with the form values
    const doctor = {
        name,
        email,
        phone,
        password,
        specialty,
        availableTimes
    };

    try {
        // Call saveDoctor from the service
        const response = await saveDoctor(doctor, token);

        if (response.success) {
            alert(response.message || "Doctor added successfully!");
            
            // Close the modal
            const modal = document.getElementById("modal");
            if (modal) modal.style.display = "none";
            
            // Reload the doctor list to reflect the new addition
            loadDoctorCards();
        } else {
            alert(response.message || "Failed to add doctor.");
        }
    } catch (error) {
        console.error("Error saving doctor:", error);
        alert("An unexpected error occurred while adding the doctor.");
    }
};