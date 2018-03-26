package com.crakama.common.rmi;

import com.crakama.client.view.CmdType;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


/**
 * This class is used by client to access server methods
 * Gateway to ServerStub
 */
public interface ServerInterface extends Remote {
    String SERVER_NAME_IN_REGISTRY = "fileserver";

    void register(ClientInterface clientCallbackInterf, String name, String password) throws RemoteException;
    void login(ClientInterface clientCallbackInterf, String name, String password) throws RemoteException;
    void unregister(ClientInterface clientCallbackInterf, String name, String password) throws RemoteException;

    void checkfile(ClientInterface clientCallbackInterf, String parameters, String kate, String filename, int i) throws RemoteException;

    void readFile(CmdType read, String loggeduser, ClientInterface clientCallbackInterf, String input) throws RemoteException;

    void writeFile(CmdType edit, String loggeduser, ClientInterface clientCallbackInterf, String parameters, String cmdReaderParameters) throws RemoteException;

    int checkAccessPermission(ClientInterface clientCallbackInterf, String peek, String s) throws RemoteException;

    void listfiles(ClientInterface clientCallbackInterf) throws RemoteException;

    void fileMonitor(ClientInterface clientCallbackInterf, String parameters) throws RemoteException;

    void stopMonitors(ClientInterface clientCallbackInterf, List<String> monitors) throws RemoteException;
}
