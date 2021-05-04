package app;

public class Main {

	public static void main(String[] args) {
		Runnable app = new App(new CommandLineParser());
		Thread thread = new Thread(app);
		thread.start();
	}

}
