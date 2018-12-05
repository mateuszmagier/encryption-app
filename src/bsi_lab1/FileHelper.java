/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsi_lab1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP
 */
public class FileHelper {
    
    private FileOutputStream fos;
    private FileInputStream fis;
    
    public FileHelper() {
        fos = null;
        fis = null;
    }
    
    /* saves array of bytes to file */
    public boolean saveFile(byte[] bytes, File file) throws FileNotFoundException {
        if(file == null)
            throw new IllegalArgumentException("Plik nie może być NULLem.");
        if(bytes == null)
            throw new IllegalArgumentException("Tablica bajtów nie może być NULLem.");
        if(file.exists())
            fos = new FileOutputStream(file);
        else
            throw new FileNotFoundException();
        
        try {
            fos.write(bytes); // zapis tablicy bajtów do pliku
            fos.close(); // zamknięcie strumienia
            return true;
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /* reads file - returns array of bytes */
    public byte[] readFile(File file) throws FileNotFoundException, IOException {
        if(file == null)
            throw new IllegalArgumentException();
        byte[] bytes = new byte[(int) file.length()]; // inicjalizacja tablicy bajtów
        if(file.exists())
            fis = new FileInputStream(file);
        else
            throw new FileNotFoundException();
        
        fis.read(bytes); // zapis zawartości pliku do tablicy bajtów
        fis.close(); // zamknięcie strumienia
        
        return bytes;
    }
}
