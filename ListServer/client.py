import socket, sys

#-------------------------------------------------------------------------------------------------------------
# Determine port

HOST, PORT = 'localhost', 9999
try:
    arg_count = len(sys.argv)
    if arg_count == 2:
        if sys.argv[1].isdigit():
            if int(sys.argv[1]) > 1399 and int(sys.argv[1]) < 10000:  
                PORT = int(sys.argv[1])
            else:
                print('Port must be between 1400 and 9999')
                user_port = -1
                while (user_port not in range(1400,10000)):
                    user_port = input("Input desired port in range [1400, 9999]: ").strip()
                    if user_port.isdigit() and int(user_port) in range(1400, 10000):
                        user_port = int(user_port)
                        PORT = user_port
                    else: 
                        print("Invalid port number.")
        else:
            try:
                configFile = open(sys.argv[1], 'r')
            except:
                print(f"\nUnable to open specified config file.")
                print('Usage: \"python client.py [config_file]\" (config_file must be a .txt file)')
                print("Format: desired port on first line, followed by newline.\n")
                exit(1)
            Lines = configFile.readlines()
            configPort = Lines[0].strip()
            if configPort.isdecimal() and int(configPort) in range(1400, 10000):
                PORT = int(configPort)
            else:
                print("Config file invalid. Format: desired port on first line, followed by newline.")
                user_port = -1
                while (user_port not in range(1400,10000)):
                    user_port = input("Input desired port in range [1400, 9999]: ").strip()
                    if user_port.isdigit() and int(user_port) in range(1400, 10000):
                        user_port = int(user_port)
                        PORT = user_port
                    else: 
                        print("Invalid port number.")
    else:
        print('\nUsage: \"python client.py [port]\"')
        print('Port must be between 1400 and 9999')
        print('OR')
        print('Usage: \"python client.py [config_file]\" (config_file must be a .txt file)')
        print("Format: desired port on first line, followed by newline.\n")
        exit(1)
except KeyboardInterrupt:
    print("\nDetected keyboard interrupt. Exiting gracefully.")
    exit(1)

#-------------------------------------------------------------------------------------------------------
# Connect to server and communicate

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
                s.close()
                print(f"Connection with {HOST}:{PORT} closed.")
                break

    except SystemExit:
        print("Exiting.")
    except socket.error:
        print("Connection timed out.")
    except KeyboardInterrupt:
        print("\nDetected keyboard interrupt. Exiting gracefully.")
    finally:
        exit(0)
