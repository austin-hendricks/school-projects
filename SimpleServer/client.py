import socket, sys

HOST, PORT = 'localhost', 9999
arg_count = len(sys.argv)
if arg_count == 2 and sys.argv[1].isdigit():  
    PORT = int(sys.argv[1])
elif arg_count != 1:
    print('Usage: \"python client.py [port]\" (default 9999 if no port specified)')
    exit(1)

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    try:
        s.connect((HOST, PORT))
        print(f"Connected to {HOST} on port {PORT}")
        while True:
            data = str(s.recv(1024).decode('ascii'))
            msg = ""
            while True:
                msg = input(data)
                if msg != "":
                    break;
            
            s.sendall(msg.encode('ascii'))
            response = str(s.recv(1024).decode('ascii'))
            print(response)
            if response == "Closing connection...":
                print(f"Connection with {HOST}:{PORT} closed.")
                sys.exit()

    except SystemExit:
        print("Exiting.")
    except socket.error:
        print("Connection timed out.")
    except KeyboardInterrupt:
        print("\nDetected keyboard interrupt. Exiting gracefully.")
    finally:
        exit(0)
