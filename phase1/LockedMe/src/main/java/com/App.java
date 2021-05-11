package com;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class App implements Runnable {
	
	private static final String DEV = "Sarah Phillips" ;
	private static final String APPNAME = "LockedMe";
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
				fileSystem.addFile(args[1]);
				break;
			case "ls":
				System.out.print(fileSystem.listAllFiles());
				break;
			case "search":
				searchFile(args[1]);
				break;
			case "open":
				fileSystem.openFile(args[1]);
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
			System.out.println("Open the file? (Y/N)");
			if(cl.parseYes(scan)) {
				System.out.println("Opening file "+fileName+"....");
				fileSystem.openFile(fileName);
			}
		}else {
			System.out.println("The file "+ fileName + " does not exist.");
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
	
	private void startUp() {
		logger.info("Application running");
		System.out.println("\nWelcome to "+ APPNAME +"\nDeveloped by: "+DEV);
		cl.printOptions();
	}
	
}
