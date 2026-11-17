## MySQL Database Design

### Table: admin
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(50), Not Null, Unique
- password_hash: VARCHAR(255), Not Null
- email: VARCHAR(100), Not Null, Unique

### Table: patients
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(50), Not Null
- last_name: VARCHAR(50), Not Null
- date_of_birth: DATE, Not Null
- email: VARCHAR(100), Not Null, Unique
- phone: VARCHAR(20), Not Null

### Table: doctors
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(50), Not Null
- last_name: VARCHAR(50), Not Null
- specialty: VARCHAR(100), Not Null
- email: VARCHAR(100), Not Null, Unique

### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id)
- patient_id: INT, Foreign Key → patients(id)
- appointment_time: DATETIME, Not Null
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)

**Design Justifications & Constraints:**
* **Cascading Deletes:** If a patient is deleted, their appointments should either be soft-deleted (status changed to cancelled) or restricted from deletion to preserve historical medical records.
* **Overlapping Appointments:** A doctor should not have overlapping appointments. This is best validated at the application/service layer before inserting data into the database.
* **Validation:** Email and phone formats will be validated in the Java application layer (Spring Boot) before hitting the database, but we keep `UNIQUE` constraints here to prevent duplicate accounts.

---

## MongoDB Collection Design

### Collection: prescriptions

```json
{
  "_id": "ObjectId('64abc123456')",
  "patientId": 14,
  "doctorId": 3,
  "appointmentId": 51,
  "issueDate": "2023-10-25T14:30:00Z",
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Take 1 tablet every 6 hours"
    },
    {
      "name": "Amoxicillin",
      "dosage": "250mg",
      "frequency": "Take 1 capsule every 8 hours"
    }
  ],
  "doctorNotes": "Patient should drink plenty of fluids and rest.",
  "metadata": {
    "status": "active",
    "pharmacyNotified": false
  },
  "tags": ["painkiller", "antibiotic"]
}
