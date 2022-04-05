import socket, sys

def main():
    list = []
    
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
                    print('Usage: \"python ss.py [config_file]\" (config_file must be a .txt file)')
                    print("Format: desired port on first line, followed by newline.\n")
                    return
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
            print('\nUsage: \"python ss.py [port]\"')
            print('Port must be between 1400 and 9999')
            print('OR')
            print('Usage: \"python ss.py [config_file]\" (config_file must be a .txt file)')
            print("Format: desired port on first line, followed by newline.\n")
            return
    except KeyboardInterrupt:
        print("\nDetected keyboard interrupt. Exiting gracefully.")
        return

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

                    elif command == 'show':
                        allTasks(conn, data_split, list)

                    elif command == 'add':
                        addTask(conn, data_split, list)

                    elif command == 'remove':
                        removeTask(conn, data_split, list)
                    
                    elif command == 'complete':
                        completeTask(conn, data_split, list)
                    
                    elif command == 'exists':
                        isInList(conn, data_split, list)
                    
                    elif command == 'clearcomp':
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
        return


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
        conn.sendall("Usage: \"help\"\n\thelp does not take any arguments".encode('ascii'))
    else:
        print(f"Fulfilling help request to {conn.getpeername()}...")
        help_str = "---\nVALID COMMANDS:\n"
        help_str += "echo: \"echo [msg]\" - Returns original message, uppercased.\n\techo requires msg argument.\n"
        help_str += "exit: \"exit\" - Closes connection.\n\texit does not take any arguments.\n"
        help_str += "help: \"help\" - Displays list of valid commands and their uses.\n\thelp does not take any arguments.\n"
        help_str += "show: \"show\" - Displays all tasks on list.\n\tshow does not take any arguments.\n"
        help_str += "add: \"add [task]\" - Adds specified task to list.\n\tadd requires task argument.\n"
        help_str += "remove: \"remove [task]\" - Removes specified task from list.\n\tremove requires task argument.\n\ttask must exist in list.\n"
        help_str += "complete: \"complete [task]\" - Marks specified task as complete.\n\tcomplete requires task argument.\n\ttask must exist in list.\n"
        help_str += "exists: \"exists [task]\" - Reports whether specified task exists in list.\n\texists requires task argument.\n"
        help_str += "clearcomp: \"clearcomp\" - Removes all completed tasks from list.\n\tclearcomp does not take any arguments.\n"
        help_str += "deleteall: \"deleteall\" - Deletes all tasks from list.\n\tdeleteall does not take any arguments.\n"
        help_str += "---"
        print("Help request fulfilled.")
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
        print("Invalid format for show request.")
        conn.sendall("Usage: \"show\"\n\show does not take any arguments".encode('ascii'))
    else:
        print(f"Fulfilling show request to {conn.getpeername()}...")
        if len(list) == 0:
            res_str = "No tasks on list."
            print(res_str)
            conn.sendall(bytes(res_str.encode('ascii')))
        else:
            all_str = "\nTASK  |  STATUS\n_______________\n"
            for task in list:
                complete_str = "complete"
                if not task[1]:
                    complete_str = "in" + complete_str
                all_str += task[0] + " | " + complete_str + "\n"
            conn.sendall(bytes(all_str.encode('ascii')))
            print("show request completed successfully.")


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
    if len(data_split) != 2:
        print("Invalid format for exists request.")
        conn.sendall("Usage: \"exists [task]\"\n\tistask requires task argument.".encode('ascii'))
    else:
        task = data_split[1]
        print(f"Fulfilling exists request to {conn.getpeername()}...")
        if ([task, True] in list or [task, False] in list):
            item_found = True
            print(f"Task ({task}) in list.")
        else:
            item_found = False
            print(f"Task ({task}) not in list.")

        item_found = str(item_found)
        conn.sendall(bytes(item_found.encode('ascii')))


def clearCompleted(conn, data_split, list):
    if len(data_split) > 1:
        print("Invalid format for clearcomp request.")
        conn.sendall("Usage: \"clearcomp\"\n\tclearcomp does not take any arguments.".encode('ascii'))
    else:
        print(f"Fulfilling clearcomp request to {conn.getpeername()}...")
        count = 0
        for task in list:
            if task[1]:
                list.remove([ task[0], task[1] ])
                count += 1

        if count == 0:
            success_str = "No completed tasks to clear."
        else:
            success_str = "Sucessfully cleared all completed tasks."

        print(success_str)
        conn.sendall(bytes(success_str.encode('ascii')))


def deleteAll(conn, data_split, list):
    if len(data_split) > 1:
        print("Invalid format for deleteall request.")
        conn.sendall("Usage: \"deleteall\"\n\tdeleteall does not take any arguments.".encode('ascii'))
    else:
        print(f"Fulfilling deleteall request to {conn.getpeername()}...")
        count = len(list)

        if count == 0:
            success_str = "No tasks to delete."
        else:
            list.clear()
            success_str = "Sucessfully deleted all tasks."

        print(success_str)
        conn.sendall(bytes(success_str.encode('ascii')))


#-----------------------------------------------------------------------------

if __name__ == "__main__":
    main()
