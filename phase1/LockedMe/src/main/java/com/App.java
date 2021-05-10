package com;

import java.util.Scanner;
import java.util.logging.Logger;

class App implements Runnable {
	
	private static final String DEV = "Sarah Phillips" ;
	private static final String APPNAME = "LockedMe";
	private static final String ROOT_FOLDER = "root";
	private CommandLineParser cl;
	private FileHandler fileSystem;
	private Logger log;
	private Scanner scan;
	
	public App(CommandLineParser parser, String loggerName) {
		cl = parser;
		scan = new Scanner(System.in);
		log = Logger.getLogger(loggerName);
		fileSystem = new FileHandler(ROOT_FOLDER,loggerName);
	}

	@Override
	public void run() {
		startUp();
		boolean running = true;
		while(running) {
			System.out.print("\n\\"+fileSystem.getContext()+">  ");
			var args = cl.parseOption(scan.nextLine());
			if(args.length == 0) {
				System.out.println("Enter 'help' to view list of possible commands");
				continue;
			}
			switch(args[0]) {
			case "exit":
				System.out.println("Exiting the application...");
				running = false;
				break;
			case "cd":
				fileSystem.changeDirectory(args[1]);
				break;
			case "add":
				fileSystem.addFile(args[1]);
				break;
			case "ls":
				fileSystem.listAllFiles();
				break;
			case "search":
				fileSystem.searchFile(args[1]);
				break;
			case "open":
				fileSystem.openFile(args[1]);
				break;
			case "delete":
				fileSystem.deleteFile(args[1]);
				break;
			case "help":
				cl.printOptions();
				break;
			}
		}
	}
	
	private void startUp() {
		log.info("Application running");
		System.out.print("\nWelcome to "+ APPNAME +"\nDeveloped by: "+DEV+"\n");
		cl.printOptions();
	}
	
}
