import { savePrescription, getPrescription } from "./services/prescriptionServices.js";

document.addEventListener('DOMContentLoaded', async () => {
  const savePrescriptionBtn = document.getElementById("savePrescription");
  const patientNameInput = document.getElementById("patientName");
  const medicinesInput = document.getElementById("medicines");
  const dosageInput = document.getElementById("dosage");
  const notesInput = document.getElementById("notes");
  const heading = document.getElementById("heading")


  const urlParams = new URLSearchParams(window.location.search);
  const appointmentId = urlParams.get("appointmentId");
  const mode = urlParams.get("mode");
  const token = localStorage.getItem("token");
  const patientName = urlParams.get("patientName")

  if (heading) {
    if (mode === "view") {
      heading.innerHTML = `View <span>Prescription</span>`;
    } else {
      heading.innerHTML = `Add <span>Prescription</span>`;
    }
  }


  // Pre-fill patient name
  if (patientNameInput && patientName) {
    patientNameInput.value = patientName;
  }

  // Fetch and pre-fill existing prescription if it exists
  if (appointmentId && token) {
    try {
      const response = await getPrescription(appointmentId, token);
      console.log("getPrescription :: ", response);

      // Now, check if the prescription exists in the response and access it from the array
      if (response.prescription && response.prescription.length > 0) {
        const existingPrescription = response.prescription[0]; // Access first prescription object
        if (patientNameInput) patientNameInput.value = existingPrescription.patientName || patientName || "";
        if (medicinesInput) medicinesInput.value = existingPrescription.medication || "";
        if (dosageInput) dosageInput.value = existingPrescription.dosage || "";
        if (notesInput) notesInput.value = existingPrescription.doctorNotes || "";
      }

    } catch (error) {
      console.warn("No existing prescription found or failed to load:", error);
    }
  }
  if (mode === 'view') {
    // Make fields read-only
    if (patientNameInput) patientNameInput.disabled = true;
    if (medicinesInput) medicinesInput.disabled = true;
    if (dosageInput) dosageInput.disabled = true;
    if (notesInput) notesInput.disabled = true;
    if (savePrescriptionBtn) savePrescriptionBtn.style.display = "none";
  }
  // Save prescription on button click
  savePrescriptionBtn?.addEventListener('click', async (e) => {
    e.preventDefault();

    const prescription = {
      patientName: patientNameInput.value,
      medication: medicinesInput.value,
      dosage: dosageInput.value,
      doctorNotes: notesInput.value,
      appointmentId
    };

    const { success, message } = await savePrescription(prescription, token);

    if (success) {
      alert("✅ Prescription saved successfully.");
      selectRole('doctor');
    } else {
      alert("❌ Failed to save prescription. " + message);
    }
  });
});
