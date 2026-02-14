# EYO Project - Email System with Bloom Filter

A comprehensive email management system featuring URL blacklisting with Bloom Filter, built with modern microservices architecture.

## 📹 Video Of WebApp
https://github.com/user-attachments/assets/40164366-2e3c-4a53-9b73-117602cc5bad

## 📱 Video Of AndroidApp
https://github.com/user-attachments/assets/0c637f27-7773-4752-b7b4-f91f298d6f75



## 🚀 Quick Start

```bash
# Clone the repository
git clone https://github.com/ysmish/EYO_Project.git
cd EYO_Project

# Start the complete system
docker-compose run --service-ports web_app
```

## 📋 Complete Documentation

**👉 [View Complete Setup Guide and Documentation](wiki/README.md)**

The main documentation contains:
- 📦 **System Overview & Architecture**
- 🛠️ **Prerequisites & Environment Setup**
- 🏗️ **Building & Running with Docker**
- 👤 **User Registration & Authentication**
- 📧 **Email Management Operations**
- 🔒 **URL Server & Bloom Filter Usage**
- 📱 **Android App Usage**
- 🧪 **API Testing & Validation**
- 🔧 **Troubleshooting Guide**

## 🏗️ System Architecture

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

## 🎯 Key Features

- **🔒 URL Blacklisting** - Advanced Bloom Filter implementation for spam protection
- **📧 Email Management** - Full CRUD operations with labels and search
- **🌐 Multi-Platform** - Web app, Android app, and REST API
- **🐳 Docker Ready** - Complete containerization with Docker Compose
- **📱 Mobile First** - Native Android application with full feature parity
- **🔍 Real-time Search** - Fast email search and filtering
- **👤 User Management** - Registration, authentication, and profile management
- **📊 Spam Reporting** - Integrated spam detection and reporting system

## 🛠️ Technologies

| Component | Technology Stack |
|-----------|-----------------|
| **Backend API** | Node.js, Express.js, MongoDB |
| **Frontend Web** | React.js, CSS3, HTML5 |
| **Mobile App** | Android (Java/Kotlin) |
| **URL Server** | C++17, TCP Sockets, Bloom Filter |
| **Database** | MongoDB with Docker |
| **Containerization** | Docker, Docker Compose |
| **Testing** | Jest, C++ Unit Tests |

## 📚 Additional Resources

- **[Protocol Documentation](wiki/doc/protocol.md)** - TCP communication protocol for URL Server
- **[API Routes Documentation](wiki/doc/routes.md)** - Complete REST API reference
- **[Images Gallery](wiki/images/)** - Screenshots and visual guides

## 🚦 Service Endpoints

| Service | Port | Description |
|---------|------|-------------|
| Web App | 8080 | React frontend interface |
| Web Server | 3000 | Node.js REST API backend |
| URL Server | 12345 | C++ Bloom Filter server |
| MongoDB | 27017 | Database server |


## 👥 Contributors

- **Omri Bareket**
- **Yuli Smishkis**
- **Eviatar Sayada**

---

**📖 For detailed setup instructions, usage guides, and troubleshooting, please visit the [Complete Documentation](wiki/README.md).**
