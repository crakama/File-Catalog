package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;


/**
 * @RemoteInterface will appear at both the client ans server side
 * @Remote exposes methods to be accessed by client remotely
 */
public interface ServerRemoteInterface extends Remote {
    String REGISTERED_SERVER_NAME = "filecatalog";


    void registerUser(String username, String pass) throws RemoteException,SQLException;

    /**
 * Remote methods that Client calls on the server, as if they were remote
 */
    void openConnection()throws RemoteException,SQLException,ClassNotFoundException;

    void login(ClientRemoteInterface clientRemoteInterface, Credentials cred) throws RemoteException;

    void uploadFile( long id, String filename) throws RemoteException;

    void closeConnection(long id) throws RemoteException,SQLException;

    void searchDB(int id, String searchString) throws RemoteException,SQLException;

    String getNextRow(int id)throws RemoteException,SQLException;

}
