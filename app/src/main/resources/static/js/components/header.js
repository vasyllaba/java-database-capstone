/* header.js */

function renderHeader() {
    const headerDiv = document.getElementById("header");
    if (!headerDiv) return; // Prevent error if headerDiv doesn't exist

    // Check if the current page is the Root Page (index.html)
    if (window.location.pathname.endsWith("/") || window.location.pathname.endsWith("/index.html")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
        headerDiv.innerHTML = `
            <header class="header">
                <div class="logo-section logo-link">
                    <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
                    <span class="logo-title">Hospital CMS</span>
                </div>
            </header>`;
        return;
    }

    // Retrieve User's Role and Token
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // Handle Session Expiry or Invalid Login
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    // Initialize Header Content
    let headerContent = `
        <header class="header">
            <div class="logo-section logo-link">
                <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
                <span class="logo-title">Hospital CMS</span>
            </div>
            <nav>`;

    // Add Role-Specific Header Content
    if (role === "admin") {
        headerContent += `
            <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
            <a href="#" onclick="logout()">Logout</a>`;
    } else if (role === "doctor") {
        headerContent += `
            <button class="adminBtn" onclick="window.location.href='/doctorDashboard'">Home</button>
            <a href="#" onclick="logout()">Logout</a>`;
    } else if (role === "patient") {
        headerContent += `
            <button id="patientLogin" class="adminBtn">Login</button>
            <button id="patientSignup" class="adminBtn">Sign Up</button>`;
    } else if (role === "loggedPatient") {
        headerContent += `
            <button id="home" class="adminBtn" onclick="window.location.href='/pages/patientDashboard.html'">Home</button>
            <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
            <a href="#" onclick="logoutPatient()">Logout</a>`;
    }

    // Close the Header Section
    headerContent += `
            </nav>
        </header>`;

    // Render the Header Content
    headerDiv.innerHTML = headerContent;

    // Attach Event Listeners
    attachHeaderButtonListeners();
}

// Helper Functions
function attachHeaderButtonListeners() {
    const loginBtn = document.getElementById("patientLogin");
    const signupBtn = document.getElementById("patientSignup");

    if (loginBtn) {
        loginBtn.addEventListener("click", () => {
            if (typeof window.openModal === "function") window.openModal("patientLogin");
        });
    }

    if (signupBtn) {
        signupBtn.addEventListener("click", () => {
            if (typeof window.openModal === "function") window.openModal("patientSignup");
        });
    }
}

function logout() {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    window.location.href = "/";
}

function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
}

// Render the Header when the file loads
renderHeader();
