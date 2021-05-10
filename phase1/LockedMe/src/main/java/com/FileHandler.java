package com;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.SortedMap;
import java.util.TreeMap;


public class FileHandler {

	private SortedMap<String,File> fileMap;
	private Logger logger;
	private File context;
	private File root;
	
	public FileHandler(String rootFolder, String loggerName) {
		logger = Logger.getLogger(loggerName);
		root = getRootDirectory(rootFolder);
		logger.log(Level.INFO, "Root directory is located at {0}", root.getAbsolutePath());	
		context = root;
		fileMap = readAllFiles(context);
	}
	
	public void openFile(String fileName) {
		var exists = fileMap.containsKey(fileName);
		if(exists) {
			var file = fileMap.get(fileName);
			var desktop = Desktop.getDesktop();
			try {
				desktop.open(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			System.out.print("No file named "+fileName+" in the current directory or any of it's subdirectories.");
		}
	}
	
	public void changeDirectory(String directory) {
		var dir = new File(directory);
		if(directory.equals(root.getName())) {
			setContext(dir);
		}else if(directory.equals("..")){
			//travel back to parent directory
			if(context != root) {
				setContext(context.getParentFile());
			}else {
				System.out.print("\root has no parent directory.");
			}
		}else {
			//assume directory is a child of the current directory
			var path = Paths.get(context.getName(),directory).toString();
			dir = new File(path);
			setContext(dir);
		}
	}
	
	private void setContext(File directory) {
		if(directory.exists() && directory.isDirectory()) {
			context = directory;
			//keep track of all files in app regardless of context?
			//fileMap = readAllFiles(context);
			System.out.println("Current directory: "+context);
		} else {
			System.out.print(directory + " is not an existing directory.");
		}
	}
	
//think about how the IOException should be handled
	private SortedMap<String, File> readAllFiles(File context) {
		Path dir = context.toPath();
		var fileTree = new TreeMap<String,File>();
		try(var paths = Files.walk(dir)){
			paths.forEach(path -> {
				var file = path.toFile();
				if (!file.isDirectory()) {
		            fileTree.put(file.getName(),file);
				}
			});
		} catch (IOException e) {
            logger.log(Level.WARNING,"An exception occurred when reading files from the current directory:{0}", e.toString());
		}
		return fileTree;
	}
	
	public String getContext() {
		return context.getPath();
	}
	
	public int numberOfFiles() {
		return fileMap.size();
	}
	
	public File getRootDirectory(String rootName) {
		var root = new File(rootName);
		if(!root.exists()) {
			root.mkdir();
		}
		return root;
	}

	public void listAllFiles() {
		var n = numberOfFiles();
		System.out.println(n+" files:");
		var files = fileMap.keySet().iterator();
		while(files.hasNext()) {
			System.out.println(files.next());
		}
	}
    
	public void addFile(String fileName) {
		if(hasFile(fileName)) {
			logger.log(Level.WARNING,"{0} already exists and cannot be overwritten",fileName);
		}else {
			var created = false;
			try {
				var path = Paths.get(context.getPath(),fileName).toString();
				var file = new File(path);
				created = file.createNewFile();
				if(created) {
					logger.info("File was added.");
					fileMap.put(fileName,file);
				}
				else logger.warning("Failed to add the file");
			} catch (IOException e) {
				logger.log(Level.WARNING,"An exception occurred when creating the file {0}",e.toString());
			}
		}
	}
	
	public void searchFile(String fileName) {
		var path = getPath(fileName);
		if(!Helpers.isNullOrEmpty(path)) {
			System.out.print(fileName + " was found at "+path);
		}else {
			System.out.print("The file "+ fileName + " does not exist");
		}
	}
	public String getPath(String fileName) {
		if(hasFile(fileName)) {
			return fileMap.get(fileName).getPath();
		}
		return "";	
	}
	
	public boolean hasFile(String fileName) {
		return fileMap.containsKey(fileName);
	}
	
}
