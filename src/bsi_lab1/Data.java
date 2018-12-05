/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsi_lab1;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author HP
 */
public class Data implements Serializable {
    
    private int id; // identyfikator zaszyfrowanych danych
    private byte[] encryptedData; // zaszyfrowane dane, zapisane w tablicy bajtów
    private Timestamp timestamp; // czas zaszyfrowania
    
    public Data() {}
    
    public Data(int _id, byte[] _encryptedData, Timestamp _timestamp) {
        this.id = _id;
        this.encryptedData = _encryptedData;
        this.timestamp = _timestamp;
    }
    
    /* gettery i settery */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(byte[] encryptedData) {
        this.encryptedData = encryptedData;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }    
}
