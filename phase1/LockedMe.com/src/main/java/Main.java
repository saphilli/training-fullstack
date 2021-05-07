package main.java.com;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.Formatter;

public class Main {
	
	public static final String LOGGER = "Logger";

	public static void main(String[] args) {
		Logger log = Logger.getLogger(LOGGER);
		log.setLevel(Level.INFO);
		
		try {
			Runnable app = new App(new CommandLineParser(), LOGGER);
			log.info("Starting application...");
			app.run();
		}
		catch(Exception e) {
			log.log(Level.SEVERE,"An exception occurred:", e);
		}
	}

}
