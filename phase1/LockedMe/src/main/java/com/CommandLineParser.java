package com;


import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

class CommandLineParser {
	
	private static Set<String> options;
	//k=command v=number of args
	static {
		options = new HashSet<>();
		options.add("cd");
		options.add("ls"); 
		options.add("search");
		options.add("add");
		options.add("mkdir");
		options.add("rmdir");
		options.add("lsdir");
		options.add("delete");
		options.add("read");
		options.add("exit");
		options.add("help");
	}
	
	private static String help = "ls                  Lists all files in the application in ascending order.\n"
			+ "lsdir               List all directories and subdirectories.\n\n"
			+ "cd <file_name>      Change context to the specified directory.\n"
			+ "cd ..               Change context to parent directory.\n"
			+ "cd root             Change context to root directory.\n\n"
			+ "mkdir <path>        Creates new subdirectory(s) in the current directory.\n"
			+ "rmdir <name>        Deletes a directory if it exists in the current directory and all of it's subdirectories and files.\n\n"
			+ "search <file_name>  Searches for a file and displays the path if the file exists.\n"
			+ "add <file_name>     Adds the specified file to the current directory.\n"
			+ "read <file_name>    Shows the content of the specified file if it exists.\n"
			+ "delete <file_name>  Deletes the specified file from the current directory.\n\n"
			+ "exit                Exits the program.\n"
			+ "help                Display available commands.";
	
	public void printOptions() {
		System.out.println("\nAvailable commands:\n\n"+help+"\n\n");
	}
	
	public String[] parseOption(String option) {
		if(Helpers.isNullOrEmpty(option)) {
			System.out.println("No command was entered.");
			return new String[0];
		}
		var args = option.stripLeading().split(" ");
		var command = args[0];
		if(!options.contains(command.toLowerCase())) {
			System.out.println("Command not recognized: "+command);
			return new String[0];
	    }
		else if((args.length == 2 && Helpers.equalsAny(command,"exit","ls","lsdir","help")) || args.length>2) {
			System.out.println("Too many arguments entered for command: " + command);
			return new String[0];
		}
		return args;
	}
	
	public boolean parseYes(Scanner scan) {
		while(true) {
			var option = scan.nextLine();
			switch(option.toLowerCase().trim()) {
			case "y":
				return true;
			case "n":
				return false;
			default:
				System.out.print("Unrecognized input. Only Y or N is accepted.");	
			}
		}
	}
}
