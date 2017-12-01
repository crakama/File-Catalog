package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemoteInterface  extends Remote{

    /**
     * Remote methods that a server can call on a client
     * @receiveServerMsg called by server module,msg is sent by server and received by client
     * @uploadFile server calls this method to get file uploaded tocatalogue
     * @throwsRemoteException
     */
    void receiveServerMsg(String msg)throws RemoteException;

    void downloadFile(long id , String msg)throws RemoteException;
}
