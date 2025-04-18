# ASP_Project
Advanced system programming Project.

## Features

- 🐋 Dockerized development environment
- ✅ Unit testing with Google Test (GTest)
- 🏗 CMake-based build system
- 📁 Organized project structure

## Project Structure
project-root/

├── build/ # Build artifacts (ignored)

├── include/ # Public headers

├── src/ # Source code

├── tests/ # Unit tests

├── CMakeLists.txt # Root CMake config

├── Dockerfile # Dockerfile for building the image

├── docker-compose.yml # Dev environment setup

└── README.md # This file


## Prerequisites

- Docker (with Docker Compose)
- Git
- (Optional) CMake and build tools if not using Docker

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Mro2903/ASP_Project.git
cd ASP_Project
```

### 2. Build and run with Docker
```bash
docker-compose up
```
This will:

- Build the development container

- Configure CMake

- Build the project

- Run all unit tests

### 3. Development workflow
After making changes:
```bash
# Rebuild and run tests
docker-compose run app /bin/bash -c "cmake .. && make && ./tests/run_tests"

# Or enter the container for interactive development:
docker-compose run app /bin/bash
```

## Testing
All tests are run automatically when you build with Docker. To run tests manually:
```bash
ctest --output-on-failure
```

## Running the application
To run the application, you can use the following command inside the Docker container:
```bash
# Run the main application
docker-compose run app /bin/bash -c "./main_app"

# Or enter the container for interactive development:
docker-compose run app /bin/bash
```