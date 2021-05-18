package com;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.NoSuchFileException;

@RunWith(JUnitParamsRunner.class)
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
	@Parameters({"..","InvalidDirectory","a.txt"})
	public void testDoesNotChangeToInvalidDirectoriesFromRoot(String directory) {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		fileSystem.changeDirectory(directory);
		var newContext = fileSystem.getContext();
		assertEquals(DIR_WITH_FILES,newContext);
	}
	
	@Test
	public void testCreateNewDirectory() {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var path = Paths.get(SUBDIR, "NewDirectory").toString();
		var fullPath = Paths.get(SUBDIR_PATH,"NewDirectory").toString();
		var file = new File(fullPath);
		
		var created = fileSystem.createDirectory(path);
		
		assertTrue(created);
		assertTrue(file.exists());
		assertTrue(file.isDirectory());
	}
	
	@Test
	public void testDontCreateDirectoryThatExists() {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var path = SUBDIR;
		var file = new File(SUBDIR_PATH);
		
		var created = fileSystem.createDirectory(path);
		
		assertFalse(created);
		assertTrue(file.isDirectory());
		assertTrue(file.exists());
	}
	
	@Test
	public void testListAllDirectories() {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		
		var listString = fileSystem.listAllDirectories();
		
		assertEquals("2 directories: \ndirectory-with-files\n"
				+ "directory-with-files\\subdirectory\n",listString);
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
	public void testAddAndReadFile() throws IOException {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var fileName = "new.txt";
		var filePath = Paths.get(DIR_WITH_FILES_PATH +"/"+ fileName).toString();
		var file = new File(filePath);
		var text = "Some sample text";
		
		fileSystem.addFile(fileName);
		fileSystem.writeToFile(fileName, text);
		var actualText = fileSystem.readFile(fileName);
		
		assertTrue(file.exists());
		assertEquals(text,actualText);
	}
	
	@Test
	public void testReadEmptyFile() throws IOException {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var fileName = "a.txt";

		var actualText = fileSystem.readFile(fileName);
		
		assertEquals("",actualText);
	}
	
	@Test
	public void testDeleteExistingFile() throws IOException {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var fileName = "a.txt";
		fileSystem.deleteFile(fileName);
		assertEquals("",fileSystem.getPath(fileName));
	}
	
	@Test
	public void testDeleteNonexistantFile() throws IOException {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var fileName = "NonexistantFile";
		var exceptionThrown = "";
		
		try {
			fileSystem.deleteFile(fileName);
		} catch (NoSuchFileException e) {
			exceptionThrown = "NoSuchFileException";
		} catch(Exception e) {
			exceptionThrown = e.getClass().getName();
		}
		
		assertEquals("NoSuchFileException",exceptionThrown);
	}
	
	@Test
	public void testDeleteNonEmptyDirectory() throws IOException {
		var fileSystem = new FileHandler(DIR_WITH_FILES,LOGGER);
		var path = SUBDIR_PATH;
		var file = new File(path);
		
		fileSystem.deleteDirectory(path);
		
		assertFalse(file.exists());
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
