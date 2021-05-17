package com;

import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;

class CommandLineParser {
	
	private static Map<String,Integer> options;
	//k=command v=number of args
	static {
		options = new HashMap<>();
		options.put("cd", 1);
		options.put("ls",0); 
		options.put("search",1);
		options.put("add",1);
		options.put("mkdir", 1);
		options.put("rmdir", 1);
		options.put("lsdir", 0);
		options.put("delete",1);
		options.put("open",1);
		options.put("exit",0);
		options.put("help",0);
	}
	
	private static String help = "ls                  Lists all files in the application in ascending order\n"
			+ "lsdir               List all directories and subdirectories\n"
			+ "cd <file_name>      Change context to the specified directory\n"
			+ "cd ..               Change context to parent directory\n"
			+ "cd root             Change context to root directory\n"
			+ "mkdir <path>        Creates new subdirectory(s) in the current directory\n"
			+ "rmdir <name>        Deletes a directory if it exists in the current directory and all of it's subdirectories and files.\n"
			+ "search <file_name>  Searches for a file and displays the path if the file exists\n"
			+ "add <file_name>     Adds the specified file to the current directory\n"
			+ "open <file_name>    Opens the specified file if it exists in the current directory\n"
			+ "delete <file_name>  Deletes the specified file from the current directory\n"
			+ "exit                Exits the program\n"
			+ "help                Display options\n";
	
	public void printOptions() {
		System.out.println("\nEnter one of the below commands:\n"+help+"\n");
	}
	
	public String[] parseOption(String option) {
		if(Helpers.isNullOrEmpty(option)) {
			System.out.println("No command was entered.");
			return new String[0];
		}
		var args = option.stripLeading().split(" ");
		var command = args[0];
		if(options.get(command.toLowerCase()) == null) {
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
