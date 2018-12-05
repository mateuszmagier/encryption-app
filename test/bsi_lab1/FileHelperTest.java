/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsi_lab1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 * 1) czy konstruktor tworzy obiekt not null
 * 2) wyjątek, jeśli do saveFile przekazano plik jako null
 * 3) wyjątek, jeśli do saveFile przekazano tablicę bajtów jako null
 * 4) saveFile zwraca TRUE, jeśli przekazano poprawne obiekty
 * 5) wyjątek, jeśli przekazany do saveFile plik nie istnieje
 * 6) porównanie tablic bajtów: tablica do zapisania i tablica odczytana z pliku
 * 7) długości pliku przed i po zapisie niepustej tablicy bajtów różnią się
 * 8) długości tablicy bajtów do zapisania i pliku po zapisie są takie same, jeśli przekazano poprawne parametry
 * 9) wyjątek, jeśli do readFile przekazano null
 * 10) brak wyjątku, jeśli do readFile przekazano istniejący plik
 * 11) wyjątek, jeśli przekazany do readFile plik nie istnieje
 * 12) readFile zwróciła pustą tablicę bajtów bajtów, jeśli odczytany plik był pusty
 */
public class FileHelperTest {
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    public File tempFile;
    
    FileHelper fh;
    
    public FileHelperTest() {
    }
    
    @Before
    public void beforeTest() throws IOException {
        fh = new FileHelper();
        tempFile = tempFolder.newFile("xyz");
    }
    
    @Test
    public void testFileHelper_CreateNewObject_NewObjectIsNotNull() {
        assertNotNull(fh);
    }

    /**
     * Test of saveFile method, of class FileHelper.
     */
    
    @Test
    public void testSaveFile_FileObjectIsNull_Exception() throws Exception {
        String msg = "";
        String expectedMsg = "Plik nie może być NULLem.";
        byte[] bytes = "Sample".getBytes();
        try {
            fh.saveFile(bytes, null);
        }
        catch(IllegalArgumentException ex) {
            msg = ex.getMessage();
        }
        assertEquals(expectedMsg, msg);
    }
    
    @Test
    public void testSaveFile_BytesArrayIsNull_Exception() throws Exception {
        String msg = "";
        String expectedMsg = "Tablica bajtów nie może być NULLem.";
        try {
            fh.saveFile(null, tempFile);
        }
        catch(IllegalArgumentException ex) {
            msg = ex.getMessage();
        }
        assertEquals(expectedMsg, msg);
    }
    
    @Test
    public void testSaveFile_FileExistsAndBytesArrayIsNotNull_BytesArraySavedToFile() throws IOException {
        byte[] bytes = "Sample".getBytes();
        
        boolean result = fh.saveFile(bytes, tempFile);
        assertTrue(result);
    }
    
    @Test(expected=FileNotFoundException.class)
    public void testSaveFile_FileDoesntExist_Exception() throws IOException {
        byte[] bytes = "Sample".getBytes();
        tempFile.delete();
        fh.saveFile(bytes, tempFile);
    }
    
    @Test
    public void testSaveAndReadFile_CorrectParameters_SavedAndReadedBytesArraysEquals() throws FileNotFoundException, IOException {
        byte[] bytes = "Sample".getBytes();
        fh.saveFile(bytes, tempFile);
        byte[] content = fh.readFile(tempFile);
        assertArrayEquals(bytes, content);
    }
    
    @Test
    public void testSaveFile_CorrectParameters_FileModified() throws FileNotFoundException {
        byte[] bytes = "Sample".getBytes();
        long length_before = tempFile.length();
        fh.saveFile(bytes, tempFile);
        long length_after = tempFile.length();
        assertNotEquals(length_before, length_after);
    }
    
    @Test
    public void testSaveFile_CorrectParameters_BytesArrayLengthEqualsToFileLength() throws FileNotFoundException {
        byte[] bytes = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.".getBytes();
        fh.saveFile(bytes, tempFile);
        long file_length = tempFile.length();
        long array_length = bytes.length;
        assertEquals(array_length, file_length);
    }
    
    /**
     * Test of readFile method, of class FileHelper.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testReadFile_FileObjectIsNull_Exception() throws IOException {
        fh.readFile(null);
    }
    
    @Test
    public void testReadFile_FileExists_NoException() throws IOException {
        fh.readFile(tempFile);
    }
    
    @Test(expected=FileNotFoundException.class)
    public void testReadFile_FileDoesntExist_Exception() throws IOException {
        tempFile.delete();
        fh.readFile(tempFile);
    }
    
    @Test
    public void testReadFile_FileIsEmpty_ShouldReturnNullByteArray() throws IOException {
        byte[] result = fh.readFile(tempFile);
        long length = result.length;
        assertEquals(0, length);
    }
}
