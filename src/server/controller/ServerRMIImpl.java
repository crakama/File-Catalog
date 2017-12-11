package server.controller;

import common.FileInterface;
import common.Credentials;
import common.ServerRMIInterface;
import common.UserInterface;
import server.model.FileCatalogDAO;
import server.model.UserImpl;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @RemoteServerImpl implements all methods defined in @ServerRMIInterface for the Remote Server
 * Implements methods to be accessed by client remotely e.g client calls on uploadFile method on the server stub
 * to get the server upload the file to DB
 */
public class ServerRMIImpl extends UnicastRemoteObject implements ServerRMIInterface,Serializable {

FileCatalogDAO fileCatalogDAO;
    UserImpl user;
    private String datasource, dbms;

    public ServerRMIImpl(String datasource, String dbms) throws RemoteException, SQLException, ClassNotFoundException {
        super();
        fileCatalogDAO = new FileCatalogDAO(datasource,dbms);
        this.datasource = datasource;
        this.dbms = dbms;
    }


    @Override
    public synchronized void registerUser(String username, String pass) throws SQLException {
        //TO DO check if username is unique
        user = new UserImpl(username,pass, fileCatalogDAO);
        fileCatalogDAO.registerUser(user);
    }

    @Override
    public void sendFileDetails(String filename, int size, String owner, String accessP) throws RemoteException {

    }
    @Override
    public synchronized void unRegisterUser(UserImpl userimpl) throws SQLException{
        fileCatalogDAO.deleteUser(userimpl);
    }
    @Override
    public synchronized UserImpl getUserRecord(String currentuser) throws SQLException {
        System.out.println("At IMPL");
        return fileCatalogDAO.findUser(currentuser);
    }

    @Override
    public synchronized void openConnection() throws SQLException, ClassNotFoundException {

       fileCatalogDAO.connectToFileCatalogDB(datasource, dbms);
    }

    @Override
    public void login(FileInterface clientRemoteInterface, Credentials cred) {

    }


    @Override
    public void closeConnection(long id) throws RemoteException, SQLException {

    }

    @Override
    public List<? extends FileInterface> listFiles() throws RemoteException, SQLException {
        return fileCatalogDAO.listFiles();
    }

    @Override
    public String getNextRow(int id) throws RemoteException, SQLException {

        return null;
    }



 /*   @Override
    public void sendFileDetails(String filename, String owner, String accessP) throws RemoteException {
        fileCatalogDAO = new FileCatalogDAO(filename,owner,accessP);
    }*/

  /*  public FileCatalogDAO(String datasource,String dbms,String filename, String owner,
                          String accessPerm, byte[] filedata)*/
}
