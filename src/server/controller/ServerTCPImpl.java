package server.controller;

import common.ServerTCPInterface;
import server.model.FileCatalogDAO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class ServerTCPImpl implements Runnable, ServerTCPInterface {
    private static String datasource = "filecatalog";
    private static String dbms = "mysql";
    static final int port=1234;
    static ServerSocket serverSocket;
    static FileCatalogDAO fileCatalogDAO = null;


    public void start(){
        new Thread(this).start();
    }
    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("New socket opened");

            serveClients();
        } catch (IOException|ClassNotFoundException|SQLException e) {
            System.out.println("Unable to connect to port");
            e.printStackTrace();
        }


    }

    @Override
    public void serveClients() throws SQLException, ClassNotFoundException {
        do{
            try {
                Socket link = serverSocket.accept();
                System.out.println("Server accepted socket connection");
                ObjectInputStream inputStream = new ObjectInputStream(link.getInputStream());
                byte[] filedata = (byte[])inputStream.readObject();
                fileCatalogDAO = new FileCatalogDAO(datasource,dbms,filedata);
                System.out.println("Input stream 1 sent to DAO");
                link.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }while (true);

    }

}
