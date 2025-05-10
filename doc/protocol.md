# Bloom Filter Protocol 

## Goal
To define a reliable, binary-safe TCP communication protocol between a Python-based client and a C++ server that operates on a Bloom filter. This version uses a **length-prefixed** message format for clarity and robustness.

---

## Message Exchange Overview

All communication is:
- Over a **persistent TCP connection**
- **Synchronous**: client sends → waits for response → proceeds
- **Length-prefixed**: Every message (client → server, and server → client) starts with a **4-byte unsigned integer** indicating the **size of the message body that follows**.

---

## 🔒 Message Format

### Binary Structure

```
[4-byte big-endian unsigned int][message body in UTF-8]
```

| Component       | Size  | Description                                                                 |
|----------------|-------|-----------------------------------------------------------------------------|
| Length Prefix   | 4 B   | Unsigned 32-bit integer, **big-endian** (`network byte order`)              |
| Message Body    | N B   | UTF-8 encoded string of exactly the specified length                       |

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

## Full Communication Example (Byte-Level)

1. **Client sends:**
   ```
   Length: 0x0000001A (26 bytes)
   Body:   POST http://example.com\n
   ```

2. **Server responds:**
   ```
   Length: 0x0000000D (13 bytes)
   Body:   201 Created
   ```

3. **Client sends:**
   ```
   Length: 0x00000019 (25 bytes)
   Body:   GET http://example.com\n
   ```

4. **Server responds:**
   ```
   Length: 0x00000018 (24 bytes)
   Body:   200 Ok\n\ntrue true
   ```

---

## Connection Rules

- The connection is **persistent**: do **not close** after each message.
- The client should **reuse the same socket** throughout execution.
- Both client and server must always:
  1. Read exactly 4 bytes to get the message size
  2. Then read exactly that many bytes to obtain the message

---

## Encoding and Constraints

- All text is UTF-8 encoded.
- Message bodies must contain only printable ASCII-compatible characters.
- Newlines (`\n`) are allowed in the message body.
- Fields in the body (e.g., `POST [URL]`) must be separated by a single space.