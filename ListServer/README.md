###### [Back to all projects in school-projects repo](https://github.com/austin-hendricks/school-projects)

# List Server
###### Python project using socket programming to create interactive server that client can connect to and issue commands to manage a todolist.
###### Assignment for Ohio State University's _CSE 3461: Computer Networking_ course.
###### Author: [Austin Hendricks](https://github.com/austin-hendricks)
###### Date Completed: April 4, 2022

---
## Table of Contents
1. [Technical Requirements and Constraints](#technical-requirements-and-constraints)
2. [How to Run](#how-to-run)
3. [Log](#log)
4. [Server Commands](#server-commands)
    - [add](#add)
    - [clearcomp](#clearcomp)
    - [complete](#complete)
    - [deleteall](#deleteall)
    - [echo](#echo)
    - [exists](#exists)
    - [exit](#exit)
    - [help](#help)
    - [remove](#remove)
    - [show](#show)

---
## Technical Requirements and Constraints
This is a request/response type network server application implemented using socket programming techniques to interact with the Internet Protocol Stack of the host OS.

### Server Requirements (fully met)
1. Server must accept and respond to a minimum of five (5) different commands. One of these can be used to shut down your server application gracefully.
2. A configuration file or command line arguments must be used to set the network port on which the server application will "listen", thereby adhering to industry "best practice".
3. The server must output a log of received commands and responses sent. Each line of the log should contain:
   - The current date and time
   - The word "REQUEST" or "RESPONSE" depending on what the server is doing
   - A log entry level (e.g., "INFO", "ERROR", etc.)
   - An appropriate request or response message
4. Upon start up, the server application must display a status message that includes the port on which it is listening.

### Client Requirements (fully met)
1. A configuration file, command line argument, or user prompt must be used to obtain the server information (server IP address and port) to use.
2. If an invalid command is received, an error must be displayed stating this and then display a list of valid commands.
3. If a valid command was received, but is improperly formatted, a detailed response must be displayed.

### Constraints (fully met)
1. The client and server applications must be able to run from two different IP addresses. For practical purposes, one may be the loopback address if both the client and server are run on the same host.
2. Applications may not be developed as a web application (an application built on top of a web browser and/or web server).
3. Each command entered in the client should result in a request being sent to the server.
4. Each request received by the server should result in a response sent back to the client.
5. Both the client and server must run without runtime or interpreter errors.

---
## How to Run
1. Download all files from zip and place into same directory.
2. Open two terminal windows, navigate to that directory in both.
3. Optionally edit config.txt to contain desired port number.
4. Run command to start server.
5. Run command to start client.
6. Interact with server via client.

If using config.txt for port, issue the following commands to run server and client programs:
```
$ python listServer.py config.txt
$ python client.py config.txt
```

Otherwise, specify port as command line arg with the following commands to run server and client programs (ex. port 9999):
```
$ python listServer.py 9999
$ python client.py 9999
```

---
## Log
Each time the server program begins listening, a log file is created within the same directory.

View a log file to see server log entries for that session.

---
## Server Commands
Below are a list of all of the commands the ListServer accepts, along with what they do and how they are used.

### **add**
>Adds specified task to list.
- **Usage:** `add [task]`
    - Requires `[task]` argument.

### **clearcomp**
> Removes all completed tasks from list.
- **Usage:** `clearcomp`
    - No extra arguments allowed.

### **complete**
> Marks specified task as complete.
- **Usage:** `complete [task]`
    - Requires `[task]` argument.
    - Specified task must exist in list.

### **deleteall**
> Deletes all tasks from list.
- **Usage:** `deleteall`
    - No extra arguments allowed.

### **echo**
> Returns original message, uppercased.
- **Usage:** `echo [msg]`
    - Requires `[msg]` argument.

### **exists**
> Reports whether specified task exists in list.
- **Usage:** `exists [task]`
    - Requires `[task]` argument.

### **exit**
> Closes connection with client and closes server.
- **Usage:** `exit`
    - No extra arguments allowed.

### **help**
> Displays list of valid commands and their uses.
- **Usage:** `help`
    - No extra arguments allowed.

### **remove**
> Removes specified task from list.
- **Usage:** `remove [task]`
    - Requires `[task]` argument.
    - Specified task must exist in list.

### **show**
> Displays all tasks on list and their completion status.
- **Usage:** `show`
    - No extra arguments allowed.
