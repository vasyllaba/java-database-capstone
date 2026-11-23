/* doctorDashboard.js */

import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

let patientTableBody;
let selectedDate = new Date().toISOString().split('T')[0];
const token = localStorage.getItem('token');
let patientName = "null";

document.addEventListener("DOMContentLoaded", () => {
    patientTableBody = document.getElementById("patientTableBody");
    const searchBar = document.getElementById("searchBar");
    const todayButton = document.getElementById("todayButton");
    const datePicker = document.getElementById("datePicker");

    if (searchBar) {
        searchBar.addEventListener("input", (e) => {
            const value = e.target.value.trim();
            patientName = value !== "" ? value : "null";
            loadAppointments();
        });
    }

    if (todayButton) {
        todayButton.addEventListener("click", () => {
            selectedDate = new Date().toISOString().split('T')[0];
            if (datePicker) {
                datePicker.value = selectedDate;
            }
            loadAppointments();
        });
    }

    if (datePicker) {
        datePicker.addEventListener("change", (e) => {
            selectedDate = e.target.value;
            loadAppointments();
        });
    }

    if (typeof window.renderContent === 'function') {
        window.renderContent();
    }
    loadAppointments();
});

async function loadAppointments() {
    if (!patientTableBody) return;

    try {
        const appointments = await getAllAppointments(selectedDate, patientName, token);
        patientTableBody.innerHTML = "";

        if (!appointments || appointments.length === 0) {
            patientTableBody.innerHTML = `<tr><td colspan="5" class="noPatientRecord">No Appointments found for today.</td></tr>`;
            return;
        }

        appointments.forEach(appointment => {
            const patientData = {
                id: appointment.patientId || appointment.id,
                name: appointment.patientName,
                phone: appointment.patientPhone,
                email: appointment.patientEmail,
                appointmentId: appointment.id 
            };
            const row = createPatientRow(patientData);
            patientTableBody.appendChild(row);
        });

    } catch (error) {
        console.error("Error loading appointments:", error);
        patientTableBody.innerHTML = `<tr><td colspan="5" class="noPatientRecord">Error loading appointments. Try again later.</td></tr>`;
    }
}