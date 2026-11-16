# User Story Template

**Title:**
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]


---
---
---

**As an admin, you can:**

Log into the portal with your username and password to manage the platform securely
Log out of the portal to protect system access
Add doctors to the portal
Delete doctor's profile from the portal
Run a stored procedure in MySQL CLI to get the number of appointments per month and track usage statistics


**Title:** Admin Login
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**
1. System successfully logs the admin in when valid credentials are provided.
2. System displays an error message for incorrect usernames or passwords.
3. Passwords must be masked during input.

**Priority:** High
**Story Points:** 2
**Notes:**
- Ensure session timeout is configured for security.

---

**Title:** Admin Logout
_As an admin, I want to log out of the portal, so that I can protect system access._

**Acceptance Criteria:**
1. Clicking the "Log out" button terminates the active session.
2. User is redirected to the login page.
3. Using the browser's back button does not allow access to admin pages after logout.

**Priority:** High
**Story Points:** 1
**Notes:**
- Clear any local session tokens upon logout.

---

**Title:** Add Doctor Profile
_As an admin, I want to add doctors to the portal, so that they can access the system and manage appointments._

**Acceptance Criteria:**
1. A form is available to enter the doctor's details (Name, Specialty, Email, etc.).
2. System validates that required fields are not empty.
3. A success message is displayed when the doctor is successfully added to the database.

**Priority:** High
**Story Points:** 3
**Notes:**
- A temporary password should be generated and sent to the new doctor's email.

---

**Title:** Delete Doctor Profile
_As an admin, I want to delete a doctor's profile from the portal, so that inactive staff no longer have system access._

**Acceptance Criteria:**
1. Admin can select a specific doctor profile to delete.
2. A confirmation prompt asks "Are you sure you want to delete this profile?" before action is taken.
3. The doctor's profile is successfully removed (or deactivated) in the MySQL database.

**Priority:** Medium
**Story Points:** 2
**Notes:**
- Consider implementing a "soft delete" so historical appointment records are not broken.

---

**Title:** Track Monthly Appointments via Stored Procedure
_As an admin, I want to run a stored procedure in MySQL CLI to get the number of appointments per month, so that I can track usage statistics._

**Acceptance Criteria:**
1. The stored procedure is successfully created in the MySQL database.
2. Running the procedure via CLI returns a table with two columns: Month and Total Appointments.
3. The query accurately counts appointments across different years/months.

**Priority:** Low
**Story Points:** 3
**Notes:**
- This is a backend/CLI task, so no UI components are required.

---
---
---

**As a patient, you can:**

View a list of doctors without logging in to explore options before registering
Sign up using your email and password to book appointments
Log into the portal to manage your bookings
Log out of the portal to secure your account
Log in and book an hour-long appointment to consult with a doctor
View my upcoming appointments so that I can prepare accordingly
Note: You need to add at least five user stories for this exercise.


**Title:** View Doctors List Unauthenticated
_As a patient, I want to view a list of doctors without logging in, so that I can explore my options before registering._

**Acceptance Criteria:**
1. A "Find a Doctor" page is accessible without an active session.
2. The page displays a list of doctors along with their specialties.
3. Attempting to book an appointment from this page prompts the user to log in or register.

**Priority:** Medium
**Story Points:** 2
**Notes:**
- Only basic information (name, specialty) should be visible to public users.

---

**Title:** Patient Sign Up
_As a patient, I want to sign up using my email and password, so that I can book appointments._

**Acceptance Criteria:**
1. A registration form requires a valid email, strong password, and basic personal details.
2. The system validates that the email is not already registered.
3. A success message is shown, and the user is redirected to the login page.

**Priority:** High
**Story Points:** 3
**Notes:**
- Passwords must be hashed before saving to the database.

---

**Title:** Patient Login
_As a patient, I want to log into the portal, so that I can manage my bookings._

**Acceptance Criteria:**
1. User can enter their registered email and password to log in.
2. An invalid login attempt shows a clear error message.
3. Upon successful login, the user is redirected to the patient dashboard.

**Priority:** High
**Story Points:** 2
**Notes:**
- Use JWT or secure sessions for authentication.

---

**Title:** Patient Logout
_As a patient, I want to log out of the portal, so that I can secure my account._

**Acceptance Criteria:**
1. A visible "Log out" button is available on the patient dashboard.
2. Clicking the button ends the session and redirects to the public home page.
3. Protected pages cannot be accessed after logging out.

**Priority:** High
**Story Points:** 1
**Notes:**
- Clear browser cookies/storage upon logout.

---

**Title:** Book Appointment
_As a patient, I want to log in and book an hour-long appointment, so that I can consult with a doctor._

**Acceptance Criteria:**
1. The booking interface displays available time slots for the selected doctor.
2. The system only allows booking of empty 1-hour slots.
3. A confirmation notification is displayed once the appointment is saved to the database.

**Priority:** High
**Story Points:** 5
**Notes:**
- Handle edge cases where two patients try to book the exact same slot simultaneously.

---

**Title:** View Upcoming Appointments
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._

**Acceptance Criteria:**
1. The dashboard displays a list of future appointments.
2. Each entry includes the doctor's name, date, time, and status.
3. Past appointments are filtered out or shown in a separate "History" tab.

**Priority:** Medium
**Story Points:** 2
**Notes:**
- Sort the list chronologically by default.



---
---
---

**As a doctor, you can:**

Log into the portal to manage your appointments
Log out of the portal to protect my data
View my appointment calendar to stay organized
Mark your unavailability to inform patients only the available slots
Update your profile with specialization and contact information so that patients have up-to-date information
View the patient details for upcoming appointments so that I can be prepared


**Title:** Doctor Login
_As a doctor, I want to log into the portal, so that I can manage my appointments._

**Acceptance Criteria:**
1. Doctor logs in using their credentials and is routed to the specific Doctor Dashboard.
2. The system verifies the user role is "Doctor" before granting access to doctor tools.
3. Incorrect credentials trigger an error message.

**Priority:** High
**Story Points:** 3
**Notes:**
- Share the login component with admins/patients, but route based on role.

---

**Title:** Doctor Logout
_As a doctor, I want to log out of the portal, so that I can protect my data._

**Acceptance Criteria:**
1. "Log out" button safely terminates the active session.
2. The user is redirected to the main login screen.
3. Patient medical data can no longer be accessed via the browser history.

**Priority:** High
**Story Points:** 1
**Notes:**
- Ensure strict caching rules so sensitive data isn't left in the browser cache.

---

**Title:** View Appointment Calendar
_As a doctor, I want to view my appointment calendar, so that I can stay organized._

**Acceptance Criteria:**
1. The dashboard provides a calendar or list view of the doctor's schedule.
2. Booked appointments are clearly highlighted with time and patient name.
3. The calendar updates to reflect the current day/week.

**Priority:** High
**Story Points:** 3
**Notes:**
- Consider integrating a UI library for the calendar component.

---

**Title:** Mark Unavailability
_As a doctor, I want to mark my unavailability, so that I inform patients of only the available slots._

**Acceptance Criteria:**
1. The doctor can select specific dates or time blocks to mark as "Unavailable."
2. These slots are immediately removed from the patient-facing booking screen.
3. The doctor cannot mark a slot unavailable if an appointment is already booked (unless they cancel it first).

**Priority:** High
**Story Points:** 4
**Notes:**
- Store unavailability ranges in the database and cross-reference during booking.

---

**Title:** Update Doctor Profile
_As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._

**Acceptance Criteria:**
1. An "Edit Profile" form allows updates to phone number, email, and specialization.
2. Changes are saved to the MySQL database.
3. The updated information is instantly reflected on the public "Find a Doctor" page.

**Priority:** Medium
**Story Points:** 2
**Notes:**
- Restrict changes to core identity fields (like Employee ID) if applicable.

---

**Title:** View Patient Details for Appointment
_As a doctor, I want to view the patient details for upcoming appointments, so that I can be prepared._

**Acceptance Criteria:**
1. Clicking on a scheduled appointment reveals the patient's basic info and past prescriptions.
2. Data is securely fetched (MySQL for profile, MongoDB for prescriptions).
3. The view is read-only unless the doctor explicitly adds a new prescription.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- This requires integration between the relational and document databases in the backend.


