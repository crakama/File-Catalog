package server.controller;

import common.ServerTCPInterface;
import server.model.FileCatalogDAO;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Contains implementation of all methods that were registered with the controller(ServerTCPInterface).
 * Multithreaded server, every new client connection is started  with a new socket object
 * on a different server thread.
 */

public class ServerTCPImpl implements Runnable, ServerTCPInterface {
    private static String datasource = "filecatalog";
    private static String dbms = "mysql";
    static final int port=1234;
    boolean connected;
    static ServerSocket serverSocket;
    static FileCatalogDAO fileCatalogDAO = null;
    private final Socket clientSocket;
    BufferedReader usercmdRvdIn= null;
    BufferedOutputStream reqFileName;
    ObjectOutputStream sendResponce;

    public ServerTCPImpl(Socket s){
        this.clientSocket = s;
        connected = true;
    }

    /**
     * Initialize server socket class with a specific port number in which the server will listen
     * for incoming connections. The client will use the same port to establish TCP connection to the server
     *
     */
    @Override
    public void run() {
        System.out.println("Run method");
        System.out.println("Try of Run method");

        try {
            usercmdRvdIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("BufferedReader" + usercmdRvdIn);
            sendResponce = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(connected){
        try {

            String userCommand = usercmdRvdIn.readLine();
            System.out.println("userCommand" + userCommand);

            if(userCommand.equals("upload")){
                System.out.println("Server received" + userCommand+ " request");
                upLoadFile();
            }else if (userCommand.equals("download")){
                System.out.println("Function not yet implemented!!");
            }

            //link.close();
        } catch (IOException|ClassNotFoundException |SQLException e) {

        }
    }

    }

    /**
     * @accept() waits for client requests from remote machines,returns a socket when there is a connection
     * Server uses the socket for client communications
     * do...while loop to keep the server thread running and listening to more requests.
     *@link.getInputStream() read data sent by the client from the socket.
     */
    @Override
    public void upLoadFile() throws SQLException, ClassNotFoundException, IOException {
        System.out.println("Upload initiated");
        PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(),true);
        System.out.println("SERVER BEFORE before send" );

        //reqFileName = new BufferedOutputStream(clientSocket.getOutputStream());
        String reqFileName = "Enter file name e.g hw.pdf or code.jpg\\n";
        printWriter.println(reqFileName);
        printWriter.flush();

        ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
        System.out.println("Input stream received from link");
        byte[] filedata = (byte[])inputStream.readObject();
        System.out.println("Byte data read");
        fileCatalogDAO = new FileCatalogDAO(datasource,dbms,filedata);
        System.out.println("Input stream 1 sent to DAO");
    }

}
