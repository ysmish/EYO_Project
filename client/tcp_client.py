import socket

class TCPClient:
    """
    A simple TCP client that:
      - connects to a server,
      - sends newline-terminated commands,
      - reads responses until the first newline,
      - and handles disconnections.
    """

    def __init__(self, server_ip, server_port):
        # Store connection parameters and initialize socket to None
        self.server_ip = server_ip
        self.server_port = int(server_port)
        self.sock = None

    def connect(self):
        """
        Open (or re-open) a TCP connection to the server.
        Closes any existing socket first, then creates a new one.
        """
        # If we already have a socket, close it before reconnecting
        self.close()
        try:
            # Create a new TCP connection
            self.sock = socket.create_connection((self.server_ip, self.server_port))
        except (socket.error, OSError) as e:
            # Wrap socket errors in ConnectionError for caller to handle
            raise ConnectionError(f"Could not connect to {self.server_ip}:{self.server_port} — {e}")

    def send_command(self, command):
        """
        Send a single command (plus newline) to the server.
        On failure, closes the socket so a fresh reconnect is possible.
        """
        if not self.sock:
            raise ConnectionError("Not connected to server.")

        # Append the required newline and encode to bytes
        payload = (command + '\n').encode('utf-8')
        try:
            # sendall() ensures all data is sent (or raises an error)
            self.sock.sendall(payload)
        except (socket.error, OSError) as e:
            # On error, clean up and bubble up a ConnectionError
            self.close()
            raise ConnectionError(f"Send failed: {e}")

    def receive_response(self):
        """
        Read from the socket until we see a newline, then return the line.
        Handles disconnections by raising ConnectionError.
        """
        if not self.sock:
            raise ConnectionError("Not connected to server.")

        data = b''
        try:
            while True:
                # Read up to 4096 bytes at a time
                chunk = self.sock.recv(4096)
                if not chunk:
                    # Server closed the connection unexpectedly
                    raise ConnectionError("Server closed the connection.")

                data += chunk
                # If the last byte we received is a newline, we've got a full message
                if chunk.endswith(b'\n'):
                    break

        except (socket.error, OSError) as e:
            # On any socket error, close and notify caller
            self.close()
            raise ConnectionError(f"Receive failed: {e}")

        # Decode from bytes to string and strip trailing whitespace/newlines
        return data.decode('utf-8').strip()

    def close(self):
        """
        Close the socket if it’s open, suppressing any errors.
        Ensures self.sock is set back to None.
        """
        if self.sock:
            try:
                self.sock.close()
            except Exception:
                pass
        self.sock = None