import sys
from tcp_client import TCPClient

def run_client(server_ip, server_port):
    # Validate input up-front via our static helper
    if not TCPClient.validate_ip_and_port(server_ip, server_port):
        print(f"[ERROR] Invalid server IP or port: {server_ip!r}, {server_port!r}")
        return
    
    # Instantiate our TCP client
    client = TCPClient(server_ip, server_port)

    # Attempt initial connection to the server
    try:
        client.connect()
        print(f"[INFO] Connected to {server_ip}:{server_port}")
    except ConnectionError as e:
        print(f"[ERROR] {e}")
        return

    # Main command loop: read user input, send to server, print response
    while True:
        try:
            command = input("> ").strip()
            if not command:
                # Ignore empty lines
                continue

            # Send the user’s command, then wait for the server’s reply
            client.send_command(command)
            response = client.receive_response()
            print(response)

        except KeyboardInterrupt:
            # Handle Ctrl+C gracefully
            print("\n[INFO] Exiting on user request.")
            break

        except ConnectionError as e:
            # If something goes wrong with the socket, try to reconnect once
            print(f"[WARNING] {e}")
            print("[INFO] Attempting to reconnect...")
            try:
                client.connect()
                print("[INFO] Reconnected successfully.")
            except ConnectionError as ce:
                # If reconnection fails, give up
                print(f"[ERROR] Reconnect failed: {ce}")
                print("[FATAL] Cannot continue without a connection.")
                break

        except Exception as e:
            # Catch-all for any other unexpected errors
            print(f"[ERROR] Unexpected error: {e}")
            break

    # Clean up socket on exit
    client.close()

def main():
    # Expect exactly two arguments: server IP and port
    if len(sys.argv) != 3:
        print("Usage: python3 -m client.main <server_ip> <server_port>")
        sys.exit(1)

    run_client(sys.argv[1], sys.argv[2])

if __name__ == "__main__":
    main()
