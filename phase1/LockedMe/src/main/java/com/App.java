package com;

import java.util.Scanner;
import java.util.logging.Logger;

class App implements Runnable {
	
	private static final String DEV = "Sarah Phillips" ;
	private static final String APPNAME = "LockedMe";
	private static CommandLineParser cl;
	private static Logger log;
	private Scanner scan;
	
	public App(CommandLineParser parser, String loggerName) {
		cl = parser;
		scan = new Scanner(System.in);
		log = Logger.getLogger(loggerName);
	}

	@Override
	public void run() {
		startUp();
		boolean running = true;
		while(running) {
			var res = cl.parseOption(scan.nextLine());
			if(res.length == 0) {
				System.out.println("Enter 'help' to view list of possible commands");
				continue;
			}
			switch(res[0]) {
			case "exit":
				System.out.println("Exiting the application...");
				running = false;
				break;
			case "add":
				break;
			case "ls":
				break;
			case "open":
				break;
			case "delete":
				break;
			case "help":
				cl.printOptions();
				break;
			}
		}
	}
	
	private void startUp() {
		log.info("Application running");
		System.out.print("Welcome to "+ APPNAME +"\nDeveloped by: "+DEV+"\n");
		cl.printOptions();
	}
	
}
