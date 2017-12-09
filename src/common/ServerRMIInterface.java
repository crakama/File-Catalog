package common;

import server.model.UserImpl;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;


/**
 * @RemoteInterface will appear at both the client ans server side
 * @Remote exposes methods to be accessed by client remotely
 */
public interface ServerRMIInterface extends Remote  {
    String REGISTERED_SERVER_NAME = "filecatalog";
    /**
     * Remote methods that Client calls on the server, as if they were remote
     */

    void registerUser(String username, String pass) throws RemoteException,SQLException;

    void unRegisterUser(UserImpl userimpl) throws RemoteException,SQLException;

    UserImpl getUserRecord(String name) throws RemoteException,SQLException;


    void openConnection()throws RemoteException,SQLException,ClassNotFoundException;

    void login(FileInterface clientRemoteInterface, Credentials cred) throws RemoteException;


    void closeConnection(long id) throws RemoteException,SQLException;

    List<? extends FileInterface> listFiles() throws RemoteException,SQLException;

    String getNextRow(int id)throws RemoteException,SQLException;

    //void sendFileDetails(String filename, String owner, String accessP) throws RemoteException;
}
