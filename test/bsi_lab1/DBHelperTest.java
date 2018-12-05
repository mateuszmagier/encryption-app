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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.apache.derby.jdbc.ClientDataSource;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * 1) konstruktor tworzy obiekt not null
 * 2) wyjątek, jeśli do insertData przekazano null
 * 3) wyjątek, jeśli do insertData przekazano niekompletny obiekt klasy Data
 * 4) porównanie pól obiektów Data: przeznaczonego do zapisania (insertData) oraz odczytanego po zapisie (selectData)
 * 5) FALSE zwracane przez insertData, jeśli wykonanie zapytania wstawiającego się nie powiodło
 * 6) getMaxId zwraca 0, jeśli tabela jest pusta
 * 7) getMaxId zwraca > 0, jeśli tabela nie jest pusta
 * 8) selectData zwraca obiekt null, jeśli rekord o podanym ID nie istnieje w bazie
 * 9) selectData zwraca obiekt Data not null, jeśli rekord o podanym ID istnieje w bazie
 * 10) wyjątek, jeśli do selectData podano ujemne ID
 * 11) selectAllData - rozmiar ResultSet i rozmiar listy zwracanej przez metodę są równe
 * 12) selectAllData zwraca pustą listę, jeśli ResultSet jest pusty
 */
@RunWith(MockitoJUnitRunner.class)
public class DBHelperTest {
    
    @Mock
    PreparedStatement pstmt;
    
    @Mock
    Statement stmt;
    
    @Mock
    ClientDataSource ds;

    @Mock
    Connection conn;
    
    @Mock
    ResultSet rs;
    
    DBHelper db;
    Data d;
    
    public DBHelperTest() {
    }
    
    @Before
    public void beforeTest() throws SQLException {
        assertNotNull(ds);
        when(ds.getConnection()).thenReturn(conn);
        db = new DBHelper(ds);
        when(conn.createStatement()).thenReturn(stmt);
        when(conn.prepareStatement(any(String.class))).thenReturn(pstmt);
        
        d = new Data();
        d.setId(1);
        d.setEncryptedData("Sample".getBytes());
        d.setTimestamp(new Timestamp(35464575));
        
        when(pstmt.execute()).thenReturn(true);
        
        when(stmt.executeQuery(any(String.class))).thenReturn(rs);
        
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(d.getId());
        when(rs.getBytes(2)).thenReturn(d.getEncryptedData());
        when(rs.getTimestamp(3)).thenReturn(d.getTimestamp());
    }
    
    @Test
    public void testDBHelper_CreateNewObject_NewObjectIsNotNull() {
        assertNotNull(db);
    }
    
    /*
         Tests of insertData method, of class DBHelper.
    */
    
    @Test
    public void testInsertData_ParameterIsNullObject_Exception() {
        String expectedMsg = "Przekazano obiekt NULL.";
        String msg = "";
        try {
            db.insertData(null);
        }
        catch(IllegalArgumentException ex) {
            msg = ex.getMessage();
        }
        assertEquals(expectedMsg, msg);
    }
    
    @Test
    public void testInsertData_ParameterObjectIsNotComplete_Exception() {
        String expectedMsg = "Przekazano niekompletny obiekt klasy Data.";
        String msg = "";
        Data d = new Data();
        d.setId(5);
        try {
            db.insertData(d);
        }
        catch(IllegalArgumentException ex) {
            msg = ex.getMessage();
        }
        assertEquals(expectedMsg, msg);
    }
    
    @Test
    public void testInsertAndSelect_OneRowInsertedAndSelected_ObjectsFieldsShouldBeEqual() {
        db.insertData(d);
        Data d1 = db.selectData(1);
        assertEquals(d.getId(),d1.getId());
        assertArrayEquals(d.getEncryptedData(), d1.getEncryptedData());
        assertEquals(d.getTimestamp(), d1.getTimestamp());
    }
    
    @Test
    public void testInsert_UnsuccesfullInsert_FalseReturned() throws SQLException {
        when(pstmt.execute()).thenReturn(false);
        boolean result = db.insertData(d);
        assertEquals(false, result);
    }
    
    /*
         Tests of getMaxId method, of class DBHelper.
    */
    
    @Test
    public void testGetMaxId_TableIsEmpty_ShouldBeZero() throws SQLException {
        int id;
        when(rs.next()).thenReturn(false);
        id = db.getMaxId();
        assertEquals(0, id);
    }
    
    @Test
    public void testGetMaxId_TableIsNotEmpty_ShouldBeGreaterThanZero() throws SQLException {
        int id;
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(6);
        id = db.getMaxId();
        assertEquals(6, id);
    }
    
    /*
         Tests of selectData method, of class DBHelper.
    */
    
    @Test
    public void testSelectData_RowWithIdDoesNotExistInTable_Null() throws SQLException {
        when(rs.next()).thenReturn(false);
        Data d1 = db.selectData(5);
        assertNull(d1);
    }
    
    @Test
    public void testSelectData_RowWithIdExistsInTable_NotNull() throws SQLException {
        when(rs.next()).thenReturn(true);
        Data d1 = db.selectData(5);
        assertNotNull(d1);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testSelect_RowWithNegativeId_Exception() {
        db.selectData(-1);
    }
    
    /*
         Tests of selectAllData method, of class DBHelper.
    */
    
    @Test
    public void testSelectAllData_TableIsNotEmpty_RowsNumberEqualsToListSize() throws SQLException {
        when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        List<Data> dl = new ArrayList<Data>();
        dl = db.selectAllData();
        assertEquals(3,dl.size());
    }
    
    @Test
    public void testSelectAllData_NoRows_ShouldReturnEmptyList() throws SQLException {
        when(rs.next()).thenReturn(false);
        List<Data> dl = new ArrayList<Data>();
        dl = db.selectAllData();
        assertEquals(0,dl.size());
    }
}
