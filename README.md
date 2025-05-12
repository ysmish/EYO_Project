# ASP Project: Bloom Filter Application

## Overview
This project provides a **Bloom Filter** api server written in C++ for efficient management and querying of blacklisted URLs. It supports inserting URLs, checking if a URL is blacklisted, deleting URLs, and persisting the blacklist across sessions. The application is designed to be efficient and scalable, making it suitable for various applications where URL filtering is required.

---

## Features
- **Bloom Filter Implementation**  
  - Efficient membership checking for URLs.
  - Utilizes multiple hash functions.
  - Persists blacklisted URLs to a file for future sessions.
  - communication with the server via a command-based interface.

- **Command-Based Interface**  
  - `GET <url>`: Check if a URL is blacklisted.
  - `POST <url>`: Add a URL to the blacklist.
  - `DELETE <url>`: Remove a URL from the blacklist.
  - for more details, see the [documentation](doc/protocol.md).

- **Persistence**  
  - Blacklisted URLs are stored in `data/bloom_filter_data.txt` and reloaded at startup.

- **Unit Testing**  
  - Extensive tests using Google Test (`gtest`).
  - Tests cover initialization, persistence, main application flow, and more.

- **Dockerized Environment**  
  - Separate Docker Compose services for the application and tests.

---

## Project Structure
- **Source Code**  
  - `src/`: Main application logic.
  - `include/`: Header files.

- **Tests**  
  - `tests/`: Unit tests for the Bloom Filter and related components.

- **Data**  
  - `data/`: Persistent storage for the Bloom Filter.

- **doc**
  - `doc/`: Documentation files.

- **Build Configuration**  
  - `CMakeLists.txt`: CMake build configuration.
  - `Dockerfile`: Docker image setup.

- **Docker Compose**  
  - `docker-compose.yml`: Defines services for running the app and tests.

- **Images**
  - `images/`: Contains example images for usage and testing.

---

## How to Build and Run

### Prerequisites
- Docker and Docker Compose installed.
- CMake (if building locally).

### Build and Run with Docker
1. **Build the Docker Images**:
   ```sh
   docker-compose build
   ```

2. **Run the Application (Server Only)**:
   ```sh
   docker-compose run app
   ```

3. **Run the Client (With The Server)**:
   ```sh
   docker-compose run client
   ```

4. **Run the Tests**:
   ```sh
   docker-compose run tests
   ```

---

## Example Usage

![run example](images/run_example.png)

### Input
```plaintext
POST http://example.com
GET http://example.com
GET http://example2.com
```

### Output
```plaintext
201 Created
200 OK

true true
false
```

---

## Testing
The project includes unit tests for:
- Bloom Filter initialization and functionality.
- Persistence of blacklisted URLs.
- Handling of invalid inputs.

To run the tests:
```sh
docker-compose run tests
```
![tests run example](images/tests.png)
---

## Future Improvements
- Handle multiple client connections.
- Implement a more sophisticated command parser.

---

## Contributors
- *Omri Bareket*
- *Yuli Smishkis*
- *Eviatar Sayada*