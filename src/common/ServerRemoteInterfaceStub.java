package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;


/**
 * @RemoteInterface will appear at both the client ans server side
 * @Remote exposes methods to be accessed by client remotely
 */
public interface ServerRemoteInterfaceStub extends Remote {
    public static final String REGISTERED_SERVER_NAME = "CATALOGUE SERVER";


    void registerUser(String username, String pass) throws SQLException;

    /**
 * Remote methods that Client calls on the server, as if they were remote
 */
    public int openConnection()throws RemoteException,SQLException,ClassNotFoundException;

    void login(ClientRemoteInterface clientRemoteInterface, Credentials cred) throws RemoteException;

    void uploadFile( long id, String filename) throws RemoteException;

    public void closeConnection(long id) throws RemoteException,SQLException;

    public void searchDB(int id, String searchString) throws RemoteException,SQLException;

    public String getNextRow(int id)throws RemoteException,SQLException;

}
