// appointmentRecordService.js
import { API_BASE_URL } from "../config/config.js";
import { getPatientAppointments, getPatientData } from "./patientServices.js";
const APPOINTMENT_API = `${API_BASE_URL}/appointments`;

export async function getAppointmentRecord() {
  const token = localStorage.getItem("token");
  if (!token) throw new Error("No patient session found.");

  const patient = await getPatientData(token);
  if (!patient?.id) throw new Error("Could not load patient details.");

  const appointments = await getPatientAppointments(patient.id, token, "patient");
  if (!Array.isArray(appointments)) throw new Error("Could not load appointment records.");
  return appointments;
}

//This is for the doctor to get all the patient Appointments
export async function getAllAppointments(date, patientName, token) {
  const response = await fetch(`${APPOINTMENT_API}/${encodeURIComponent(date)}/${encodeURIComponent(patientName || "null")}/${encodeURIComponent(token)}`);
  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(data.message || `Failed to fetch appointments (${response.status})`);
  }

  return Array.isArray(data.appointments) ? data.appointments : [];
}

export async function bookAppointment(appointment, token) {
  try {
    const response = await fetch(`${APPOINTMENT_API}/${token}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(appointment)
    });

    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Something went wrong"
    };
  } catch (error) {
    console.error("Error while booking appointment:", error);
    return {
      success: false,
      message: "Network error. Please try again later."
    };
  }
}

export async function updateAppointment(appointment, token) {
  try {
    const response = await fetch(`${APPOINTMENT_API}/${token}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(appointment)
    });

    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Something went wrong"
    };
  } catch (error) {
    console.error("Error while booking appointment:", error);
    return {
      success: false,
      message: "Network error. Please try again later."
    };
  }
}
