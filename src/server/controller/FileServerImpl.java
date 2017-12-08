package server.controller;

import server.model.FileCatalogDAO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.sql.SQLException;

/**
 * Receives file transfer requests from clients and pushes it to database
 */
public class FileServerImpl implements Runnable {
    private final Socket clientFileSocket;
    private static String datasource = "filecatalog";
    private static String dbms = "mysql";
    FileCatalogDAO fileCatalogDAO = null;
    public FileServerImpl(Socket cs){
        this.clientFileSocket = cs;
    }

    @Override
    public void run() {
        try {
            receivedFile();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receivedFile() throws SQLException, ClassNotFoundException, IOException {
        ObjectInputStream inputStream = new ObjectInputStream(clientFileSocket.getInputStream());
        System.out.println("File Input stream received from link");
        byte[] filedata = (byte[])inputStream.readObject();
        System.out.println("File input stream converted to Byte data to be read");
        fileCatalogDAO = new FileCatalogDAO(datasource,dbms,filedata);
        System.out.println("Byte data sent to DAO for saving");
    }
}
