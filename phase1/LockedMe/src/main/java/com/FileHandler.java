package com;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.logging.Logger;
import java.util.logging.Level;
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
	
	private void setContext(File directory) {
		if(directory.isDirectory()) {
			context = directory;
		} else {
			System.out.println(directory + " is not an existing directory.");
		}
	}
	
	public String getContext() {
		return context.getPath();
	}
	
	public String getPath(String fileName) {
		if(hasFile(fileName)) {
			return fileMap.get(fileName).getPath();
		}
		return "";	
	}
	
	public String getFileNameFromPath(String path) {
		var fileName = "";
		try {
			fileName = Path.of(path).getFileName().toString();
		}catch(Exception e) {
			logger.warning("Couldn't get fileName from path entered.");
		}
		return fileName;
	}
	
	public boolean hasFile(String fileName) {
		return fileMap.containsKey(fileName);
	}
	
	public File getFile(String fileName) {
		return fileMap.get(fileName);
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
	
	public int numberOfFiles() {
		return fileMap.size();
	}
	
	public int numberOfDirectories() {
		return directorySet.size();
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
		var n = numberOfDirectories() + " directories: \n";
		var dirList = new StringBuilder();
		dirList.append(n);
		for(String dir : directorySet) {
			dirList.append(dir+"\n");
		}
		return dirList.toString();
		
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
			var path = Paths.get(context.getPath(),directory).toString();
			dir = new File(path);
			setContext(dir);
		}
	}
	
	public boolean createDirectory(String path) {
		var fullPath = Paths.get(context.getPath(),path).toString();
		var dir = new File(fullPath);
		if(!dir.exists()) {
			var created = dir.mkdirs();
			if(!created) return false;
			directorySet.add(dir.getPath());
			return true;
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
	    else if(directory.isFile()) {
	    	var fileName = directory.getName();
	    	fileMap.remove(fileName);
	    }
	    Files.delete(Paths.get(path));
	    directorySet.remove(path);
	    return true;
	}
	
	public String addFile(String path) {
		var created = false;
		var fileName = getFileNameFromPath(path);
		Path pathToFile = null;
		if(hasFile(fileName)) {
			var existingPath = getPath(fileName);
			logger.log(Level.WARNING,"{0} already exists in the application at "+ existingPath+" and cannot be overwritten",fileName);
			path = null;
		}else {
			try {
				var fullPath = Paths.get(context.getPath(),path);
				pathToFile = fullPath.getParent();
				Files.createDirectories(pathToFile);
				var file = new File(fullPath.toString());
				created = file.createNewFile();
				if(created) {
					fileMap.put(fileName,file);
				}
				else {
					logger.warning("Failed to add the file");
					path = null;
				}
			} catch(FileAlreadyExistsException e) {
				logger.log(Level.WARNING,"An exception occurred when creating the path: {0}",pathToFile);
				path = null;
			}
			catch (IOException e) {
				logger.log(Level.WARNING,"An exception occurred when creating the file {0}",e.toString());
				path = null;
			}
		}
		return path;
	}
	
	public void writeToFile(String path, String text) throws IOException{
		var fileName = getFileNameFromPath(path);
		var file = getFile(fileName);
		if(file == null) throw new FileNotFoundException();
		try(var writer = new FileWriter(file)){
			writer.write(text);
			writer.flush();
		}
	}
	
	public String readFile(String path) throws IOException {
		var fileName = getFileNameFromPath(path);
		var file = getFile(fileName);
		var buffer = "";
		if(file == null) throw new FileNotFoundException();
		try(var inputStream = new FileInputStream(file)){
			var dataInStream = new DataInputStream(inputStream);
			var size = inputStream.available();
			var bytes = new byte[size];
			var sizeRead = dataInStream.read(bytes);
			if(sizeRead == 0) {
				return "";
			}
			buffer = new String(bytes);
		}
		return buffer;
	}

	public void deleteFile(String path) throws IOException {
		var fileName = getFileNameFromPath(path);
		var fullPath = getPath(fileName);
		if(Helpers.isNullOrEmpty(fullPath)) {
			throw new NoSuchFileException(path);
		}
		Files.delete(Path.of(fullPath));
		fileMap.remove(fileName);
	}
	
	
}
