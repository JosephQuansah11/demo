// API Base URL
const API_BASE = '/api';

// Global state for intelligent polling
let clientVersion = 0;
let isPolling = false;
let pollInterval = null;
let isInitializationComplete = false;

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    checkDockerStatus();
    startIntelligentPolling();
    loadAllData();
    updatePollingStatus();
    
    // Set up periodic Docker status check (less frequent)
    setInterval(checkDockerStatus, 30000);
    
    // Set up periodic data refresh (less frequent when idle)
    setInterval(() => {
        if (isInitializationComplete) {
            loadAllData();
        }
    }, 30000);
});

// Docker Management Functions
async function checkDockerStatus() {
    try {
        const response = await fetch(`${API_BASE}/docker/status`);
        const data = await response.json();
        
        updateDockerStatus(data.dockerRunning, data.postgresRunning);
    } catch (error) {
        console.error('Error checking Docker status:', error);
        showMessage('Error checking Docker status', 'error');
    }
}

function updateDockerStatus(dockerRunning, postgresRunning) {
    const dockerIndicator = document.getElementById('docker-indicator');
    const dockerText = document.getElementById('docker-text');
    const postgresIndicator = document.getElementById('postgres-indicator');
    const postgresText = document.getElementById('postgres-text');
    const setupBtn = document.getElementById('setup-docker-btn');
    
    // Update Docker status
    dockerIndicator.className = `status-indicator ${dockerRunning ? 'status-running' : 'status-stopped'}`;
    dockerText.textContent = dockerRunning ? 'Running' : 'Stopped';
    
    // Update PostgreSQL status
    postgresIndicator.className = `status-indicator ${postgresRunning ? 'status-running' : 'status-stopped'}`;
    postgresText.textContent = postgresRunning ? 'Running' : 'Stopped';
    
    // Update setup button
    setupBtn.disabled = !dockerRunning || postgresRunning;
    setupBtn.textContent = postgresRunning ? 'PostgreSQL Already Running' : 
                          !dockerRunning ? 'Start Docker First' : 'Setup PostgreSQL';
}

async function setupDocker() {
    const setupBtn = document.getElementById('setup-docker-btn');
    const originalText = setupBtn.textContent;
    
    setupBtn.disabled = true;
    setupBtn.innerHTML = '<span class="loading"></span> Setting up...';
    
    try {
        const response = await fetch(`${API_BASE}/docker/setup`, {
            method: 'POST'
        });
        const data = await response.json();
        
        if (response.ok) {
            showMessage(data.message, 'success');
            setTimeout(checkDockerStatus, 2000);
        } else {
            showMessage(data.error || 'Setup failed', 'error');
        }
    } catch (error) {
        console.error('Error setting up Docker:', error);
        showMessage('Error setting up Docker environment', 'error');
    } finally {
        setupBtn.disabled = false;
        setupBtn.textContent = originalText;
    }
}

// Intelligent Polling System
function startIntelligentPolling() {
    if (isPolling) return;
    
    isPolling = true;
    console.log('Starting intelligent polling...');
    
    // Start with frequent polling
    pollForChanges();
}

function stopPolling() {
    if (pollInterval) {
        clearTimeout(pollInterval);
        pollInterval = null;
    }
    isPolling = false;
    console.log('Polling stopped - system is idle');
}

async function pollForChanges() {
    try {
        const [progressResponse, statusResponse] = await Promise.all([
            fetch(`${API_BASE}/data/progress?clientVersion=${clientVersion}`),
            fetch(`${API_BASE}/data/status`)
        ]);
        
        const progressData = await progressResponse.json();
        const statusData = await statusResponse.json();
        
        // Update client version if provided
        if (progressData.version !== undefined) {
            clientVersion = progressData.version;
        }
        
        // Update display only if there are changes
        if (progressData.hasChanges || progressData.progress) {
            updateProgressDisplay(progressData.progress || {}, statusData);
        }
        
        // Update initialization status
        isInitializationComplete = statusData.complete;
        
        // Determine next polling interval based on activity
        let nextPollDelay;
        if (progressData.shouldPoll === false || statusData.complete) {
            // System is idle or complete, poll less frequently or stop
            if (statusData.complete && !progressData.hasChanges) {
                stopPolling();
                return;
            }
            nextPollDelay = 10000; // 10 seconds when idle
        } else if (progressData.hasChanges) {
            nextPollDelay = 1000; // 1 second when active
        } else {
            nextPollDelay = 3000; // 3 seconds default
        }
        
        // Schedule next poll
        if (isPolling) {
            pollInterval = setTimeout(pollForChanges, nextPollDelay);
        }
        
    } catch (error) {
        console.error('Error in intelligent polling:', error);
        // Retry with exponential backoff on error
        if (isPolling) {
            pollInterval = setTimeout(pollForChanges, 5000);
        }
    }
}

function updateProgressDisplay(progressData, statusData) {
    // Update overall status
    const statusDiv = document.getElementById('initialization-status');
    if (statusData.complete) {
        statusDiv.innerHTML = '<div class="message success">✅ Data initialization completed successfully!</div>';
    } else {
        statusDiv.innerHTML = '<div class="message info">🔄 Data initialization in progress...</div>';
    }
    
    // Update individual progress
    updateEntityProgress('user', progressData.User, statusData.userCount);
    updateEntityProgress('church', progressData.Church, statusData.churchCount);
    updateEntityProgress('department', progressData.Department, statusData.departmentCount);
}

function updateEntityProgress(entityType, progressInfo, count) {
    const progressBar = document.getElementById(`${entityType}-progress`);
    const statusDiv = document.getElementById(`${entityType}-status`);
    
    if (progressInfo) {
        statusDiv.textContent = progressInfo.message;
        
        // Calculate progress percentage based on message
        let percentage = 0;
        if (progressInfo.message.includes('completed')) {
            percentage = 100;
        } else if (progressInfo.message.includes('/')) {
            const match = progressInfo.message.match(/(\d+)\/(\d+)/);
            if (match) {
                percentage = (parseInt(match[1]) / parseInt(match[2])) * 100;
            }
        } else if (progressInfo.message.includes('Starting')) {
            percentage = 10;
        }
        
        progressBar.style.width = percentage + '%';
    } else {
        statusDiv.textContent = `Ready (${count} records)`;
        progressBar.style.width = count > 0 ? '100%' : '0%';
    }
}

// Data Display Functions
function showTab(tabName) {
    // Hide all tabs
    const tabs = document.querySelectorAll('.tab');
    const tabContents = document.querySelectorAll('.tab-content');
    
    tabs.forEach(tab => tab.classList.remove('active'));
    tabContents.forEach(content => content.classList.remove('active'));
    
    // Show selected tab
    event.target.classList.add('active');
    document.getElementById(`${tabName}-tab`).classList.add('active');
    
    // Load data for the selected tab
    loadEntityData(tabName);
}

async function loadAllData() {
    await Promise.all([
        loadEntityData('users'),
        loadEntityData('churches'),
        loadEntityData('departments')
    ]);
}

async function loadEntityData(entityType) {
    try {
        const [dataResponse, countResponse] = await Promise.all([
            fetch(`${API_BASE}/data/${entityType}?size=20`),
            fetch(`${API_BASE}/data/${entityType}/count`)
        ]);
        
        const data = await dataResponse.json();
        const count = await countResponse.json();
        
        updateEntityTable(entityType, data.content || []);
        updateEntityCount(entityType, count);
    } catch (error) {
        console.error(`Error loading ${entityType} data:`, error);
    }
}

function updateEntityTable(entityType, data) {
    const tbody = document.getElementById(`${entityType}-tbody`);
    tbody.innerHTML = '';
    
    data.forEach(item => {
        const row = document.createElement('tr');
        
        switch(entityType) {
            case 'users':
                row.innerHTML = `
                    <td>${item.name}</td>
                    <td>${item.email}</td>
                    <td>${item.telephone}</td>
                    <td>${item.address?.city || 'N/A'}</td>
                    <td>${item.address?.country || 'N/A'}</td>
                `;
                break;
            case 'churches':
                row.innerHTML = `
                    <td>${item.name}</td>
                    <td>${item.denomination}</td>
                    <td>${item.pastor}</td>
                    <td>${item.address?.city || 'N/A'}</td>
                    <td>${item.capacity}</td>
                `;
                break;
            case 'departments':
                row.innerHTML = `
                    <td>${item.name}</td>
                    <td>${item.description}</td>
                    <td>${item.head}</td>
                    <td>${item.memberCount}</td>
                    <td>${item.budget}</td>
                `;
                break;
        }
        
        tbody.appendChild(row);
    });
}

function updateEntityCount(entityType, count) {
    const countElement = document.getElementById(`${entityType.slice(0, -1)}-count`);
    if (countElement) {
        countElement.textContent = count;
    }
}

// Utility Functions
function showMessage(message, type) {
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}`;
    messageDiv.textContent = message;
    
    // Insert at the top of main content
    const mainContent = document.querySelector('.main-content');
    mainContent.insertBefore(messageDiv, mainContent.firstChild);
    
    // Remove after 5 seconds
    setTimeout(() => {
        messageDiv.remove();
    }, 5000);
}

// Manual refresh functions
function forceRefreshProgress() {
    clientVersion = 0; // Reset version to force full update
    if (!isPolling) {
        startIntelligentPolling();
    } else {
        pollForChanges(); // Immediate poll
    }
}

function togglePolling() {
    if (isPolling) {
        stopPolling();
        showMessage('Real-time updates paused', 'info');
    } else {
        startIntelligentPolling();
        showMessage('Real-time updates resumed', 'success');
    }
    updatePollingStatus();
}

function updatePollingStatus() {
    const statusElement = document.getElementById('polling-status');
    if (statusElement) {
        statusElement.textContent = isPolling ? 'Active' : 'Paused';
        statusElement.className = `polling-status ${isPolling ? 'active' : 'paused'}`;
    }
}

// Test functions for intelligent polling demonstration
async function simulateChange() {
    try {
        const response = await fetch(`${API_BASE}/test/simulate-change?entityType=Test&message=Simulated change at ${new Date().toLocaleTimeString()}`, {
            method: 'POST'
        });
        const data = await response.json();
        showMessage(`Simulated change: ${data.updateMessage}`, 'info');
        
        // Restart polling if it was stopped
        if (!isPolling) {
            startIntelligentPolling();
        }
    } catch (error) {
        console.error('Error simulating change:', error);
        showMessage('Error simulating change', 'error');
    }
}

async function resetInitialization() {
    try {
        const response = await fetch(`${API_BASE}/test/reset-initialization`, {
            method: 'POST'
        });
        const data = await response.json();
        showMessage(data.message, 'info');
        
        // Reset client state and restart polling
        isInitializationComplete = false;
        clientVersion = 0;
        if (!isPolling) {
            startIntelligentPolling();
        }
    } catch (error) {
        console.error('Error resetting initialization:', error);
        showMessage('Error resetting initialization', 'error');
    }
}

async function completeInitialization() {
    try {
        const response = await fetch(`${API_BASE}/test/complete-initialization`, {
            method: 'POST'
        });
        const data = await response.json();
        showMessage(data.message, 'success');
        
        // This should cause polling to eventually stop
        isInitializationComplete = true;
    } catch (error) {
        console.error('Error completing initialization:', error);
        showMessage('Error completing initialization', 'error');
    }
}
