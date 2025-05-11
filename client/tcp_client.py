import socket
import struct

class TCPClient:
    """Handles TCP connection, sending, and receiving messages from the server."""

    def __init__(self, server_ip, server_port):
        self.server_ip = server_ip
        self.server_port = int(server_port)
        self.sock = None

    def connect(self):
        """Establish a persistent TCP connection to the server."""
        self.sock = socket.create_connection((self.server_ip, self.server_port))

    def send_command(self, command):
        """Send a newline-terminated command string to the server."""
        message = (command + '\n').encode('utf-8')
        self.sock.sendall(message)

    def receive_response(self):
        """Receive response from the server, up to and including the first newline."""
        data = b''
        while True:
            chunk = self.sock.recv(4096)
            if not chunk:
                break
            data += chunk
            if b'\n' in chunk:
                break
        return data.decode().strip()

    def close(self):
        """Close the socket connection."""
        if self.sock:
            self.sock.close()