/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsi_lab1;

import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 *
 * @author HP
 */
public class Encryption {
    private KeyGenerator keygen; // generator klucza
    private SecretKey key; // klucz
    private Cipher c; // obiekt klasy odpowiedzialnej za szyfrowanie

    public Encryption(String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException {
        // pobranie instancji generatora kluczy dla algorytmu 3DES
        this.keygen = KeyGenerator.getInstance(algorithm);
        //wygenerowanie klucza 3DES
        this.key = keygen.generateKey();
        try {
            this.c = Cipher.getInstance(algorithm);
        }
        catch(NoSuchPaddingException ex) {
            this.c = null;
        }
    }
    
    public byte[] crypt(int cipherMode, byte[] bytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if(bytes == null)
            throw new IllegalArgumentException("Tablica bajtów do zaszyfrowania nie może być NULL");
        
        if(cipherMode != 1 && cipherMode != 2)
            throw new InvalidParameterException("Nieprawidłowy tryb obiektu Cipher.");
        
        c.init(cipherMode, key); // inicjalizacja obiektu Cipher
        
        byte[] output = c.doFinal(bytes); // szyfrowanie/odszyfrowanie tablicy bajtów
        
        return output;
    }
}