package com.crakama.client.clientlauncher;

import com.crakama.client.view.ClientManager;
import com.crakama.common.ServerInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class StartClient {

    public static void main(String[] args) {
	// write your code here
        try {
            //ServerInterface serverInterface = (ServerInterface) Naming.lookup(ServerInterface.BANK_NAME_IN_REGISTRY);
            //new ClientManager().start(serverInterface);
            new ClientManager().start();

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
