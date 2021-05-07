package com;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;


public class FileSystem {

	private static FileSystem instance = null;
	
	private TreeMap<String,Directory> directoryTree;
	
	private FileSystem(String rootFolder) {
		directoryTree = new TreeMap<>();
		var root = new File(rootFolder);
		if(!root.exists()) root.mkdir();
	}
	
	public static FileSystem create(String rootPath) {
		if(instance == null) {
			instance = new FileSystem(rootPath);
		}
		return instance;
	}

	//public createDirectory() {}
	
	//public addFile(Directory,File)
	//
}
