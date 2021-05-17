package com;

import org.junit.Assert.*;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;


public class FileHandlerTests {
	
	private final String BASE_DIR = System.getProperty("user.dir");
	private final String ROOT = "test-root";
	private final String ROOT_PATH = Paths.get(BASE_DIR,ROOT).toString();
	private final String DIR_WITH_FILES = "directory-with-files";
	private final String SUBDIR ="subdirectory";
	private final String DIR_WITH_FILES_PATH = Paths.get(BASE_DIR,DIR_WITH_FILES).toString();
	private final String SUBDIR_PATH = Paths.get(DIR_WITH_FILES,SUBDIR).toString();
	private final String LOGGER = "Logger";

	private FileHandler fileSystem;

	@Before
	public void setUp() throws Exception {
		//set up test directory with existing files
		var root = new File(DIR_WITH_FILES);
		var subDir = new File(SUBDIR_PATH);
		if(!root.exists()) {
			root.mkdir();		
			for(char c = 'x';c<='z';c++) {
				var name = c+".txt";
				var path = Paths.get(DIR_WITH_FILES,name).toString();
				var file = new File(path);
				file.createNewFile();
			}
		}
		if(!subDir.exists()) {
			subDir.mkdir();
			for(char c='a';c<='c';c++) {
				var name = c+".txt";
				var path = Paths.get(SUBDIR_PATH,name).toString();
				var file = new File(path);
				file.createNewFile();
			}
		}
	}

	@After
	public void tearDown() throws Exception {
		var root = new File(ROOT_PATH);
		var dirWithFiles = new File(DIR_WITH_FILES_PATH);
		deleteDirectory(root);
		deleteDirectory(dirWithFiles);
		assertEquals(false,root.exists());
		assertEquals(false,dirWithFiles.exists());
	}
	
	public void deleteDirectory(File directory) {
	    if(directory.isDirectory()) {
	      File[] files = directory.listFiles();

	      if(files != null) {
	        for(File file : files) {
	          deleteDirectory(file);
	        }
	      }
	    }
	    directory.delete();
	}
		
	@Test
	public void testRootCreateRootDirectory() {
		fileSystem = new FileHandler(ROOT,LOGGER);
		var root = fileSystem.getRootDirectory(ROOT);
		assertEquals("Root file not created",true,root.exists());
	}
	
	@Test
	public void testChangeDirectoryChild() {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var expectedPath = Paths.get(DIR_WITH_FILES,SUBDIR).toString();
		fileSystem.changeDirectory(SUBDIR);
		var newContext = fileSystem.getContext();
		assertEquals(expectedPath,newContext);
	}
	
	@Test
	public void testChangeToRootDirectory() {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		fileSystem.changeDirectory(SUBDIR);
		fileSystem.changeDirectory(DIR_WITH_FILES);
		var newContext = fileSystem.getContext();
		assertEquals(DIR_WITH_FILES,newContext);
	}
	
	@Test
	public void testChangeToParentDirectory() {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		fileSystem.changeDirectory(SUBDIR);
		fileSystem.changeDirectory("..");
		var newContext = fileSystem.getContext();
		assertEquals(DIR_WITH_FILES,newContext);
	}
	
	@Test
	public void testDoesNotChangeToInvalidDirectory() {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		fileSystem.changeDirectory("InvalidDirectory");
		var newContext = fileSystem.getContext();
		assertEquals(DIR_WITH_FILES,newContext);
	}
	
	@Test 
	public void testDoesNotChangeToRootParent() {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		fileSystem.changeDirectory("..");
		var newContext = fileSystem.getContext();
		assertEquals(DIR_WITH_FILES,newContext);
	}
	
	@Test
	public void testDoesNotSetContextToFile() {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		fileSystem.changeDirectory("a.txt");
		var newContext = fileSystem.getContext();
		assertEquals(DIR_WITH_FILES,newContext);
	}
	
	@Test
	public void testListAllFiles() {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var expectedList ="6 files: \n"
			+"     a.txt\n"
			+"     b.txt\n"
			+"     c.txt\n"
			+"     x.txt\n"
			+"     y.txt\n"
			+"     z.txt\n";
		assertEquals(expectedList,fileSystem.listAllFiles());
	}

	@Test
	public void testNumberOfFiles() {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var n = fileSystem.numberOfFiles();
		assertEquals(6,n);
	}
	
	@Test
	public void testOpenFile() {
		
	}
	
	@Test
	public void testAddFile() {
		
	}
	
	@Test
	public void testDeleteExistingFile() throws IOException {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var fileName = "a.txt";
		fileSystem.deleteFile(fileName);
		assertEquals("",fileSystem.getPath(fileName));
	}
	
	@Test(expected=NoSuchFileException.class)
	public void testDeleteNonexistantFile() throws IOException {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var fileName = "NonexistantFile";
		fileSystem.deleteFile(fileName);
	}
	
	@Test(expected=DirectoryNotEmptyException.class)
	public void testDeleteNotEmptyDirectory() throws IOException {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var fileName = SUBDIR;
		fileSystem.deleteFile(fileName);
	}
	
	@Test
	public void testGetPathOfExistingFile() {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var fileName = "a.txt";
		var path = fileSystem.getPath(fileName);
		var expectedPath = SUBDIR_PATH +"\\"+fileName;
		assertEquals(expectedPath,path);
	}
	
	@Test
	public void testPathOfNonExistantFileIsEmpty() {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var path = fileSystem.getPath("NonExistantFile");
		assertEquals("",path);
	}
	
	@Test
	public void testHasExistingFile() {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var fileName = "a.txt";
		var hasFile = fileSystem.hasFile(fileName);
		assertEquals(true,hasFile);
	}
	
	@Test
	public void testDoesNotHaveNonexistantFile() {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var fileName = "NonexistantFile";
		var hasFile = fileSystem.hasFile(fileName);
		assertEquals(false,hasFile);
	}
}
