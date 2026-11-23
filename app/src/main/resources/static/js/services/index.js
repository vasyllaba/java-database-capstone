/* index.js */

import { openModal } from '../components/modals.js';
import { API_BASE_URL } from '../config/config.js';

const ADMIN_API = API_BASE_URL + '/admin/login';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

window.onload = function () {
    const adminBtn = document.getElementById('adminLogin');
    const doctorBtn = document.getElementById('doctorLogin');

    if (adminBtn) {
        adminBtn.addEventListener('click', () => {
            openModal('adminLogin');
        });
    }

    if (doctorBtn) {
        doctorBtn.addEventListener('click', () => {
            openModal('doctorLogin');
        });
    }
};

window.adminLoginHandler = async function() {
    const usernameInput = document.getElementById('adminUsername');
    const passwordInput = document.getElementById('adminPassword');
    
    if (!usernameInput || !passwordInput) return;

    const username = usernameInput.value;
    const password = passwordInput.value;
    
    const admin = { username, password };

    try {
        const response = await fetch(ADMIN_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(admin)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('token', data.token);
            
            if (typeof selectRole === 'function') {
                selectRole('admin');
            } else if (window.selectRole) {
                window.selectRole('admin');
            }
        } 
        else {
            alert("Invalid credentials!");
        }
    } catch (error) {
        console.error("Error during admin login:", error);
        alert("An unexpected error occurred during login.");
    }
};

window.doctorLoginHandler = async function() {
    const emailInput = document.getElementById('doctorEmail');
    const passwordInput = document.getElementById('doctorPassword');
    
    if (!emailInput || !passwordInput) return;

    const email = emailInput.value;
    const password = passwordInput.value;
    
    const doctor = { email, password };

    try {
        const response = await fetch(DOCTOR_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(doctor)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('token', data.token);
            
            if (typeof selectRole === 'function') {
                selectRole('doctor');
            } else if (window.selectRole) {
                window.selectRole('doctor');
            }
        } 
        else {
            alert("Invalid credentials!");
        }
    } catch (error) {
        console.error("Error during doctor login:", error);
        alert("An unexpected error occurred during login.");
    }
};