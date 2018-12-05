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
import javax.crypto.NoSuchPaddingException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * 1) porównanie tablic bajtów: tekst oryginalny, tekst oryginalny po zaszyfrowaniu i odszyfrowaniu
 * 2) wyjątek, gdy przekazano null jako tablicę bajtów (do zaszyfrowania)
 * 3) wyjątek, gdy przekazano null jako tablicę bajtów (do odszyfrowania)
 * 4) wyjątek, gdy przekazano niewłaściwy tryb pracy obiektu klasy Cipher
 * 5) tablica bajtów not null, jeśli w parametrze tablica bajtów not null i właściwy tryb Cipher
 * 6) długość tablicy bajtów będąca wynikiem szyfrowania jest podzielna przez 8, jeśli przekazano właściwe parametry
 * 7) wyjątek, jeśli podjęto próbę odszyfrowania tablicy bajtów o długości niepodzielnej przez 8
 * 8) wyjątek, jeśli do konstruktora przekazano niewłaściwą nazwę algorytmu
 * 9) obiekt not null, jeśli do konstruktora przekazano właściwą nazwę algorytmu
 */
public class EncryptionTest {
    
    Encryption enc;
    
    @Before
    public void beforeTest() throws Exception {
        enc = new Encryption("DESede");
    }

    /**
     * Test of crypt method, of class Encryption.
     */
    @Test
    public void testCrypt_TextToEncrypt_DecryptedByteArrayEqualToByteArrayToEncrypt() throws Exception {
        int cipherMode = 1; // encrypt
        byte[] text = "Sample".getBytes();
        byte[] enc_text = enc.crypt(cipherMode, text);
        cipherMode = 2; // decrypt
        byte[] result = enc.crypt(cipherMode, enc_text);
        assertArrayEquals(text, result);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testCrypt_NullBytesArrayToEncrypt_Exception() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        int cipherMode = Cipher.ENCRYPT_MODE;
        enc.crypt(cipherMode, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testCrypt_NullBytesArrayToDecrypt_Exception() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        int cipherMode = Cipher.DECRYPT_MODE;
        enc.crypt(cipherMode, null);
    }
    
    @Test(expected=InvalidParameterException.class)
    public void testCrypt_InvalidCipherMode_Exception() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        int cipherMode = 0;
        byte[] text = "Sample".getBytes();
        enc.crypt(cipherMode, text);
    }
    
    @Test
    public void testCrypt_NotNullBytesArrayOnInput_NotNullBytesArray() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] bytes = "xyz".getBytes();
        byte[] result = enc.crypt(Cipher.ENCRYPT_MODE, bytes);
        assertNotNull(result);
    }
    
    // 3DES - bloki o długości 64b
    @Test
    public void testCrypt_TextToEncrypt_BytesArrayLengthDivisibleBy8() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        int cipherMode = 1; // encrypt
        byte[] text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.".getBytes();
        byte[] enc_text = enc.crypt(cipherMode, text);
        int size = enc_text.length;
        assertEquals(0, size % 8);
    }
    
    @Test(expected=IllegalBlockSizeException.class)
    public void testCrypt_TextToDecryptLengthIsNotDivisibleBy8_Exception() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] bytes = "xyz".getBytes();
        enc.crypt(Cipher.DECRYPT_MODE, bytes);
    }
    
    /*
        Tests of Encryption class constructor
    */
    
    @Test(expected=NoSuchAlgorithmException.class)
    public void testEncryption_IncorrectAlgorithmName_Exception() throws NoSuchAlgorithmException, NoSuchPaddingException {
        String algorithm = "xyz";
        Encryption e = new Encryption(algorithm);
    }
    
    @Test
    public void testEncryption_CorrectAlgorithmName_NotNullObject() throws NoSuchAlgorithmException, NoSuchPaddingException {
        String algorithm = "AES";
        Encryption e = new Encryption(algorithm);
        assertNotNull(e);
    }
}
