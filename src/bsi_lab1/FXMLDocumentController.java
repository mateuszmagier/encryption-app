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
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.sql.DataSource;
import org.apache.derby.jdbc.ClientDataSource;

/**
 *
 * @author HP
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private TextArea textarea; // pole tekstu do zaszyfrowania
    
    private File fileToEncrypt; // dowolny plik do zaszyfrowania
    private File fileEncryptionFile; // plik CFR przechowujący zaszyfrowany dowolny plik
    private File fileToDecrypt; // plik CFR z zaszyfrowanym plikiem (wybierany do odszyfrowania)
    private File decryptedFileFile; // plik przechowujący odszyfrowany plik
    private DBHelper db; // obiekt klasy pomocniczej realizującej połączenie z bazą danych
    private FileHelper fh;
    //private FileHelper fh; // obiekt klasy pomoczniczej realizującej operacje plikowe
    private ObservableList data; // obiekt klasy pomocniczej dostarczający dane do TableView
    private File textEncryptionFile; // plik CFR przechowujący zaszyfrowany tekst
    private File textDecryptionFile; // plik CFR przechowujący zaszyfrowany tekst (wybierany do odszyfrowania)
    private File decryptedTextFile; // plik TXT przechowujący odszyfrowany tekst
    private File dataToEncrypt; // plik do zaszyfrowania w bazie danych
    private File decryptedDataFile; // plik z odszyfrowanymi danymi z bazy danych
    private int lastId; // najwyższy identyfikator w bazie
    
    private Encryption enc; // obiekt klasy pomocniczej realizującej szyfrowanie
    @FXML
    private Label decryptedTextLabel; // odszyfrowany tekst
    @FXML
    private TableView<?> table; // tabela z zaszyfrowanymi danymi
    @FXML
    private Label file_to_encrypt_path; // ścieżka do pliku do zaszyfrowania
    @FXML
    private Label file_to_decrypt_path; // ścieżka do pliku do odszyfrowania
    @FXML
    private TextField data_id; // ID rekordu do odszyfrowania
    @FXML
    private TextArea log_textarea; // logi
    @FXML
    private Label data_to_encrypt_path;
    
    ClientDataSource ds;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            enc = new Encryption("DESede");
//            System.out.println(Base64.getEncoder().encodeToString(enc.key.getEncoded()));
            ds = new ClientDataSource();
            ds.setDatabaseName("bsi");
            ds.setUser("username");
            ds.setPassword("password");
            ds.setServerName("localhost");
            ds.setPortNumber(1527);
            db = new DBHelper(ds);
            fh = new FileHelper();
            //fh = new FileHelper();
            try {
                updateLog("Program rozpoczął działanie...");
                prepareTable(); // budowa struktury tabeli
                fillTable(); // wypełnienie tabeli danymi z bazy danych
                log_textarea.setEditable(false);
            } catch (SQLException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void encryptFileButtonAction(ActionEvent event) throws InvalidKeyException, IOException, FileNotFoundException, IllegalBlockSizeException, BadPaddingException {
        if(fileToEncrypt != null) {
            if(fileEncryptionFile == null) {
                // wybór nazwy pliku, w którym zapisany zostanie zaszyfrowany plik
                fileEncryptionFile = saveFileDialog(new FileChooser.ExtensionFilter("CFR", "*.cfr"));
            }
            // szyfrowanie pliku
            byte[] input = fh.readFile(fileToEncrypt);
            byte[] output = enc.crypt(Cipher.ENCRYPT_MODE, input);
            fh.saveFile(output, fileEncryptionFile);
        }
        updateLog("Zaszyfrowano plik: " + fileToEncrypt.getAbsolutePath());
        updateLog("Zaszyfrowany plik zapisano w pliku: " + fileEncryptionFile.getAbsolutePath());
        fileToEncrypt = null;
        fileEncryptionFile = null;
    }

    @FXML
    private void decryptFileButtonAction(ActionEvent event) throws InvalidKeyException, IOException, FileNotFoundException, IllegalBlockSizeException, BadPaddingException {
        if(decryptedFileFile == null) {
            // wybór nazwy pliku, w którym zapisany zostanie odszyfrowany plik
            decryptedFileFile = saveFileDialog(null);
        }
        
        if(decryptedFileFile != null && fileToDecrypt != null) {
            // odszyfrowanie pliku
            byte[] input = fh.readFile(fileToDecrypt);
            byte[] output = enc.crypt(Cipher.DECRYPT_MODE, input);
            fh.saveFile(output, decryptedFileFile);
        }
        
        updateLog("Odszyfrowano plik: " + fileToDecrypt.getAbsolutePath());
        updateLog("Odszyfrowany plik zapisano w pliku: " + decryptedFileFile.getAbsolutePath());
    }

    @FXML
    private void encryptTextButtonAction(ActionEvent event) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        String text = textarea.getText(); // pobranie tekstu z pola
        if(textEncryptionFile == null) {
            // wybór nazwy pliku, w którym zapisany zostanie zaszyfrowany tekst
            textEncryptionFile = saveFileDialog(new FileChooser.ExtensionFilter("CFR", "*.cfr"));
        }
        byte[] output = enc.crypt(Cipher.ENCRYPT_MODE, text.getBytes());
        fh.saveFile(output, textEncryptionFile);
        updateLog("Zaszyfrowany tekst zapisano w pliku: " + textEncryptionFile.getAbsolutePath());
        textEncryptionFile = null;
    }

    @FXML
    private void decryptTextButtonAction(ActionEvent event) throws InvalidKeyException, IOException, FileNotFoundException, IllegalBlockSizeException, BadPaddingException {
        if(textDecryptionFile == null) {
            textDecryptionFile = chooseFile(); // wybór pliku do odszyfrowania
        }
        byte[] input = fh.readFile(textDecryptionFile);
        byte[] output = enc.crypt(Cipher.DECRYPT_MODE, input);
        decryptedTextLabel.setText(new String(output));
        updateLog("Odszyfrowano tekst z pliku: " + textDecryptionFile.getAbsolutePath());
        
        decryptedTextFile = new File("C:/test/decrypted_" + textDecryptionFile.getName() + ".txt");
        fh.saveFile(output, decryptedTextFile);
        updateLog("Odszyfrowany tekst zapisano w pliku: " + decryptedTextFile.getAbsolutePath());
        
        textDecryptionFile = null;
        decryptedTextFile = null;
    }

    /* szyfrowanie danych wprowadzonych przez użytkownika i zapisanie ich w bazie danych */
    @FXML
    private void encryptDataButtonAction(ActionEvent event) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, SQLException {
        int id;
        byte[] input = fh.readFile(dataToEncrypt);
        byte[] output = enc.crypt(Cipher.ENCRYPT_MODE, input);
        id = db.getMaxId() + 1; // inkrementacja ID
        
        // przygotowanie obiektu klasy Data do zapisania w bazie danych
        Data data = new Data(id, output, new Timestamp(System.currentTimeMillis()));
        db.insertData(data); // wstawienie nowego rekordu
        lastId = id; // aktualizacja ID ostatnio dodanego rekordu
        updateLog("Zaszyfrowane dane zapisano w bazie z ID = " + data.getId());
        
        refreshTable(); // ponowne wczytanie danych z bazy danych do tabeli - odświeżenie widoku
    }

    /* odszyfrowanie danych zapisanych w bazie w rekordzie o wybranym ID */
    @FXML
    private void decryptDataButtonAction(ActionEvent event) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        int id = Integer.parseInt(data_id.getText()); // ID rekordu do odszyfrowania
        if(decryptedDataFile == null) {
            // wybór nazwy pliku, w którym zapisany zostanie odszyfrowany plik
            decryptedDataFile = saveFileDialog(null);
        }
        byte[] input = db.selectData(id).getEncryptedData(); // odczytanie zaszyfrowanych danych
        byte[] output = enc.crypt(Cipher.DECRYPT_MODE, input);
        fh.saveFile(output, decryptedDataFile);
        updateLog("Odszyfrowano dane dla rekordu z bazy o ID = " + id);
        updateLog("Odszyfrowane dane zapisano w pliku: " + decryptedDataFile.getAbsolutePath());
    }

    /* metoda pomocnicza - wybór pliku */
    private File chooseFile() {
        File file;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik...");
        file = fileChooser.showOpenDialog(new Stage());
        
        return file; // zwracany jest wybrany plik - obiekt klasy File
    }

    /* przygotowanie struktury tabeli */
    public void prepareTable() throws SQLException {
        ResultSet rs = db.getAllData(); // pobierz wszystkie rekordy z bazy danych
        data = FXCollections.observableArrayList();

        // dynamiczne budowanie struktury tabeli na podstawie struktury tabeli w bazie danych
        for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
            final int j = i;                
            TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {                                                                                              
                    return new SimpleStringProperty(param.getValue().get(j).toString());                        
                }                    
            });

            table.getColumns().addAll(col);
        }
        
        table.getColumns().get(1).setMaxWidth(270);
    }
    
    /* wstawienie rekordów z bazy do tabeli w aplikacji */
    public void fillTable() throws SQLException {
        ResultSet rs = db.getAllData();
        while(rs.next()){
            //Iterate Row
            ObservableList<String> row = FXCollections.observableArrayList();
            for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                //Iterate Column
                row.add(rs.getString(i));
            }
            data.add(row);

        }
        table.setItems(data);
    }
    
    // zresetowanie zawartości tabeli
    public void refreshTable() throws SQLException {
        for (int i = 0; i<table.getItems().size(); i++) {
            table.getItems().clear();
        }
        
        fillTable();
    }
    
    /* wybór nazwy i lokalizacji pliku do zapisania - zwraca obiekt klasy File
        parametr: filtr rozszerzenia
    */
    public File saveFileDialog(FileChooser.ExtensionFilter filter) {
        File file;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz lokalizację pliku do zapisania...");
        if(filter != null) 
            fileChooser.getExtensionFilters().add(filter);
        file = fileChooser.showSaveDialog(new Stage());
        return file;
    }

    // wybór pliku do zaszyfrowania
    @FXML
    private void chooseFileToEncryptAction(ActionEvent event) {
        fileToEncrypt = chooseFile();
        if(fileToEncrypt != null) {
            file_to_encrypt_path.setText(fileToEncrypt.getAbsolutePath());
        }
    }

    // wybór pliku do odszyfrowania
    @FXML
    private void chooseFileToDecryptAction(ActionEvent event) {
        fileToDecrypt = chooseFile();
        if(fileToDecrypt != null) {
            file_to_decrypt_path.setText(fileToDecrypt.getAbsolutePath());
        }
    }
    
    // dodanie nowego wpisu do logów
    public void updateLog(String msg) {
        String temp = log_textarea.getText();
        String updated = temp + new Timestamp(System.currentTimeMillis()) + ": " + msg + "\n";
        log_textarea.setText(updated);
    }

    /* metoda testująca działanie aplikacji */
    @FXML
    private void testAction(ActionEvent event) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, SQLException {
        /* SZYFROWANIE TEKSTU */
        textarea.setText("Przykładowy tekst...");
        textEncryptionFile = new File("C:/test/text_enc.cfr");
        encryptTextButtonAction(event);
        textDecryptionFile = new File("C:/test/text_enc.cfr");
        decryptTextButtonAction(event);
        
        /*SZYFROWANIE PLIKU */
        fileToEncrypt = new File("C:/test/example.jpg");
        fileEncryptionFile = new File("C:/test/example.cfr");
        file_to_encrypt_path.setText(fileToEncrypt.getAbsolutePath());
        encryptFileButtonAction(event);
        
        fileToDecrypt = new File("C:/test/example.cfr");
        decryptedFileFile = new File("C:/test/decrypted_example.cfr.jpg");
        file_to_decrypt_path.setText(fileToDecrypt.getAbsolutePath());
        decryptFileButtonAction(event);
        
        /* SZYFROWANIE DANYCH */
//        db_textarea.setText("Przykład");
        dataToEncrypt = new File("C:/test/instrukcja.pdf");
        data_to_encrypt_path.setText(dataToEncrypt.getAbsolutePath());
        encryptDataButtonAction(event);
        data_id.setText(lastId + "");
        decryptedDataFile = new File("C:/test/decrypted_instrukcja.pdf");
        decryptDataButtonAction(event);
    }

    @FXML
    private void chooseDataToEncryptAction(ActionEvent event) {
        dataToEncrypt = chooseFile();
        if(dataToEncrypt != null) {
            data_to_encrypt_path.setText(dataToEncrypt.getAbsolutePath());
        }
    }
}