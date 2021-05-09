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
	private String context;
	
	public FileHandler(String rootFolder, String loggerName) {
		logger = Logger.getLogger(loggerName);
		var root = getRootDirectory(rootFolder).getAbsolutePath();
		logger.log(Level.INFO, "Root directory is located at {0}", root);	
		setContext(rootFolder);
	}
	
	public void setContext(String directory) {
		context = directory;
		try {
			fileMap = readAllFiles(context);
		} catch (IOException e) {
			logger.warning("An exception occurred when reading files from the root folder:" + e.getMessage() + e.getStackTrace());
		}
	}
	
	public String getContext() {
		return context;
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

	public SortedMap<String, File> readAllFiles(String rootFolder) throws IOException {
		Path dir = Paths.get(rootFolder);
		var fileTree = new TreeMap<String,File>();
		try(var paths = Files.walk(dir)){
			paths.forEach(path -> {
				var file = path.toFile();
				if (!file.isDirectory()) {
		            fileTree.put(file.getName(),file);
				}
			});
		}
		return fileTree;
	}
		
	public Iterator<String> listFiles() {
		return fileMap.keySet().iterator();
	}
    
	//public createDirectory() {}
	
	//public addFile(Directory,File) {
		
	
}
