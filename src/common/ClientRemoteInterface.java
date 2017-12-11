package common;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientRemoteInterface  extends Remote{

    /**
     * Remote methods that a server can call on a client
     * @receiveServerMsg called by server module,msg is sent by server and received by client
     * @uploadFile server calls this method to get file uploaded tocatalogue
     * @throwsRemoteException
     */
 /*   void receiveServerMsg(String msg)throws RemoteException;

    void downloadFile(long id , String msg)throws RemoteException;*/

 //Method that a server would call to get file details
    public List<FileInfo> getFileDetails()throws RemoteException, IOException;


}
