package com.crakama.server.serverlauncher;

import com.crakama.server.controller.ServerStub;

import java.net.MalformedURLException;
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

}
