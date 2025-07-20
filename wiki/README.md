# Email System with Bloom Filter - Complete Setup Guide

This wiki provides comprehensive documentation for setting up and running the complete email system using Docker Compose. The system includes URL blacklisting with Bloom Filter, web interface, mobile app, and API backend.

## Table of Contents

1. [System Overview](#system-overview)
2. [Prerequisites](#prerequisites)
3. [Environment Setup](#environment-setup)
4. [Building and Running the Complete System](#building-and-running-the-complete-system)
5. [User Registration and Authentication](#user-registration-and-authentication)
6. [Email Management Operations](#email-management-operations)
7. [URL Server and Bloom Filter Usage](#url-server-and-bloom-filter-usage)
8. [Android App Usage](#android-app-usage)
9. [API Testing and Validation](#api-testing-and-validation)
10. [Troubleshooting](#troubleshooting)

---

## 📚 Documentation References

- **[Protocol Documentation](doc/protocol.md)** - TCP communication protocol for URL Server and Bloom Filter
- **[API Routes Documentation](doc/routes.md)** - Complete REST API reference for Web Server endpoints

---

## System Overview

The email system consists of multiple interconnected services:

- **URL Server** (Port 12345): C++ server with Bloom Filter for URL blacklisting
- **MongoDB** (Port 27017): Database for storing emails and user data
- **Web Server** (Port 3000): Node.js/Express backend API
- **Web App** (Port 8080): React frontend interface
- **Android App**: Native mobile client connecting to the API
- **URL Client**: Python client for testing URL server

### Architecture Diagram

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web App       │    │   Android App   │    │   URL Client    │
│   (React)       │    │   (Native)      │    │   (Python)      │
│   Port 8080     │    │                 │    │                 │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┘                      │
                                 │                      │
                    ┌─────────────▼─────────────┐       │
                    │      Web Server           │       │
                    │    (Node.js/Express)      │       │
                    │      Port 3000            │       │
                    └─────────┬───┬─────────────┘       │
                              │   │                     │
              ┌───────────────┘   └───────────────┐     │
              │                                   │     │
    ┌─────────▼─────────┐              ┌─────────▼─────▼─┐
    │     MongoDB       │              │    URL Server     │
    │     Port 27017    │              │   (Bloom Filter)  │
    │                   │              │    Port 12345     │
    └───────────────────┘              └───────────────────┘
```

---

## Prerequisites

Before setting up the system, ensure you have the following installed:

- **Docker** (version 20.0 or higher)
- **Docker Compose** (version 2.0 or higher)
- **Git** for cloning the repository
- **Android Studio** (for Android app development/testing)
- **Modern web browser** (Chrome, Firefox, Safari, or Edge)

### Verify Installation

```bash
# Check Docker version
docker --version

# Check Docker Compose version
docker-compose --version

# Verify Docker is running
docker info
```

**Expected Output:**
```
Docker version 24.0.0, build 1234567
Docker Compose version v2.20.0
```

---

## Environment Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd <project-directory>
```

### 2. Project Structure

```
project-root/
├── docker-compose.yml          # Main orchestration file
├── web_app/                    # React frontend
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── Dockerfile
├── web_server/                 # Node.js backend
│   ├── controllers/
│   ├── models/
│   ├── routes/
│   ├── app.js
│   └── Dockerfile
├── url_server/                 # C++ URL server
│   ├── src/
│   ├── include/
│   ├── tests/
│   └── Dockerfile
├── android_app/                # Android application
└── wiki/                       # This documentation
```

### 3. Environment Configuration

Create a `.env` file in the web_server directory:

```bash
cd web_server
cat > .env << EOF
JWT_SECRET=your_jwt_secret_key_here
DATABASE_URL="mongodb://admin:password@mongodb:27017/asp_project?authSource=admin"
PORT=3000
EOF
```

![Environment Configuration Setup](images/env.png)

---

## Building and Running the Complete System

### Step 1: Build All Docker Images

```bash
# Navigate to project root
cd /path/to/project

# Build all services
docker-compose build
```

**Expected Output:**
```
Building url_server...
Building web_server...
Building web_app...
Successfully built all services
```

![Docker Build Process](images/docker_build.png)

### Step 2: Start the Complete System

```bash
# Start all services in detached mode
docker-compose run -d --service-ports web_app
```

**Alternative: Start with logs visible**
```bash
# Start all services with logs
docker-compose run --service-ports web_app
```

**Running a specific service:**

1. ***Run the Application (URL Server Only)***:
   ```sh
   docker-compose run --service-ports url_server
   ```

2. ***Run the URL Client (With The URL Server)***:
   ```sh
   docker-compose run url_client
   ```

3. ***Run the URL Server Tests***:
   ```sh
   docker-compose run url_tests
   ```

4. ***Run the Web Server (Backend API)***:
   ```sh
    docker-compose run --service-ports web_server
    ```


**Expected Output:**
```
Creating network "project_default" with the default driver
Creating asp_mongodb ... done
Creating project_url_server_1 ... done
Creating project_web_server_1 ... done
Creating project_web_app_1 ... done
```

### Step 3: Verify All Services Are Running

```bash
# Check service status
docker-compose ps
```

**Expected Output:**
```
NAME                    COMMAND                  SERVICE      STATUS        PORTS
asp_mongodb             "docker-entrypoint.s…"   mongodb      Up 30 seconds 0.0.0.0:27017->27017/tcp
project_url_server_1    "./main_app"             url_server   Up 29 seconds 0.0.0.0:12345->12345/tcp
project_web_server_1    "npm start"              web_server   Up 28 seconds 0.0.0.0:3000->3000/tcp
project_web_app_1       "serve -s build -l 80…"  web_app      Up 27 seconds 0.0.0.0:8080->8080/tcp
```

### Step 4: Check Service Health

```bash
# Check web server health
curl http://localhost:3000/api/health

# Check URL server health
nc localhost 12345 <<< "GET www.example.com"

# Verify web app is accessible
curl http://localhost:8080
```

![Service Health Check](images/health.png)

---

## User Registration and Authentication

### Web Interface Registration

1. **Access the Web Application**
   - Open browser and navigate to `http://localhost:8080`
   - Click on "Register" or "Create Account"

![Web Login](images/login_page.png)

2. **Fill Registration Form**
   - First Name: `John`
   - Last Name: `Doe`
   - Birthday: `1990-01-01`
   - Username: `johndoe`
   - Password: `securepassword123`

![Web Registration Form](images/reg_page.png)

3. **Complete Registration**
   - Click "Register" button
   - Wait for success confirmation

![Web Registration Success](images/reg_good.png)

### Android App Registration

1. **Access the Android Application**
   - Launch the EYO_mail app on your Android device
   - Tap on "Register" or "Create Account"

![Android Registration Screen](images/login_andro.png)

2. **Fill Registration Form**
   - First Name: `John`
   - Last Name: `Doe`
   - Birthday: `1990-01-01`
   - Username: `johndoe`
   - Password: `securepassword123`

![Android Registration Form](images/reg_andro.png)

3. **Complete Registration**
   - Tap "Register" button
   - Wait for success confirmation

![Android Registration Success](images/reg_suc.png)

### API Registration (Alternative Method)

```bash
# Create first user via API
curl -i -X POST http://localhost:3000/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "birthday": "1990-01-01",
    "username": "johndoe",
    "password": "securepassword123"
  }'

# Create second user for testing
curl -i -X POST http://localhost:3000/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith",
    "birthday": "1992-05-15",
    "username": "janesmith",
    "password": "mypassword456"
  }'
```

**Expected Response:**
```
HTTP/1.1 201 Created
Location: api/users/johndoe
{"message":"User created successfully"}
```

### User Login

#### Web Interface Login

1. **Navigate to Login Page**
   - Go to `http://localhost:8080/login`
   - Enter username: `johndoe`
   - Enter password: `securepassword123`

![Web Login](images/login_page.png)

2. **Successful Login**
   - Click "Login" button
   - Redirected to dashboard

![Web Dashboard After Login](images/home_dashboard.png)

#### Android App Login

1. **Navigate to Login Screen**
   - Open the EYO_mail app
   - Tap "Login" if you already have an account
   - Enter username: `johndoe`
   - Enter password: `securepassword123`

![Android Login Screen](images/login_andro.png)

2. **Successful Login**
   - Tap "Login" button
   - Redirected to main dashboard

![Android Dashboard After Login](images/andro_dashboard.png)

#### API Login (Get Authentication Token)

```bash
# Login and get authentication token
curl -i -X POST http://localhost:3000/api/tokens \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "securepassword123"
  }'
```

**Expected Response:**
```
HTTP/1.1 201 Created
{"token":"token_value_here"}
```

**Important:** Save the token for authenticated API requests.

---

## Email Management Operations

### Composing and Sending Emails

#### Web Interface Email Composition

1. **Access Compose Interface**
   - Click "Compose" button in the dashboard
   - Fill in the email form:
     - To: `janesmith`
     - Subject: `Welcome to our email system`
     - Body: `Hello Jane, this is a test email from our new system.`

![Web Email Compose Interface](images/home_dashboard.png)

2. **Send Email**
   - Click "Send" button
   - Confirm email sent successfully

![Web Email Sent Confirmation](images/mail_sent.png)

#### Android App Email Composition

1. **Access Compose Interface**
   - Tap the floating action button "+" in the dashboard
   - Fill in the email form:
     - To: `janesmith`
     - Subject: `Welcome to our email system`
     - Body: `Hello Jane, this is a test email from our new system.`

![Android Email Compose Interface](images/andro_compose.png)

2. **Send Email**
   - Tap "Send" button
   - Confirm email sent successfully

![Android Email Sent Confirmation](images/andro_sent.png)

#### API Email Sending

```bash
# Send email via API (using authentication token)
curl -i -X POST http://localhost:3000/api/mails \
  -H "Authorization: johndoe" \
  -H "Content-Type: application/json" \
  -d '{
    "to": ["janesmith"],
    "cc": [],
    "subject": "API Test Email",
    "body": "This email was sent via the REST API"
  }'
```

**Expected Response:**
```
HTTP/1.1 201 Created
Location: /api/mails/1
{"message":"Mail sent successfully"}
```

### Reading Emails

#### Web Interface Email Reading

1. **Access Inbox**
   - Navigate to dashboard inbox
   - Click on any email to open

![Web Email List View](images/mail_list.png)

2. **View Email Details**
   - Read full email content
   - View sender, recipients, timestamp

![Web Email Detail View](images/mail_management.png)

#### Android App Email Reading

1. **Access Inbox**
   - Navigate to inbox tab in the app
   - Tap on any email to open

![Android Email List View](images/andro_mail_list.png)

2. **View Email Details**
   - Read full email content
   - View sender, recipients, timestamp
   - Options to reply, forward, delete

![Android Email Detail View](images/andro_mail.png)

#### API Email Retrieval

```bash
# Get all emails for user
curl -i -X GET http://localhost:3000/api/mails \
  -H "Authorization: janesmith"

# Get specific email by ID
curl -i -X GET http://localhost:3000/api/mails/1 \
  -H "Authorization: janesmith"
```

**Expected Response:**
```json
{
  "id": 1,
  "from": "johndoe",
  "to": "janesmith",
  "cc": [],
  "subject": "API Test Email",
  "body": "This email was sent via the REST API",
  "date": "2025-07-20T10:30:00.000Z",
  "attachments": []
}
```

### Editing Emails

#### API Email Update

```bash
# Update email body
curl -i -X PATCH http://localhost:3000/api/mails/1 \
  -H "Authorization: johndoe" \
  -H "Content-Type: application/json" \
  -d '{
    "body": "Updated email content with new information"
  }'
```

**Expected Response:**
```
HTTP/1.1 204 No Content
Location: /api/mails/1
```

### Deleting Emails

#### Web Interface Email Deletion

1. **Select Email**
   - Check the email in the inbox
   - Click "Delete" button

![Web Email Deletion Interface](images/delete.png)

2. **Confirm Deletion**
   - Confirm the deletion action
   - Email removed from inbox

![Web Delete Confirmation](images/no_mails.png)

#### Android App Email Deletion

1. **Select Email**
   - Long press on the email in the inbox or open email details
   - Tap "Delete" button/icon

![Android Email Deletion Interface](images/andro_del.png)

2. **Confirm Deletion**
   - Confirm the deletion action
   - Email removed from inbox

![Android Email Deletion Confirmation](images/andro_del_good.png)

#### API Email Deletion

```bash
# Delete specific email
curl -i -X DELETE http://localhost:3000/api/mails/1 \
  -H "Authorization: johndoe"
```

**Expected Response:**
```
HTTP/1.1 204 No Content
```

### Email Search

#### Web Interface Search

1. **Use Search Bar**
   - Enter search term in the search box
   - View filtered results

![Web Email Search Interface](images/home_dashboard.png)

#### Android App Search

1. **Use Search Bar**
   - Tap the search icon in the app
   - Enter search term in the search box
   - View filtered results

![Android Email Search Interface](images/andro_del_good.png)

#### API Email Search

```bash
# Search emails containing specific text
curl -i -X GET "http://localhost:3000/api/search/API" \
  -H "Authorization: johndoe"
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "from": "johndoe",
    "to": "janesmith",
    "subject": "API Test Email",
    "body": "This email was sent via the REST API",
    "date": "2025-07-20T10:30:00.000Z"
  }
]
```

### Label Management

#### Web Interface Label Operations

1. **Create Labels**
   - Navigate to "Labels" section in the dashboard
   - Click "Create New Label" button
   - Enter label details:
     - Name: `Important`
     - Color: Select color (e.g., red)

![Web Label Creation Interface](images/new_label.png)

2. **Attach Labels to Emails**
   - Open an email or select from inbox
   - Click "Add Label" button
   - Select existing labels or create new ones
   - Apply label to email

![Web Label Attachment Interface](images/attach_label.png)

#### Android App Label Operations

1. **Create Labels**
   - Navigate to hamburger menu → "Labels"
   - Tap "+" or "Add Label" button
   - Enter label details:
     - Name: `Important`
     - Color: Select color from palette

![Android Label Creation Interface](images/andro_label.png)

2. **Attach Labels to Emails**
   - Long press on an email in inbox or open email details
   - Tap "Labels" icon
   - Select existing labels or create new ones
   - Apply labels to email

![Android Label Attachment Interface](images/andro_attach.png)
#### API Label Operations

```bash
# Create a new label
curl -i -X POST http://localhost:3000/api/labels \
  -H "Authorization: johndoe" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Important",
    "color": "#ff0000"
  }'

# Get all labels
curl -i -X GET http://localhost:3000/api/labels \
  -H "Authorization: johndoe"

# Update label
curl -i -X PATCH http://localhost:3000/api/labels/1 \
  -H "Authorization: johndoe" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Very Important",
    "color": "#ff3333"
  }'
```

### User Profile Management

#### Web Interface Profile Operations

1. **Access User Settings**
   - Click on user avatar/profile button in top-right corner
   - Select "Profile Settings" or "Account Settings"

![Web User Settings Button](images/use_btn.png)

2. **Update Profile Information**
   - Edit first name, last name, birthday
   - Change username (if allowed)
   - Update password

![Web Profile Edit Dialog](images/capture.png)

#### Android App Profile Operations

1. **Access User Settings**
   - Tap hamburger menu or user avatar
   - Select "Profile" or "Account Settings"

![Android User Settings Button](images/andro_usr.png)
2. **Update Profile Information**
   - Tap "Edit Profile" button
   - Update personal information:
     - First Name, Last Name
     - Birthday
     - Username
   - Save changes

![Android Profile Edit Dialog](images/andro_img.png)

#### API Profile Operations

```bash
# Get user profile
curl -i -X GET http://localhost:3000/api/users/johndoe \
  -H "Authorization: johndoe"

# Update profile picture
curl -i -X POST http://localhost:3000/api/users/photo \
  -H "Authorization: johndoe" \
  -F "photo=@/path/to/profile.jpg"
```

### Spam Reporting

#### Web Interface Spam Reporting

1. **Report Spam Email**
   - Open the suspicious email
   - Click "Report Spam" button (usually near delete)
   - Confirm spam report in dialog box

![Web Spam Report Interface](images/delete.png)

2. **View Spam Reports**
   - Navigate to "Spam" folder to see reported emails
   - Admin panel may show spam statistics

![Web Spam Folder Interface](images/spam.png)

#### Android App Spam Reporting

1. **Report Spam Email**
   - Open the suspicious email or long press in inbox
   - Tap "More options" (⋮) menu
   - Select "Report Spam"

![Android Spam Report Interface](images/andro_spam.png)

2. **View Spam Reports**
   - Navigate to drawer menu → "Spam" folder
   - View all reported spam emails
   - Option to restore false positives

![Android Spam Folder Interface](images/andro_spam_folder.png)

#### API Spam Reporting

```bash
# Report an email as spam
curl -i -X POST http://localhost:3000/api/blacklist \
  -H "Authorization: johndoe" \
  -H "Content-Type: application/json" \
  -d '{
    "url": "http://spam-sender-domain.com",
    "emailId": 123,
    "reason": "spam"
  }'

# Get spam statistics
curl -i -X GET http://localhost:3000/api/blacklist \
  -H "Authorization: johndoe"
```

---

## URL Server and Bloom Filter Usage

The URL server provides Bloom Filter functionality for blacklisting URLs in emails using a custom TCP protocol.

**📋 Protocol Reference:** For detailed information about the communication protocol, message formats, and response codes, see [doc/protocol.md](doc/protocol.md).

### Testing URL Server

#### Using the URL Client

```bash
# Run the interactive URL client
docker-compose run url_client
```

**Interactive Commands:**
```
# Add URL to blacklist
POST http://malicious-site.com

# Check if URL is blacklisted
GET http://malicious-site.com

# Remove URL from blacklist
DELETE http://malicious-site.com
```

![Interactive URL Client Session](images/url_client.png)

#### Direct URL Server Testing

```bash
# Test URL server directly
echo "POST http://spam-site.com" | nc localhost 12345
echo "GET http://spam-site.com" | nc localhost 12345
```

### Running URL Server Tests

```bash
# Execute URL server unit tests
docker-compose run url_tests
```

**Expected Output:**
```
[==========] Running 15 tests from 3 test suites.
[----------] Global test environment set-up.
[----------] 5 tests from BloomFilterTest
[ RUN      ] BloomFilterTest.BasicFunctionality
[       OK ] BloomFilterTest.BasicFunctionality (0 ms)
...
[==========] 15 tests from 3 test suites ran. (45 ms total)
[  PASSED  ] 15 tests.
```

![URL Server Test Results](images/tests.png)

---

## Android App Usage

### Building and Installing the Android App

1. **Open Android Studio**
   - Open the `android_app` directory in Android Studio
   - Wait for Gradle sync to complete

![Android Studio Project](images/studio.png)

2. **Configure API Endpoint**
   - Edit `app/src/main/java/com/example/eyo/config/ApiConfig.java`
   - Set base URL to your server IP:
     ```java
     public static final String BASE_URL = "http://YOUR_IP:3000/api/";
     ```

3. **Build and Run**
   - Connect Android device or start emulator
   - Click "Run" button in Android Studio

![Android Studio Build and Run](images/andro_build.png)

### Android App Features

For detailed Android app usage including registration, login, email composition, reading, deletion, and advanced features, please refer to the corresponding Android sections in:

- [User Registration and Authentication](#user-registration-and-authentication) - Android App Registration
- [User Login](#user-login) - Android App Login  
- [Email Management Operations](#email-management-operations) - Android App Email Operations
  - Label Management - Create, attach, and organize emails with labels
  - User Profile Management - Profile settings, picture upload, account management
  - Spam Reporting - Report suspicious emails and manage spam folder

#### Additional Android-Specific Examples

**Quick Demo - Complete Android Workflow:**

1. **Register New User:**
   - Launch EYO_mail app
   - Tap "Create Account"
   - Fill form with sample data:
     - First Name: `Mobile`
     - Last Name: `User`  
     - Birthday: `1995-03-20`
     - Username: `mobileuser`
     - Password: `mobilepass123`
   - Tap "Register"

2. **Send Test Email:**
   - Tap floating action button "+"
   - Fill email form:
     - To: `johndoe`
     - Subject: `Mobile email test`  
     - Body: `This email was sent from the Android app`
   - Tap "Send"

3. **Advanced Features Demo:**

   **Label Management:**
   - Hamburger menu → "Labels" → "+" 
   - Create label: Name: `Work`, Color: Blue
   - Long press email → "Labels" → Apply "Work" label

   **Profile Management:**
   - Tap user avatar → "Profile"
   - Update profile picture from camera/gallery
   - Edit personal information and save

   **Spam Reporting:**
   - Open suspicious email → "⋮" menu → "Report Spam"
   - Confirm spam report → Email moved to spam folder

---

## API Testing and Validation

**📋 API Reference:** For complete documentation of all API routes, request/response formats, and authentication details, see [doc/routes.md](doc/routes.md).

### Complete API Workflow Test

```bash
# 1. Create users
echo "Creating users..."
curl -X POST http://localhost:3000/api/users \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User1","birthday":"1990-01-01","username":"testuser1","password":"pass123"}'

curl -X POST http://localhost:3000/api/users \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User2","birthday":"1991-02-02","username":"testuser2","password":"pass456"}'

# 2. Login and get tokens
echo "Getting authentication tokens..."
TOKEN1=$(curl -s -X POST http://localhost:3000/api/tokens \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser1","password":"pass123"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

TOKEN2=$(curl -s -X POST http://localhost:3000/api/tokens \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser2","password":"pass456"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 3. Send email
echo "Sending email..."
curl -X POST http://localhost:3000/api/mails \
  -H "Authorization: $TOKEN1" \
  -H "Content-Type: application/json" \
  -d '{"to":["testuser2"],"subject":"Test Email","body":"This is a test email"}'

# 4. Check received emails
echo "Checking received emails..."
curl -X GET http://localhost:3000/api/mails \
  -H "Authorization: $TOKEN2"

# 5. Search emails
echo "Searching emails..."
curl -X GET "http://localhost:3000/api/search/test" \
  -H "Authorization: $TOKEN2"
```

![API Test Results](images/run_example.png)

## Troubleshooting

### Common Issues and Solutions

#### 1. Services Not Starting

**Problem:** Docker containers fail to start

**Solution:**
```bash
# Check logs for specific service
docker-compose logs web_server
docker-compose logs url_server
docker-compose logs mongodb

# Restart services
docker-compose down
docker-compose run <specific_run_command>
```

#### 2. Port Conflicts

**Problem:** Ports already in use

**Solution:**
```bash
# Check what's using the ports
sudo netstat -tulpn | grep :3000
sudo netstat -tulpn | grep :8080
sudo netstat -tulpn | grep :12345

# Kill processes or change ports in docker-compose.yml
```

#### 3. Database Connection Issues

**Problem:** Web server can't connect to MongoDB

**Solution:**
```bash
# Check MongoDB logs
docker-compose logs mongodb

# Verify environment variables
docker-compose exec web_server env | grep DATABASE_URL

# Test direct connection
docker-compose exec web_server npm run test-db
```

#### 4. URL Server Communication

**Problem:** Web server can't reach URL server

**Solution:**
```bash
# Test URL server directly
docker-compose exec web_server telnet url_server 12345

# Check URL server logs
docker-compose logs url_server

# Verify network connectivity
docker network ls
docker network inspect project_default
```

#### 5. Android App Connection Issues

**Problem:** Android app can't connect to API

**Solution:**
1. Verify IP address in Android app configuration
2. Check firewall settings
3. Use `adb logcat` to view Android logs
4. Test API endpoint from browser on same network

### Service Status Monitoring

```bash
# Monitor all services
watch docker-compose ps

# Monitor logs in real-time
docker-compose logs -f

# Check resource usage
docker stats
```


---


## Conclusion

This wiki provides comprehensive documentation for setting up, running, and managing the complete email system with Bloom Filter URL blacklisting. The system demonstrates modern software architecture with microservices, containerization, and multiple client interfaces.


**Contributors:**
- Omri Bareket
- Yuli Smishkis  
- Eviatar Sayada