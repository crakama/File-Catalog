package client.startup;

import client.view.ClientRemoteImpl;
import server.Callee;

import java.rmi.RemoteException;

public class Main {
    public static void main(String[] args ){
        try {
            new ClientRemoteImpl().start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
