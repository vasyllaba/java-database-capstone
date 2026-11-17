## MySQL database design

### Table: `admin`
* `admin_id` (INT, PRIMARY KEY, AUTO_INCREMENT)
* `username` (VARCHAR(50), NOT NULL, UNIQUE)
* `password_hash` (VARCHAR(255), NOT NULL) - *Stored as a hash for security.*
* `email` (VARCHAR(100), NOT NULL, UNIQUE)

### Table: `doctors`
* `doctor_id` (INT, PRIMARY KEY, AUTO_INCREMENT)
* `first_name` (VARCHAR(50), NOT NULL)
* `last_name` (VARCHAR(50), NOT NULL)
* `specialty` (VARCHAR(100), NOT NULL)
* `email` (VARCHAR(100), NOT NULL, UNIQUE)
* `phone` (VARCHAR(20), NOT NULL)

### Table: `patients`
* `patient_id` (INT, PRIMARY KEY, AUTO_INCREMENT)
* `first_name` (VARCHAR(50), NOT NULL)
* `last_name` (VARCHAR(50), NOT NULL)
* `date_of_birth` (DATE, NOT NULL)
* `email` (VARCHAR(100), NOT NULL, UNIQUE)
* `phone` (VARCHAR(20), NOT NULL)

### Table: `appointments`
* `appointment_id` (INT, PRIMARY KEY, AUTO_INCREMENT)
* `doctor_id` (INT, NOT NULL, FOREIGN KEY referencing `doctors(doctor_id)`)
* `patient_id` (INT, NOT NULL, FOREIGN KEY referencing `patients(patient_id)`)
* `appointment_datetime` (DATETIME, NOT NULL)
* `status` (VARCHAR(20), NOT NULL, DEFAULT 'SCHEDULED') - *e.g., SCHEDULED, COMPLETED, CANCELLED.*

---

## 2. MongoDB Collection Design (Document Data)
We use MongoDB for `prescriptions` because medical prescriptions can vary greatly in structure. Some have one medication, others have many, and doctors may include varying amounts of unstructured notes. A document database easily handles these nested arrays and flexible schemas.

### Collection: `prescriptions`
This collection stores the medical prescriptions issued by doctors to patients. It references the MySQL `patient_id` and `doctor_id` to maintain a logical link across the dual-database architecture.

**Example Document (JSON):**
```json
{
  "_id": {
    "$oid": "64b8f0a2c9e77c001f8e4a11"
  },
  "patient_id": 105,
  "doctor_id": 12,
  "appointment_id": 402,
  "issue_date": "2023-10-25T14:30:00Z",
  "diagnosis": "Acute Bronchitis",
  "medications": [
    {
      "name": "Amoxicillin",
      "dosage": "500mg",
      "frequency": "Three times a day",
      "duration_days": 7
    },
    {
      "name": "Ibuprofen",
      "dosage": "400mg",
      "frequency": "As needed for pain",
      "duration_days": 5
    }
  ],
  "doctor_notes": "Patient advised to drink plenty of fluids and rest. Follow up in one week if symptoms persist."
}
