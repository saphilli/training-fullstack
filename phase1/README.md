# Phase 1 Assessment
## LockedMe.com
A Java console application serving as a file storage platform developed as a Maven project. The app accepts commands to add, delete, search and read from files, as well as to add and delete directories. 

### Running the Application
Clone this repository and open in an IDE such as Eclipse/IntelliJ to run locally. The program entry point is located at `../LockedMe/src/main/java/com/Main.java`.
When the application is first run, it will create directory called `root` located at `phase1/LockedMe` project directory. This is where the files/directories added through the app will be stored.

To use the app, simply enter commands through the console in your IDE.

### Commands

Once the app is running, the user is shown a welcome screen with the following commands listed:

```
ls                  Lists all files in the application in ascending order.
lsdir               List all directories and subdirectories.

cd <file_name>      Change context to the specified directory.
cd ..               Change context to parent directory.
cd root             Change context to root directory.

mkdir <path>        Creates new subdirectory(s) in the current directory.
rmdir <path>        Deletes a directory or path if it exists in the current directory and all of it's subdirectories and files.

search <file_name>  Searches for a file and displays the path if the file exists.
add <file_name>     Adds the specified file to the current directory.
read <file_name>    Shows the content of the specified file if it exists.
delete <file_name>  Deletes the specified file from the current directory.

exit                Exits the program.
help                Display available commands.
```
The app validates all user input, and the user can enter the `help` command at any time to see the commands again. The application will also not exit until the user enters `exit`, even in the event of an operation failing.

The user can change the current context to any directory in the `root` folder using `cd`. The user is not able to change to a directory that is a parent of the `root` directory.

The app does not support writing to/editing files except for when they are first added through the `add` command. However if the files are edited and saved outside the application, these changes are reflected in the app and can be read by entering `read`.
