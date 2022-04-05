# List Server
##### **Author:** Austin Hendricks
##### **Date:** April 4, 2022

---
## Table of Contents
1. [How to Run](#how-to-run)
2. [Log](#log)
3. [Server Commands](#server-commands)
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
View the log to see server log entries for session.

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