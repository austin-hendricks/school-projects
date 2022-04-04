import socket
from sqlite3 import complete_statement
import sys

def main():
    list = []
    
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
                    data = conn.recv(1024).strip().decode('ascii').lower()
                    data_split = data.split(" ", 1)
                    command = data_split[0]                        
                    print("{} wrote: ".format(conn.getpeername()), end="")
                    print(data)
                    
                    if command == 'echo' :
                        echo(conn, data_split)

                    elif command == 'help':
                        help(conn, data_split)

                    elif command == 'showall':
                        allTasks(conn, data_split, list)

                    elif command == 'add':
                        addTask(conn, data_split, list)

                    elif command == 'remove':
                        removeTask(conn, data_split, list)
                    
                    elif command == 'complete':
                        completeTask(conn, data_split, list)
                    
                    elif command == 'istask':
                        isInList(conn, data_split, list)
                    
                    elif command == 'clearcompleted':
                        clearCompleted(conn, data_split, list)
                    
                    elif command == 'deleteall':
                        deleteAll(conn, data_split, list)
                    
                    elif command == 'exit':
                        if exit(conn, data_split):
                            break                     
                    
                    else:
                        invalid(conn, command)

    except KeyboardInterrupt:
        print("\nDetected keyboard interrupt. Closing all connections and exiting gracefully.")
        conn.close()
    except:
        print("Connection closed by client.")
    finally:
        sys.exit()


#-----------------------------------------------------------------------------
# Utility Commands

def echo(conn, data_split):
    if len(data_split) > 1:
        msg = data_split[1]
        # just send back the same data, but upper-cased
        print(f"Echoing {msg.upper()} to {conn.getpeername()}")
        conn.sendall(bytes(msg.upper().encode('ascii')))
    else:
        print("Invalid format for echo request.")
        conn.sendall("Usage: \"echo [msg]\"\n\techo requires msg argument.".encode('ascii'))


def help(conn, data_split):
    if len(data_split) > 1:
        print("Invalid format for help request.")
        conn.sendall("Usage: \"help\"\nhelp does not take any arguments".encode('ascii'))
    else:
        print(f"Fulfilling help request to {conn.getpeername()}.")
        help_str = "---\nVALID COMMANDS:\n"
        help_str += "echo: \"echo [msg]\" - Returns original message, uppercased.\n\techo requires msg argument.\n"
        help_str += "exit: \"exit\" - Closes connection.\n"
        help_str += "help: \"help\" - Displays list of valid commands and their uses.\n"
        help_str += "showall: \"showall\" - Displays all tasks on list.\n"
        help_str += "add: \"add [task]\" - Adds specified task to list.\n\tadd requires task argument.\n"
        help_str += "remove: \"remove [task]\"\n\tremove requires task argument.\n\ttask must exist in list.\n"
        help_str += "complete: \"complete [task]\"\n\tcomplete requires task argument.\n\ttask must exist in list.\n"
        help_str += "---"
        conn.sendall(bytes(help_str.encode('ascii')))


def invalid(conn, command):
    print("Command \"" + command + "\" not valid. Continuing to listen for next command.")
    error_str = "Command \"" + command + "\" not recognized. Use 'help' to see list of valid commands."
    conn.sendall(bytes(error_str.encode('ascii')))


def exit(conn, data_split):
    if len(data_split) > 1:
        print("Invalid format for exit request.")
        conn.sendall("Usage: \"exit\"\nexit does not take any arguments".encode('ascii'))
        return False
    else:
        print("Exit request recieved. Closing connection.")
        conn.sendall("Closing connection...".encode('ascii'))
        conn.close()
        return True
        
#-----------------------------------------------------------------------------
# List-Related Commands

def allTasks(conn, data_split, list):
    if len(data_split) > 1:
        print("Invalid format for showall request.")
        conn.sendall("Usage: \"showall\"\nshowall does not take any arguments".encode('ascii'))
    else:
        print(f"Fulfilling showall request to {conn.getpeername()}.")
        if len(list) == 0:
            conn.sendall(bytes("No tasks on list.".encode('ascii')))
        else:
            all_str = "\nTASK  |  STATUS\n_______________\n"
            for task in list:
                complete_str = "complete"
                if not task[1]:
                    complete_str = "in" + complete_str
                all_str += task[0] + " | " + complete_str + "\n"
            conn.sendall(bytes(all_str.encode('ascii')))


def addTask(conn, data_split, list):
    if len(data_split) != 2:
        print("Invalid format for add request.")
        conn.sendall("Usage: \"add [task]\"\n\tadd requires task argument.".encode('ascii'))
    else:
        task = data_split[1]
        print(f"Fulfilling add request to {conn.getpeername()}...")
        list.append([task, False])
        print(f"Added task ({task}) to list.")
        success_str = f"Sucessfully added task ({task}) to list."
        conn.sendall(bytes(success_str.encode('ascii')))


def removeTask(conn, data_split, list):
    if len(data_split) != 2:
        print("Invalid format for remove request.")
        conn.sendall("Usage: \"remove [task]\"\n\tremove requires task argument.\n\ttask must exist in list.".encode('ascii'))
    else:
        task = data_split[1]
        if ([task, True] in list):
            item_found = [task, True]
        elif ([task, False] in list):
            item_found = [task, False]
        else:
            print(f"Unable to fulfill remove request to {conn.getpeername()}.")
            conn.sendall(f"Task ({task}) not found in list.".encode('ascii'))
            return
        
        print(f"Fulfilling remove request to {conn.getpeername()}...")
        list.remove(item_found)
        print(f"Removed task ({task}) from list.")
        success_str = f"Sucessfully removed task ({task}) from list."
        conn.sendall(bytes(success_str.encode('ascii')))


def completeTask(conn, data_split, list):
    if len(data_split) != 2:
        print("Invalid format for complete request.")
        conn.sendall("Usage: \"complete [task]\"\n\tcomplete requires task argument.\n\ttask must exist in list.".encode('ascii'))
    else:
        task = data_split[1]
        if ([task, False] in list):
            task_in_list = list.index([task, False])
        else:
            conn.sendall(f"No incomplete task ({task}) not found in list.".encode('ascii'))
            return
        
        print(f"Fulfilling complete request to {conn.getpeername()}...")
        list[task_in_list][1] = True
        print(f"Completed task ({task}).")
        success_str = f"Sucessfully completed task ({task})."
        conn.sendall(bytes(success_str.encode('ascii')))


def isInList(conn, data_split, list):
    # specify valid task
    return


def clearCompleted(conn, data_split, list):
    # do something
    return


def deleteAll(conn, data_split, list):
    # do something
    return


#-----------------------------------------------------------------------------

if __name__ == "__main__":
    main()
