package com.crakama.common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Class used by server to access client's methods remotely
 * Passed to server through call by reference, as client's callback object
 * Gateway to clientStub
 */

public interface ClientInterface extends Remote {
    void serverResponse( String response) throws RemoteException;
}
