import socket
import sys

def main():
    HOST, PORT = 'localhost', 9999
    arg_count = len(sys.argv)
    if arg_count == 2 and sys.argv[1].isdigit():  
        PORT = int(sys.argv[1])
    elif arg_count != 1:
        print('Usage: \"python client.py [port]\" (default 9999 if no port specified)')
        exit(1)

    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.bind((HOST, PORT))
            s.listen()
            print(f"Listening on port {PORT}...")

            conn, addr = s.accept()
            print(f"Connected by {addr}")

            with conn:
                while True:
                    conn.sendall("SimpleServer> ".encode('ascii'))
                    data = conn.recv(1024).strip().decode('ascii')
                    data_split = data.split(" ", 1)
                    command = data_split[0]                        
                    print("{} wrote: ".format(conn.getpeername()), end="")
                    print(data)
                    
                    if command == 'echo' :
                        if len(data_split) > 1:
                            msg = data_split[1]
                            # just send back the same data, but upper-cased
                            print(f"Echoing {msg.upper()} to {conn.getpeername()}")
                            conn.sendall(bytes(msg.upper().encode('ascii')))
                        else:
                            print("Invalid format for echo request.")
                            conn.sendall("Usage: \"echo [message]\"".encode('ascii'))
                    elif command == 'exit':
                        print("Exit request recieved. Closing connection.")
                        conn.sendall("Closing connection...".encode('ascii'))
                        conn.close()
                        break
                    elif command == 'help':
                        print(f"Fulfilling help request to {conn.getpeername()}.")
                        help_str = "---\nVALID COMMANDS:\necho: \"echo [msg]\" - Returns original message, uppercased.\nexit: \"exit\" - Closes connection.\nhelp: \"help\" - Displays list of valid commands and their uses.\n---"
                        conn.sendall(bytes(help_str.encode('ascii')))
                    else:
                        print("Command \"" + command + "\" not valid. Continuing to listen for next command.")
                        error_str = "Command \"" + command + "\" not recognized. Use 'help' to see list of valid commands."
                        conn.sendall(bytes(error_str.encode('ascii')))

    except KeyboardInterrupt:
        print("\nDetected keyboard interrupt. Closing all connections and exiting gracefully.")
        conn.close()
    except:
        print("Connection closed by client.")
    finally:
        sys.exit()


def allTasks():
    # do something
    return


def toDo():
    # do something
    return


def completed():
    # do something
    return


def addTask(task):
    # do something
    return


def removeTask(task):
    # do something
    return


def popTask():
    # do something
    return


def isInList(task):
    # do something
    return


def isComplete(task):
    # do something
    return


def completeTask(task):
    # do something
    return


def clearCompleted():
    # do something
    return


def numTasksToDo():
    # do something
    return


def numTasksCompleted():
    # do something
    return


def numTasksTotal():
    # do something
    return



if __name__ == "__main__":
    main()
