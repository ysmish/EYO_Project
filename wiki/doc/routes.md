# Web Server API Routes Documentation

This document provides comprehensive documentation for all API routes available in the web server backend.

## Base URL
All API routes are prefixed with `/api/`

**Example:** `http://localhost:3000/api/users`

---

## Authentication
Most routes require authentication using a token obtained from the login endpoint.

**Authentication Header:**
```
Authorization: <username>
```

---

## Users API

### Create User
**Endpoint:** `POST /api/users`  
**Authentication:** None required  
**Description:** Register a new user account

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe", 
  "birthday": "1990-01-01",
  "username": "johndoe",
  "password": "securepassword123",
  "photo": "base64_encoded_image_optional"
}
```

**Response:**
- **201 Created:** User created successfully
  ```json
  {"message": "User created successfully"}
  ```
  - Headers: `Location: api/users/<user_id>`

- **400 Bad Request:** Missing required fields
- **409 Conflict:** Username already exists

---

### Get User by ID
**Endpoint:** `GET /api/users/:id`  
**Authentication:** Required  
**Description:** Retrieve user information by user ID

**Response:**
- **200 OK:** User data retrieved
  ```json
  {
    "id": "user_id",
    "firstName": "John",
    "lastName": "Doe",
    "birthday": "1990-01-01",
    "username": "johndoe",
    "photo": "base64_encoded_image"
  }
  ```

- **404 Not Found:** User not found
- **401 Unauthorized:** Invalid authentication

---

### Change User Photo
**Endpoint:** `POST /api/users/photo`  
**Authentication:** Required  
**Description:** Upload/change user profile photo

**Request:** Multipart form data with `photo` field

**Response:**
- **200 OK:** Photo updated successfully
- **400 Bad Request:** Invalid photo format
- **401 Unauthorized:** Invalid authentication

---

## Authentication API

### Create Token (Login)
**Endpoint:** `POST /api/tokens`  
**Authentication:** None required  
**Description:** Authenticate user and get access token

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "securepassword123"
}
```

**Response:**
- **201 Created:** Login successful
  ```json
  {"token": "authentication_token_here"}
  ```

- **401 Unauthorized:** Invalid credentials
- **400 Bad Request:** Missing username or password

---

### Validate Token
**Endpoint:** `POST /api/tokens/validate`  
**Authentication:** None required  
**Description:** Validate if a token is still active

**Request Body:**
```json
{
  "token": "authentication_token_here"
}
```

**Response:**
- **200 OK:** Token is valid
- **401 Unauthorized:** Token is invalid or expired

---

## Mails API

### Get All Mails
**Endpoint:** `GET /api/mails`  
**Authentication:** Required  
**Description:** Retrieve all emails for the authenticated user

**Response:**
- **200 OK:** List of emails
  ```json
  [
    {
      "id": 1,
      "from": "sender_username",
      "to": ["recipient_username"],
      "cc": ["cc_username"],
      "subject": "Email subject",
      "body": "Email content",
      "date": "2025-07-20T10:30:00.000Z",
      "attachments": []
    }
  ]
  ```

- **401 Unauthorized:** Invalid authentication

---

### Send Mail
**Endpoint:** `POST /api/mails`  
**Authentication:** Required  
**Description:** Send a new email

**Request Body:**
```json
{
  "to": ["recipient_username"],
  "cc": ["cc_username"],
  "subject": "Email subject",
  "body": "Email content",
  "attachments": []
}
```

**Response:**
- **201 Created:** Mail sent successfully
  ```json
  {"message": "Mail sent successfully"}
  ```
  - Headers: `Location: /api/mails/<mail_id>`

- **400 Bad Request:** Missing required fields
- **401 Unauthorized:** Invalid authentication

---

### Get Mail by ID
**Endpoint:** `GET /api/mails/:id`  
**Authentication:** Required  
**Description:** Retrieve a specific email by ID

**Response:**
- **200 OK:** Email data retrieved
  ```json
  {
    "id": 1,
    "from": "sender_username",
    "to": ["recipient_username"],
    "cc": ["cc_username"],
    "subject": "Email subject",
    "body": "Email content",
    "date": "2025-07-20T10:30:00.000Z",
    "attachments": []
  }
  ```

- **404 Not Found:** Email not found
- **401 Unauthorized:** Invalid authentication

---

### Update Mail
**Endpoint:** `PATCH /api/mails/:id`  
**Authentication:** Required  
**Description:** Update an existing email (partial update)

**Request Body:**
```json
{
  "subject": "Updated subject",
  "body": "Updated content"
}
```

**Response:**
- **204 No Content:** Mail updated successfully
  - Headers: `Location: /api/mails/<mail_id>`

- **404 Not Found:** Email not found
- **401 Unauthorized:** Invalid authentication

---

### Delete Mail
**Endpoint:** `DELETE /api/mails/:id`  
**Authentication:** Required  
**Description:** Delete a specific email

**Response:**
- **204 No Content:** Mail deleted successfully
- **404 Not Found:** Email not found
- **401 Unauthorized:** Invalid authentication

---

### Create Draft
**Endpoint:** `POST /api/mails/drafts`  
**Authentication:** Required  
**Description:** Create a draft email (not sent)

**Request Body:**
```json
{
  "to": ["recipient_username"],
  "cc": ["cc_username"],
  "subject": "Draft subject",
  "body": "Draft content"
}
```

**Response:**
- **201 Created:** Draft created successfully
  ```json
  {"message": "Draft created successfully"}
  ```

---

### Send Draft
**Endpoint:** `POST /api/mails/drafts/:id/send`  
**Authentication:** Required  
**Description:** Send a previously created draft

**Response:**
- **200 OK:** Draft sent successfully
- **404 Not Found:** Draft not found
- **401 Unauthorized:** Invalid authentication

---

## Labels API

### Get All Labels
**Endpoint:** `GET /api/labels`  
**Authentication:** Required  
**Description:** Retrieve all email labels for the authenticated user

**Response:**
- **200 OK:** List of labels
  ```json
  [
    {
      "id": 1,
      "name": "Important",
      "color": "#ff0000",
      "userId": "user_id"
    }
  ]
  ```

---

### Create Label
**Endpoint:** `POST /api/labels`  
**Authentication:** Required  
**Description:** Create a new email label

**Request Body:**
```json
{
  "name": "Important",
  "color": "#ff0000"
}
```

**Response:**
- **201 Created:** Label created successfully
- **400 Bad Request:** Missing required fields

---

### Get Label by ID
**Endpoint:** `GET /api/labels/:id`  
**Authentication:** Required  
**Description:** Retrieve a specific label by ID

**Response:**
- **200 OK:** Label data retrieved
- **404 Not Found:** Label not found

---

### Update Label
**Endpoint:** `PATCH /api/labels/:id`  
**Authentication:** Required  
**Description:** Update an existing label

**Request Body:**
```json
{
  "name": "Updated Name",
  "color": "#00ff00"
}
```

**Response:**
- **204 No Content:** Label updated successfully
- **404 Not Found:** Label not found

---

### Delete Label
**Endpoint:** `DELETE /api/labels/:id`  
**Authentication:** Required  
**Description:** Delete a specific label

**Response:**
- **204 No Content:** Label deleted successfully
- **404 Not Found:** Label not found

---

## Search API

### Search Emails
**Endpoint:** `GET /api/search/:query`  
**Authentication:** Required  
**Description:** Search through emails using a query string

**Parameters:**
- `query`: Search term to look for in email content

**Example:** `GET /api/search/meeting`

**Response:**
- **200 OK:** Search results
  ```json
  [
    {
      "id": 1,
      "from": "sender_username",
      "to": ["recipient_username"],
      "subject": "Meeting tomorrow",
      "body": "Let's have a meeting tomorrow",
      "date": "2025-07-20T10:30:00.000Z"
    }
  ]
  ```

- **401 Unauthorized:** Invalid authentication

---

## Blacklist API

### Add URL to Blacklist
**Endpoint:** `POST /api/blacklist`  
**Authentication:** Required  
**Description:** Add a URL to the blacklist via the URL server

**Request Body:**
```json
{
  "url": "http://malicious-site.com"
}
```

**Response:**
- **201 Created:** URL added to blacklist
- **400 Bad Request:** Invalid URL format
- **500 Internal Server Error:** URL server communication error

---

### Remove URL from Blacklist
**Endpoint:** `DELETE /api/blacklist/:id`  
**Authentication:** Required  
**Description:** Remove a URL from the blacklist via the URL server

**Parameters:**
- `id`: URL or URL identifier to remove

**Response:**
- **204 No Content:** URL removed from blacklist
- **404 Not Found:** URL not found in blacklist
- **500 Internal Server Error:** URL server communication error

---

## Error Responses

### Common HTTP Status Codes

- **200 OK:** Request successful
- **201 Created:** Resource created successfully
- **204 No Content:** Request successful, no content to return
- **400 Bad Request:** Invalid request data
- **401 Unauthorized:** Authentication required or invalid
- **404 Not Found:** Resource not found
- **409 Conflict:** Resource already exists
- **500 Internal Server Error:** Server-side error

### Error Response Format

```json
{
  "message": "Error description",
  "error": "Detailed error information"
}
```

---

## Rate Limiting and Security

- Authentication tokens have expiration times
- All passwords are hashed before storage
- Input validation is performed on all endpoints
- CORS is configured for web app access

---

## Testing Examples

### Complete User Flow Example

```bash
# 1. Register user
curl -X POST http://localhost:3000/api/users \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","birthday":"1990-01-01","username":"testuser","password":"testpass"}'

# 2. Login to get token
TOKEN=$(curl -s -X POST http://localhost:3000/api/tokens \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 3. Send email
curl -X POST http://localhost:3000/api/mails \
  -H "Authorization: testuser" \
  -H "Content-Type: application/json" \
  -d '{"to":["recipient"],"subject":"Test","body":"Hello World"}'

# 4. Get all emails
curl -X GET http://localhost:3000/api/mails \
  -H "Authorization: testuser"

# 5. Search emails
curl -X GET http://localhost:3000/api/search/Hello \
  -H "Authorization: testuser"
```

This completes the API documentation for all available routes in the web server.
