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
    private Socket clientFileSocket;
    private static String datasource = "filecatalog";
    private static String dbms = "mysql";
    private String filename,owner,accessPerm;
    FileCatalogDAO fileCatalogDAO = null;
    public FileServerImpl(Socket cs){
        this.clientFileSocket = cs;
    }

    //change it to have socket & string params
    public FileServerImpl(String filename, String owner,String accessPerm) {
        this.filename = filename;
        this.owner = owner;
        this.accessPerm = accessPerm;
        //receivedFile(filename,owner,accessPerm);
        System.out.println("FILE NAME PASSED at FSI Constr" + filename);
    }

    @Override
    public void run() {
        try {
            receivedFile(filename,owner,accessPerm);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receivedFile(String filename, String owner,String accessPerm) throws SQLException, ClassNotFoundException, IOException {
        ObjectInputStream inputStream = new ObjectInputStream(clientFileSocket.getInputStream());
        System.out.println("File Input stream received from link");
        byte[] filedata = (byte[])inputStream.readObject();
        System.out.println("File input stream converted to Byte data to be read");
        System.out.println("FILE NAME at Server" + filename);
        fileCatalogDAO = new FileCatalogDAO(datasource,dbms,filename,owner,accessPerm,filedata);
        System.out.println("File data sent to DAO for saving");
    }
}
