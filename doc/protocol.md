# Bloom Filter Protocol 

## Goal
To define a reliable, binary-safe TCP communication protocol between a Python-based client and a C++ server that operates on a Bloom filter.

---

## Message Exchange Overview

All communication is:
- Over a **persistent TCP connection**
- **Synchronous**: client sends → waits for response → proceeds

---

## Client → Server Messages

| Command | Fields     | Example                          | Description                                  |
|---------|------------|----------------------------------|----------------------------------------------|
| POST    | [URL]      | `POST http://example.com\n`      | Add URL to the blacklist                     |
| GET     | [URL]      | `GET http://example.com\n`       | Query if URL is blacklisted                  |
| DELETE  | [URL]      | `DELETE http://example.com\n`    | Remove URL from blacklist                    |
| Other   | [Invalid]  | `FOO bar\n`                      | Server should respond with `400 Bad Request` |

> ⚠ Commands must end with a newline (`\n`) in the message body.

---

## Server → Client Responses

| Code | Message Format                     | When Used                                           |
|------|------------------------------------|----------------------------------------------------|
| 201  | `201 Created`                      | URL successfully added (POST)                     |
| 200  | `200 Ok\n\n[content]`              | URL is in blacklist (GET), includes Bloom result  |
| 204  | `204 No Content`                   | URL removed or not found (DELETE)                 |
| 404  | `404 Not Found`                    | URL not found or logical error                    |
| 400  | `400 Bad Request`                  | Invalid syntax / unknown command                  |

---

## Full Communication Example

Client                          Server
------                          ------
1. Establish TCP ⟶────────────►
                                (Connection open)
2. “POST http://a.com\n” ────►  Validate & add to Bloom
                                ◄─── “201 Created\n”
3. “GET http://a.com\n”  ────►  Check Bloom membership
                                ◄─── “200 Ok\n\ntrue\n”
4. “FOO bar\n”           ────►  Unknown command
                                ◄─── “400 Bad Request\n”
5. “DELETE http://b.com\n”─►  Attempt removal
                                ◄─── “404 Not Found\n”

---

## Connection Rules

- The connection is **persistent**: do **not close** after each message.
- The client should **reuse the same socket** throughout execution.

---

## Encoding and Constraints

- All text is UTF-8 encoded.
- Message bodies must contain only printable ASCII-compatible characters.
- Newlines (`\n`) are allowed in the message body.
- Fields in the body (e.g., `POST [URL]`) must be separated by a single space.