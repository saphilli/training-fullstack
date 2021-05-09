package com;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
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
	
	
	public void changeDirectory(String directory) {
		var dir = new File(directory);
		if(directory.equals(root.getName())) {
			setContext(dir);
		}else if(directory.equals("..")){
			//travel back to parent directory
			if(context != root) {
				setContext(context.getParentFile());
			}else {
				System.out.print("root has no parent directory.");
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
			fileMap = readAllFiles(context);
			System.out.println("Current directory: "+context);
		} else {
			System.out.print(directory + " is not an existing directory.");
		}
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

	//think about how the IOException should be handled
	public SortedMap<String, File> readAllFiles(File context) {
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
            logger.warning("An exception occurred when reading files from the current directory:" + e.toString());
		}
		return fileTree;
	}
		
	public Iterator<String> listFiles() {
		return fileMap.keySet().iterator();
	}
    
	//public createDirectory() {}
	
	//public addFile(Directory,File) {
		
	
}
