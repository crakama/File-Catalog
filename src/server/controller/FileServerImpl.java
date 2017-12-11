package server.controller;

import client.view.ClientRemoteImpl;
import common.ClientRemoteInterface;
import common.FileInfo;
import common.FileInterface;
import server.model.FileCatalogDAO;
import server.startup.CatalogServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Receives file transfer requests from clients and pushes it to database
 */
public class FileServerImpl implements Runnable, FileInterface {
    private Socket clientFileSocket;
    private static String datasource = "filecatalog";
    private static String dbms = "mysql";
    private String filename,owner,accessPerm;
    FileCatalogDAO fileCatalogDAO = null;
    FileInterface fileInterface;
    ClientRemoteInterface clientRemoteInterface;
    private int filesize;

    public FileServerImpl(Socket cs){

        System.out.println("FILE NAME PASSED at FSI Constr 1" + filename);
    }

    //change it to have socket & string params
    public FileServerImpl(String filename, String owner,String accessPerm) {
        //this.filename = filename;
        //this.owner = owner;
        //this.accessPerm = accessPerm;
        //receivedFile(filename,owner,accessPerm);
        System.out.println("FILE NAME PASSED at FSI Constr 2" + filename);
    }

    public FileServerImpl() {

    }


    public FileServerImpl(String filename, int filesize, String fileowner, String accessPerm) {
        //FileInfo details from DB ...
        this.filename = filename;
        this.filesize = filesize;
        this.owner = fileowner;
        this.accessPerm = accessPerm;

    }



    @Override
    public void run() {
        try {

            //use getters to read data sent by user
           // getFiles();


            receivedFile();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    //receivedFile(String filename, String owner,String accessPerm)
    public void receivedFile() throws SQLException,
            ClassNotFoundException, IOException {
        ClientRemoteInterface cri = new ClientRemoteImpl();
        List<FileInfo> ls = (List) cri.getFileDetails();
        for (FileInfo fileInfo : ls){
            this.filename = fileInfo.getFName();
            System.out.println("The File Name ast FSI"+ filename);
            this.filesize = fileInfo.getFSize();
            this.owner = fileInfo.getFOwner();
            this.accessPerm = fileInfo.getFAccessP();
        }

        ObjectInputStream inputStream = new ObjectInputStream(clientFileSocket.getInputStream());
        System.out.println("FileInfo Input stream received from link");
        byte[] filedata = (byte[])inputStream.readObject();
        System.out.println("FileInfo input stream converted to Byte data to be read");
        System.out.println("FILE NAME at Server" + filename);

        fileCatalogDAO = new FileCatalogDAO(datasource,dbms,filename,owner,accessPerm,filesize,filedata);
        System.out.println("FileInfo data sent to DAO for saving");
    }

    //Initial FileInfo details from user
    @Override
    public void setFileName(String fileName) {
        this.filename = fileName;
    }

    @Override
    public void setAccessPerm(String accessPerm) {
        this.accessPerm = accessPerm;
    }

    @Override
    public void setFileOwner(String fileOwner) {
        this.owner = fileOwner;
    }

    @Override
    public void setSize(int size) {
        this.filesize = size;
    }

    @Override
    public String getFileName() {
        return this.filename;
    }

    @Override
    public int getSize() {
        return this.filesize;
    }

    @Override
    public String getAccessPerm() {
        return this.accessPerm;
    }

    @Override
    public String getFileOwner() {
        return this.owner;
    }




}
