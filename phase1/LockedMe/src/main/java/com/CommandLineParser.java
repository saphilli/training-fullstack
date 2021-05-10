package com;

import java.util.Map;
import java.util.HashMap;

class CommandLineParser {
	
	private static Map<String,String> options;
	static {
		options = new HashMap<>();
		options.put("cd", "Change context to the specified directory");
		options.put("ls","Lists all files in the application in ascending order"); 
		options.put("search","Searches for a file and displays the path if the file exists");
		options.put("add","Adds the specified file to the current directory");
		options.put("delete","Deletes the specified file from the current directory");
		options.put("open","Opens the specified file if it exists in the current directory");
		options.put("exit","Exits the program");
		options.put("help","Display options");
	}
	private static String help = buildHelpString();
	
	private static String buildHelpString() {
		var str = new StringBuilder();
		for(var entry:options.entrySet()) {
			var cmd = entry.getKey();
			if(Helpers.equalsAny(cmd,"cd","add","delete","open","search")) {
				cmd += " <file_name>";
			}
			var description = entry.getValue();
			var offset = 20 - cmd.length();
			str.append("\n"+cmd + " ".repeat(offset) + description);
		}
		return str.toString();
	}

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
