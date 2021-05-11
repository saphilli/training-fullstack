package com;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


public class FileHandler {

	private SortedMap<String,File> fileMap;
	private SortedSet<String> directorySet;
	private Logger logger;
	private File context;
	private File root;
	
	public FileHandler(String rootFolder, String loggerName) {
		logger = Logger.getLogger(loggerName);
		root = getRootDirectory(rootFolder);
		logger.log(Level.INFO, "Root directory is located at {0}", root.getAbsolutePath());	
		context = root;
		fileMap = new TreeMap<>();
		directorySet = new TreeSet<>();
		readAllFiles(context);
	}
	
	public void changeDirectory(String directory) {
		var dir = new File(directory);
		if(dir.getName().equals(root.getName())) {
			setContext(dir);
		}else if(directory.equals("..")){
			//travel back to parent directory
			if(!context.equals(root)) {
				setContext(context.getParentFile());
			}else {
				System.out.println("\\root has no parent directory.");
			}
		}else {
			//assume directory is a child of the current directory
			var path = Paths.get(context.getName(),directory).toString();
			dir = new File(path);
			setContext(dir);
		}
	}
	
	private void setContext(File directory) {
		if(directory.isDirectory()) {
			context = directory;
		} else {
			System.out.println(directory + " is not an existing directory.");
		}
	}
	
	private void readAllFiles(File context) {
		Path dir = context.toPath();
		try(var paths = Files.walk(dir)){
			paths.forEach(path -> {
				var file = path.toFile();
				if (!file.isDirectory()) {
		            fileMap.put(file.getName(),file);
				}else {
					directorySet.add(file.getPath());
				}
			});
		} catch (IOException e) {
            logger.log(Level.WARNING,"An exception occurred when reading files from the current directory:{0}", e.toString());
		}
	}
	
	public void openFile(String fileName) {
		var exists = fileMap.containsKey(fileName);
		if(exists) {
			var file = fileMap.get(fileName);
			if(Desktop.isDesktopSupported()) {
				var desktop = Desktop.getDesktop();
				try {
					desktop.open(file);
				} catch (IOException e) {
					logger.log(Level.SEVERE,"An exception was thrown while attempting to open the file: {0}",e);
				}
			}
		}else {
			System.out.println("No file named "+fileName+" in the current directory or any of it's subdirectories.");
		}
	}
	
	public String getContext() {
		return context.getPath();
	}
	
	public int numberOfFiles() {
		return fileMap.size();
	}
	
	public File getRootDirectory(String rootName) {
		var rootDir = new File(rootName);
		if(!rootDir.exists()) {
			rootDir.mkdir();
		}
		return rootDir;
	}

	public String listAllFiles() {
		var n = numberOfFiles() + " files: \n";
		var files = fileMap.keySet().iterator();
		var fileList = new StringBuilder();
		fileList.append(n);
		while(files.hasNext()) {
			fileList.append("     "+files.next()+"\n");
		}
		return fileList.toString();
	}
	
	public String listAllDirectories() {
		var dirList = new StringBuilder();
		for(String dir : directorySet) {
			dirList.append(dir+"\n");
		}
		return dirList.toString();
		
	}
	
	public boolean createDirectory(String path) {
		var fullPath = Paths.get(context.getPath(),path).toString();
		var dir = new File(fullPath);
		if(!dir.exists()) {
			return dir.mkdirs();
		}
		logger.log(Level.WARNING,"Path already exists.");
		return false;
	}
	
	public boolean deleteDirectory(String path) throws IOException {
		var directory = new File(path);
	    if(directory.isDirectory()) {
	      File[] files = directory.listFiles();
	      if(files != null) {
	        for(File file : files) {
	          var deleted = deleteDirectory(file.getPath());
	          if(!deleted) return false;
	        }
	      }
	    }
	    Files.delete(Paths.get(path));
	    return true;
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

	public void deleteFile(String fileName) throws IOException {
		var path = getPath(fileName);
		if(Helpers.isNullOrEmpty(path)) {
			throw new NoSuchFileException(fileName);
		}
		Files.delete(Path.of(path));
		fileMap.remove(fileName);
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
