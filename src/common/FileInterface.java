package common;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

public interface FileInterface extends Remote {

    /**
     * Remote methods that a server can call on a client
     * @receiveServerMsg called by server module,msg is sent by server and received by client
     * @uploadFile server calls this method to get file uploaded tocatalogue
     * @throwsRemoteException
     */
    // Get file records
    String getFileName();
    int getSize();
    String getAccessPerm();
    String getFileOwner();

    void receivedFile() throws SQLException, ClassNotFoundException, IOException;

    //public List<FileInfo> getFiles() throws RemoteException;
    void setFileName(String fileName);
    void setAccessPerm(String accessPerm);
    void setFileOwner(String fileOwner);
    void  setSize(int size);

}
