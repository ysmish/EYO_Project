import sys
from tcp_client import TCPClient

def run_client(server_ip, server_port):
    client = TCPClient(server_ip, server_port)
    try:
        client.connect()
        print(f"[INFO] Connected to server at {server_ip}:{server_port}")

        while True:
            command = input("> ").strip()
            if not command:
                continue
            client.send_command(command)
            response = client.receive_response()
            print(response)
    except KeyboardInterrupt:
        print("\n[INFO] Client terminated by user.")
    except Exception as e:
        print(f"[ERROR] {e}")
    finally:
        client.close()


def main():
    if len(sys.argv) != 3:
        print("Usage: python3 -m client.main <server_ip> <server_port>")
        sys.exit(1)

    server_ip = sys.argv[1]
    server_port = sys.argv[2]
    run_client(server_ip, server_port)

if __name__ == "__main__":
    main()