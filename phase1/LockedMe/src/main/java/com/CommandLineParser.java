package com;

import java.util.Map;
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
		options.put("delete",1);
		options.put("open",1);
		options.put("exit",0);
		options.put("help",0);
	}
	
	private static String help = "ls                  Lists all files in the application in ascending order\n"
			+ "cd <file_name>      Change context to the specified directory\n"
			+ "cd ..               Change context to parent directory"
			+ "cd root             Change context to root directory"
			+ "search <file_name>  Searches for a file and displays the path if the file exists\n"
			+ "add <file_name>     Adds the specified file to the current directory\n"
			+ "open <file_name>    Opens the specified file if it exists in the current directory"
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
		var args = option.split(" ");
		var command = args[0];
		if(options.get(command.toLowerCase()) == null) {
			System.out.println("Command not recognized: "+command);
			return new String[0];
	    }
		else if((args.length == 2 && Helpers.equalsAny(command,"exit","ls","help")) || args.length>2) {
			System.out.println("Too many arguments entered for command: " + command);
			return new String[0];
		}
		return args;
	}
}
