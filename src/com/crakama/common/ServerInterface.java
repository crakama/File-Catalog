package com.crakama.common;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * This class is used by client to access server methods
 * Gateway to ServerStub
 */
public interface ServerInterface extends Remote {
    public static final String SERVER_NAME_IN_REGISTRY = "fileserver";

    void register(ClientInterface clientCallbackInterf, String name, String password) throws RemoteException;
    void login(ClientInterface clientCallbackInterf, String name, String password) throws RemoteException;
    void unregister(ClientInterface clientCallbackInterf, String name, String password) throws RemoteException;

    void checkfile(ClientInterface clientCallbackInterf, String filename) throws RemoteException;
}
