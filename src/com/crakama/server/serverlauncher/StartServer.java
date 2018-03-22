package com.crakama.server.serverlauncher;

import com.crakama.server.controller.SFileTransfer;
import com.crakama.server.controller.ServerStub;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class StartServer {
    private String datasource = "filescatalog";
    private String dbms = "mysql";

    public static void main(String[] args){
        try {
            StartServer server = new StartServer();
            server.startRegistry();
            System.out.println("Server is Running::");
        } catch (RemoteException |MalformedURLException  e) {
            e.printStackTrace();
            System.out.println("Could not start Server, Try Again::");
        }
        new StartServer().openFileTransferPort();
    }

    private void startRegistry() throws RemoteException, MalformedURLException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException e) {
            System.out.println("Creating a new ServerStub, None found Running at Registry:");
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        ServerStub serverStub = new ServerStub(dbms,datasource);
        Naming.rebind(ServerStub.SERVER_NAME_IN_REGISTRY,serverStub);

    }

    /**
     * TCP connection for file transfer to and from client listening on the same port
     */

    private void openFileTransferPort(){
        try {
            //TODO: Refactor to a more efficient implementation with thread pool, to avoid one thread per client
            ServerSocket serverSocket = new ServerSocket(1213);
            while(true){
                Socket clientSocket = serverSocket.accept();
                SFileTransfer serverTCP = new SFileTransfer(clientSocket,dbms,datasource);
                Thread serverThread = new Thread(serverTCP);
                System.out.println("New Server Thread Started on socket:" + clientSocket);
                serverThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Problem Opening ServerSocket for file transfer:");
        }
    }

}
