package app;

import java.util.Scanner;
import java.util.logging.Level;

class App implements Runnable {
	
	private static final String DEV = "Sarah Phillips" ;
	private static final String APPNAME = "LockedMe";
	private CommandLineParser cl;
	private Scanner scan;
	
	public App(CommandLineParser parser) {
		cl = parser;
		scan = new Scanner(System.in);
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
		System.out.print("Welcome to "+ APPNAME +"\nDeveloped by: "+DEV+"\n");
		cl.printOptions();
	}
	
}
