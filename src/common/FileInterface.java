package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileInterface extends Remote{

    /**
     * Remote methods that a server can call on a client
     * @receiveServerMsg called by server module,msg is sent by server and received by client
     * @uploadFile server calls this method to get file uploaded tocatalogue
     * @throwsRemoteException
     */
    // Get file records
    String getFileName();
    String getSize();
    String getAccessPerm();

    String getFileOwner();
}
