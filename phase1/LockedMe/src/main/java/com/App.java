package com;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class App implements Runnable {
	
	private static final String DEV = "Sarah Phillips" ;
	private static final String APP_NAME = "LockedMe";
	private static final String ROOT_FOLDER = "root";
	private CommandLineParser cl;
	private FileHandler fileSystem;
	private Logger logger;
	private Scanner scan;
	
	public App(CommandLineParser parser, String loggerName) {
		cl = parser;
		scan = new Scanner(System.in);
		logger = Logger.getLogger(loggerName);
		fileSystem = new FileHandler(ROOT_FOLDER,loggerName);
	}

	@Override
	public void run() {
		startUp();
		var running = true;
		while(running) {
			System.out.print("\n\\"+fileSystem.getContext()+">  ");
			var args = cl.parseOption(scan.nextLine());
			if(args.length == 0) {
				System.out.println("Enter 'help' to view list of possible commands");
				continue;
			}
			switch(args[0]) {
			case "exit":
				logger.info("Exiting the application...");
				running = false;
				break;
			case "cd":
				fileSystem.changeDirectory(args[1]);
				break;
			case "mkdir":
				addDirectory(args[1]);
				break;
			case "rmdir":
				removeDirectory(args[1]);
				break;
			case "lsdir":
				System.out.print(fileSystem.listAllDirectories());
				break;
			case "add":
				addFile(args[1]);
				break;
			case "ls":
				System.out.print(fileSystem.listAllFiles());
				break;
			case "search":
				searchFile(args[1]);
				break;
			case "read":
				readFile(args[1]);
				break;
			case "delete":
				removeFile(args[1]);
				break;
			case "help":
				cl.printOptions();
				break;
			}
		}
	}
	
	private void startUp() {
		logger.info("Application running");
		System.out.println("\nWelcome to "+ APP_NAME +"\nDeveloped by: "+DEV);
		cl.printOptions();
	}
	
	private void addFile(String fileName) {
		var created = fileSystem.addFile(fileName);
		if(created) System.out.println("File was successfully added. Path: "+fileSystem.getPath(fileName));
		else System.out.println("Something went wrong when creating the file.");
		System.out.println("Write to the file? (Y/N)");
		if(cl.parseYes(scan)) {
			System.out.print("Type some words, then when you're happy press \"Enter\"\n");
			var text = scan.nextLine();
			try {
				fileSystem.writeToFile(fileName, text);
				System.out.println("Successfully wrote to the file.");
			}catch (FileNotFoundException e) {
				System.out.print("Failed to write to "+fileName+" as the file was not found.");
			}catch (IOException e){
				var message = "Failed to write to the file " + fileName+", an exception was thrown during the operation: {0}";
				logger.log(Level.SEVERE,message,e.toString());
			}
		}
	}
	
	private void removeFile(String fileName) {
		try {
			fileSystem.deleteFile(fileName);
			System.out.println("Successfully deleted the file "+fileName);
		}catch (DirectoryNotEmptyException e) {
			System.out.println("Failed to delete the directory "+fileName+" as it contains files.");
		}catch (NoSuchFileException e) {
			System.out.println("Failed to delete the file "+fileName+" as it does not exist.");
		}catch (IOException e) {
			var message = "Failed to delete the file " + fileName+", an exception was thrown during the operation: {0}";
			logger.log(Level.SEVERE,message,e.toString());
		}
	}
	
	private void searchFile(String fileName) {
		var path = fileSystem.getPath(fileName);
		if(!Helpers.isNullOrEmpty(path)) {
			System.out.println(fileName + " was found at "+path);
			System.out.println("Read the file? (Y/N)");
			if(cl.parseYes(scan)) {
				readFile(fileName);
			}
		}else {
			System.out.println("The file "+ fileName + " does not exist.");
		}
	}
	
	private void readFile(String fileName) {
		System.out.println("Reading from the file "+fileName+"....");
		try {
			var text = fileSystem.readFile(fileName);
			if(Helpers.isNullOrEmpty(text)) System.out.print("File contains no readable content");
			else System.out.println(text);
		} catch (IOException e) {
			var message = "An exception occurred while attempting to read from the file "+fileName+": {0}";
			logger.log(Level.SEVERE,message,e.toString());
			System.out.print("Failed to read the file.");
		}
	}
	
	private void removeDirectory(String directory) {
		try{
			var context = fileSystem.getContext();
			var path = Paths.get(context,directory).toString();
			fileSystem.deleteDirectory(path);
			System.out.println("Successfully deleted directory "+directory);
		}catch(IOException e) {
			var message = "Failed to delete the directory " + directory+", an exception was thrown during the operation: {0}";
			logger.log(Level.SEVERE,message,e.toString());
		}
	}
	
	private void addDirectory(String path) {
		var created = fileSystem.createDirectory(path);
		if(!created) {
			System.out.println("The specified path was not created");
		}
		System.out.println("The path "+path+" was created");
	}
	
	
	
}
