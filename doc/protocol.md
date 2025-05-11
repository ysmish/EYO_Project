# Bloom Filter Protocol

## Goal
To create a connection between the client and the server that will allow the client to interact with the Bloom filter on the server side.

## Message Types

### Client → Server

| Command | Fields   | Explanation                          | Response |
|---------|----------|--------------------------------------|----------|
| POST    | [URL]    | Adds a URL to the blacklist.         | 201      |
| GET     | [URL]    | Check if a URL is in the blacklist.  | 204      |
| DELETE  | [URL]    | Removes a URL from the blacklist.    | 200/404  |

### Server → Client

| Response | Fields        | Explanation                                      |
|----------|---------------|--------------------------------------------------|
| 201      | Created       | Successful response to the POST request.        |
| 200      | Ok            | Successful response to the GET request. Also sends the requested data in the content. |
| 204      | No Content    | Successful response to the DELETE request.      |
| 404      | Not Found     | Response to a valid but illogical request.      |
| 400      | Bad Request   | Response to an invalid request.                 |

## Message Syntax

- Every message is a string (ASCII characters only).
- Messages may contain fields.
- Fields are separated by a space `" "`.
- Messages may contain content.
- The content is separated by a double newline `"\n\n"` at the end of all fields.
- The first field of client messages is the type of request.
- The first field of server messages is the response number.

## Connection and Communication Type
- The connection is over TCP.
- The communication is synchronous (after a request, wait for a response).

## Example

| Step | Actor   | Action                                  | Data                                   |
|------|---------|-----------------------------------------|----------------------------------------|
| 1    | Client  | Connects to server                      | TCP Connection established             |
| 2    | User    | Enters command: `POST http://example.com` | (Input to client)                      |
| 3    | Client  | Sends command to server                 | `POST http://example.com`              |
| 4    | Server  | Processes command (adds URL to blacklist) | (Internal logic)                       |
| 5    | Server  | Sends response to client                | `201 Created`                          |
| 6    | Client  | Displays response                       | `201 Created` (console)                |
| 7    | User    | Enters command: `GET http://example.com`  | (Input to client)                      |
| 8    | Client  | Sends command to server                 | `GET http://example.com`               |
| 9    | Server  | Checks Bloom filter/blacklist           | (Internal logic)                       |
| 10   | Server  | Sends response to client                | `200 Ok\n\ntrue true`                  |
| 11   | Client  | Displays response                       | `200 Ok\n\ntrue true` (console)        |
| 12   | User    | Enters invalid command: `FOO bar`       | (Input to client)                      |
| 13   | Client  | Sends command to server                 | `FOO bar`                              |
| 14   | Server  | Rejects invalid command                 | `400 Bad Request`                      |
| 15   | Client  | Displays error                          | `400 Bad Request` (console)            |
| 16   | User    | Enters command: `DELETE http://what.com` | (Input to client)                      |
| 17   | Client  | Sends command to server                 | `DELETE http://what.com`               |
| 18   | Server  | Processes command (tries to delete the URL) | (Internal logic)                       |
| 19   | Server  | Rejects illogical command               | `404 Not Found`                        |
| 20   | Client  | Displays error                          | `404 Not Found` (console)              |

### Notes
- Fields cannot contain spaces or newlines, but this can be resolved using encodings like Base64.
- The current protocol does not include metadata indicating the end of each message. This may change in future updates.
