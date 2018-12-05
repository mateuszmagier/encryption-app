/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsi_lab1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 *
 * @author HP
 */
public class DBHelper {
    private static final String dbURL = "jdbc:derby://localhost:1527/bsi;create=true;user=username;password=password";
    private static final String tableName = "data";
    private static Connection conn = null;
    private static Statement stmt = null;
    private static PreparedStatement pstmt = null;
    
    private static DataSource ds;
    
    public DBHelper(DataSource _ds) throws SQLException {
//        conn = createConnection();
        this.ds = _ds;
        conn = ds.getConnection();
    }
    
    /* dodawanie nowego rekordu do bazy danych
        Data data - obiekt klasy Data, reprezentujący zaszyfrowane dane
    */
    public boolean insertData(Data data) {
        if(data == null)
            throw new IllegalArgumentException("Przekazano obiekt NULL.");
        else if(data.getId() == 0 || data.getEncryptedData() == null || data.getTimestamp() == null)
            throw new IllegalArgumentException("Przekazano niekompletny obiekt klasy Data.");
        try
        {
            pstmt = conn.prepareStatement("INSERT INTO " + tableName + " values (?,?,?)");
            pstmt.setInt(1, data.getId()); // ID
            pstmt.setBytes(2, data.getEncryptedData()); // zaszyfrowane dane, w postaci tablicy bajtów
            pstmt.setTimestamp(3, data.getTimestamp()); // czas operacji
            if(!pstmt.execute()) return false;
            pstmt.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        return true;
    }
    
    /* metoda pomocnicza zwracająca największe ID w bazie */
    public int getMaxId() throws SQLException {
        String sql = "select max(id) from data";
        int id;
        stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery(sql);
        if(results.next())
            id = results.getInt(1);
        else
            id = 0;
        
        return id;
    }
    
    /* metoda zwracająca wybrany obiekt klasy Data z bazy danych 
        parametr id - identyfikator rekordu w bazie danych
    */
    public Data selectData(int id) {
        if(id < 0)
            throw new IllegalArgumentException();
        Data data = new Data();
        
        ResultSet result = null;
        
        try
        {
            stmt = conn.createStatement();
            result = stmt.executeQuery("select * from " + tableName + " where id = " + id); // parametryzacja zapytania
            if(result.next()) {
                data.setId(result.getInt(1)); // ID
                data.setEncryptedData(result.getBytes(2)); // zaszyfrowane dane, w postaci tablicy bajtów
                data.setTimestamp(result.getTimestamp(3)); // czas operacji
            }
            else
                return null;
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        
        return data;
    }
    
    /* metoda zwracająca kolekcję obiektów klasy Data zapisanych w bazie danych */
    public List<Data> selectAllData() {
        
        List<Data> dataList = new ArrayList<Data>();
        
        try
        {
            stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery("select * from " + tableName);

            while(results.next()) // pętla "przechodzi" po rekordach otrzymanych w wyniku wykonania zapytania
            {
                Data data = new Data();
                data.setId(results.getInt(1));
                data.setEncryptedData(results.getBytes(2));
                data.setTimestamp(results.getTimestamp(3));
                dataList.add(data); // dodaj obiekt do listy
            }
            results.close();
            stmt.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        
        return dataList;
    }
    
    /* uproszczona wersja metody selectAllData - zwraca wyniki w innej postaci */
    public ResultSet getAllData() throws SQLException {
        stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery("select * from " + tableName);
        return results;
    }
}
