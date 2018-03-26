package com.crakama.common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;


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

    void readFile(ClientInterface clientCallbackInterf, String input) throws RemoteException;

    void writeFile(ClientInterface clientCallbackInterf, String parameters, String cmdReaderParameters) throws RemoteException;

    int checkAccessPermission(ClientInterface clientCallbackInterf, String peek, String s) throws RemoteException;

    void listfiles(ClientInterface clientCallbackInterf) throws RemoteException;
}
