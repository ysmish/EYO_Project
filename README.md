# ASP Project: Bloom Filter Application

## Overview
This project provides a **mailbox-like** application that uses a RESTful web server to manage user authentication, mailbox operations, and a Bloom Filter for URL blacklisting. The application is designed to efficiently check membership of URLs in a blacklist, ensuring that unwanted URLs are filtered out from mails.

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

- **Web Server**
  - Provides a RESTful API for interacting with the Bloom Filter.
  - Built using Node.js and Express.
  - Supports basic operations like adding, checking, and deleting URLs.

- **Unit Testing**  
  - Extensive tests using Google Test (`gtest`).
  - Tests cover initialization, persistence, main application flow, and more.

- **Dockerized Environment**  
  - Separate Docker Compose services for the application and tests.

---

## Project Structure

  - **web_server/**:  
    - `controllers/`: Contains the web server controllers.
    - `modles/`: Contains the data models.
    - `routes/`: Defines the routes for the web server.
    - `app.js`: Main entry point for the web server.
  

  - **url_server/**:  
    - `src/`: Main application logic.
    - `data/`: Persistent storage for the Bloom Filter.
    - `include/`: Header files.
    - `tests/`: Unit tests for the Bloom Filter and related components.

- **doc**
  - `doc/`: Documentation files.

- **Build Configuration**  
  - `CMakeLists.txt`: CMake build configuration.
  - `Dockerfile`: Docker image setup.
  - `package.json`: Node.js dependencies for the web server.
  - `package-lock.json`: Lock file for Node.js dependencies.

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

2. **Run the Application (URL Server Only)**:
   ```sh
   docker-compose run --service-ports url_server
   ```

3. **Run the URL Client (With The URL Server)**:
   ```sh
   docker-compose run url_client
   ```

4. **Run the URL Server Tests**:
   ```sh
   docker-compose run url_tests
   ```

5. **Run the Web Server**:
   ```sh
    docker-compose run --service-ports web_server
    ```
---

## Example Usage

![run example](images/run_example.png)

```bat
C:\Windows>curl -i -X POST http://127.0.0.1:3000/api/users -H "Content-Type: application/json" -d "{\"firstName\":\"Foo\",\"lastName\":\"Bar\",\"birthday\":\"2001-09-11\",\"username\":\"Foobar\",\"password\":\"1337secret\"}" 
HTTP/1.1 201 Created
X-Powered-By: Express
Location: api/users/Foobar
Content-Type: application/json; charset=utf-8
Content-Length: 39
ETag: W/"27-O/zF1M4EsSeqcgZOzAXUbc5OpUA"
Date: Tue, 03 Jun 2025 16:41:53 GMT
Connection: keep-alive
Keep-Alive: timeout=5

{"message":"User created successfully"}
C:\Windows>curl -i -X POST http://127.0.0.1:3000/api/users -H "Content-Type: application/json" -d "{\"firstName\":\"omri\",\"lastName\":\"bareket\",\"birthday\":\"2007-03-29\",\"username\":\"Mro\",\"password\":\"p455w0rd\"}" 
HTTP/1.1 201 Created
X-Powered-By: Express
Location: api/users/Mro
Content-Type: application/json; charset=utf-8
Content-Length: 39
ETag: W/"27-O/zF1M4EsSeqcgZOzAXUbc5OpUA"
Date: Tue, 03 Jun 2025 16:41:54 GMT
Connection: keep-alive
Keep-Alive: timeout=5

{"message":"User created successfully"}
C:\Windows>curl -i -X POST http://127.0.0.1:3000/api/tokens -H "Content-Type: application/json" -d "{\"username\":\"Foobar\",\"password\":\"1337secret\"}" 
HTTP/1.1 201 Created
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 18
ETag: W/"12-V59NTCvdeD/8jlB0BoV+f7Se0pg"
Date: Tue, 03 Jun 2025 16:41:54 GMT
Connection: keep-alive
Keep-Alive: timeout=5

{"token":"Foobar"}
C:\Windows>curl -i -X GET http://127.0.0.1:3000/api/users/Foobar 
HTTP/1.1 200 OK
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 145
ETag: W/"91-+ddMpJeulVdyvyavI6Nl+oloYVE"
Date: Tue, 03 Jun 2025 16:41:54 GMT
Connection: keep-alive
Keep-Alive: timeout=5

{"id":"Foobar","firstName":"Foo","lastName":"Bar","birthday":"2001-09-11","email":"Foobar@example.com","photo":"https://i.sstatic.net/frlIf.png"}
C:\Windows>curl -i -X GET http://127.0.0.1:3000/api/users/nonexistentuser 
HTTP/1.1 404 Not Found
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 53
ETag: W/"35-tHg+MCSy1KRnCyykxkoz01pbVG0"
Date: Tue, 03 Jun 2025 16:41:54 GMT
Connection: keep-alive
Keep-Alive: timeout=5

{"message":"User not found","error":"User not found"}
C:\Windows>curl -i -X POST http://127.0.0.1:3000/api/mails -H "Authorization: Foobar" -H "Content-Type: application/json" -d "{\"to\":\"Mro\",\"subject\":\"asdfasdfasdfasd\",\"body\":\"From Foobar to Mro\"}" 
HTTP/1.1 201 Created
X-Powered-By: Express
Location: /api/mails/1
Date: Tue, 03 Jun 2025 16:41:54 GMT
Connection: keep-alive
Keep-Alive: timeout=5
Content-Length: 0
C:\Windows>curl -i -X POST http://127.0.0.1:3000/api/mails -H "Authorization: nonexistentuser" -H "Content-Type: application/json" -d "{\"to\":\"Foobar\",\"subject\":\"Test\",\"body\":\"From nonexistentuser to Foobar\"}" 
HTTP/1.1 400 Bad Request
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 26
ETag: W/"1a-hq/hT0ORGTkTfyRpVCZ/JB/r8Eg"
Date: Tue, 03 Jun 2025 16:41:54 GMT
Connection: keep-alive
Keep-Alive: timeout=5

{"error":"User not found"}
C:\Windows>curl -i -X POST http://127.0.0.1:3000/api/mails -H "Authorization: Foobar" -H "Content-Type: application/json" -d "{\"to\":\"nonexistentuser\",\"subject\":\"Test\",\"body\":\"From Foobar to nonexistentuser\"}" 
HTTP/1.1 400 Bad Request
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 26
ETag: W/"1a-hq/hT0ORGTkTfyRpVCZ/JB/r8Eg"
Date: Tue, 03 Jun 2025 16:41:54 GMT
Connection: keep-alive
Keep-Alive: timeout=5

{"error":"User not found"}
C:\Windows>curl -i -X PATCH http://127.0.0.1:3000/api/mails/1 -H "Authorization: Foobar" -H "Content-Type: application/json" -d "{\"body\":\"Updated Body\"}" 
HTTP/1.1 204 No Content
X-Powered-By: Express
Location: /api/mails/1
Date: Tue, 03 Jun 2025 16:41:54 GMT
Connection: keep-alive
Keep-Alive: timeout=5


C:\Windows>curl -i -X GET http://127.0.0.1:3000/api/mails/1 -H "Authorization: Mro" 
HTTP/1.1 200 OK
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 150
ETag: W/"96-CsGLrGtLO+yd4DPdJn33F5ni9Lc"
Date: Tue, 03 Jun 2025 16:41:54 GMT
Connection: keep-alive
Keep-Alive: timeout=5

{"id":1,"from":"Foobar","to":"Mro","cc":[],"subject":"asdfasdfasdfasd","body":"From Foobar to Mro","date":"2025-06-03T16:41:54.307Z","attachments":[]}
C:\Windows>curl -i -X GET http://127.0.0.1:3000/api/search/T -H "Authorization: Foobar" 
HTTP/1.1 200 OK
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 296
ETag: W/"128-n1jNaUUNWPTEp8XZrJ5rXHyiZmE"
Date: Tue, 03 Jun 2025 16:41:54 GMT
Connection: keep-alive
Keep-Alive: timeout=5
[{"id":1,"from":"Foobar","to":"Mro","cc":[],"subject":"asdfasdfasdfasd","body":"Updated Body","date":"2025-06-03T16:41:54.307Z","attachments":[]}]

...
```

---

## Testing
The project includes unit tests for:
- Bloom Filter initialization and functionality.
- Persistence of blacklisted URLs.
- Handling of invalid inputs.

To run the tests:
```sh
docker-compose run url_tests
```
![tests run example](images/tests.png)
---

## Future Improvements
- Add UI for the web server like Gmail.
- Add a database for more better data management.

---

## Contributors
- *Omri Bareket*
- *Yuli Smishkis*
- *Eviatar Sayada*