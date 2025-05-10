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
        length = len(message)
        network_length = socket.htonl(length)  # Convert to network byte order
        header = struct.pack('I', network_length)  # Pack as 4-byte unsigned int
        self.sock.sendall(header + message)

    def receive_response(self):
        """Receive a response with a 4-byte length prefix, then read exactly that many bytes."""
        try:
            # Read the 4-byte length header
            raw_length = self._recv_exactly(4)
            if not raw_length:
                raise ConnectionError("Connection closed before length header received.")

            packed_length = struct.unpack('I', raw_length)[0]
            message_length = socket.ntohl(packed_length)  # Convert to host byte order

            # Read the full message of that length
            message_data = self._recv_exactly(message_length)
            return message_data.decode().strip()

        except Exception as e:
            return f"[ERROR] Failed to receive complete message: {e}"

    def _recv_exactly(self, n):
        """Helper to receive exactly n bytes from the socket."""
        data = b''
        while len(data) < n:
            chunk = self.sock.recv(n - len(data))
            if not chunk:
                raise ConnectionError("Socket closed while receiving data.")
            data += chunk
        return data
    
    def close(self):
        """Close the socket connection."""
        if self.sock:
            self.sock.close()