package com.crakama.server.serverlauncher;

import com.crakama.server.controller.ServerStub;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class StartServer {

    public static void main(String[] args){
        try {
            new StartServer().startRegistry();
            Naming.rebind(ServerStub.SERVER_NAME_IN_REGISTRY,new ServerStub());
            System.out.println("Server is Running::");
        } catch (RemoteException |MalformedURLException  e) {
            e.printStackTrace();
            System.out.println("Could not start Server, Try Again::");
        }
    }

    private void startRegistry() throws RemoteException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException e) {
            System.out.println("Creating a new ServerStub, None found Running at Registry:");
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
    }

}
