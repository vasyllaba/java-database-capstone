import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = `${API_BASE_URL}/doctor`;

async function readResponse(response) {
  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(data.message || `Doctor request failed (${response.status})`);
  }
  return data;
}

export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);
    const data = await readResponse(response);
    return Array.isArray(data.doctors) ? data.doctors : [];
  } catch (error) {
    console.error("Failed to fetch doctors:", error);
    return [];
  }
}

export async function filterDoctors(name, time, specialty) {
  const path = [name, time, specialty]
    .map(value => value == null || value === "" ? "null" : encodeURIComponent(value))
    .join("/");

  const response = await fetch(`${DOCTOR_API}/filter/${path}`);
  return await readResponse(response);
}

export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${encodeURIComponent(token)}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor)
    });
    const data = await response.json().catch(() => ({}));
    return {
      success: response.ok,
      message: data.message || (response.ok ? "Doctor added successfully." : "Failed to add doctor.")
    };
  } catch (error) {
    console.error("Failed to save doctor:", error);
    return { success: false, message: error.message || "Network error." };
  }
}

export async function deleteDoctor(id, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${id}/${encodeURIComponent(token)}`, {
      method: "DELETE"
    });
    return response.ok;
  } catch (error) {
    console.error("Failed to delete doctor:", error);
    return false;
  }
}
