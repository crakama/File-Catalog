package server.controller;

import common.ClientRemoteInterface;
import common.Credentials;
import common.ServerRemoteInterfaceStub;
import server.model.FileCatalogDAO;
import server.model.UserImpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

/**
 *
 * @RemoteServerImpl implements all methods defined in @ServerRemoteInterfaceStub for the Remote Server
 * Implements methods to be accessed by client remotely e.g client calls on uploadFile method on the server stub
 * to get the server upload the file to DB
 */
public class ServerRemoteImplStub extends UnicastRemoteObject implements ServerRemoteInterfaceStub {

FileCatalogDAO fileCatalogDAO;
    public ServerRemoteImplStub(String datasource, String dbms) throws RemoteException, SQLException, ClassNotFoundException {
        super();
        fileCatalogDAO = new FileCatalogDAO(datasource,dbms);
    }


    @Override
    public synchronized void registerUser(String username, String pass) throws SQLException {
        //TO DO check if username is unique
        UserImpl user = new UserImpl(username,pass, fileCatalogDAO);
        fileCatalogDAO.registerUser(user);
    }

    @Override
    public synchronized int openConnection() throws RemoteException, SQLException, ClassNotFoundException {
        return 0;
    }

    @Override
    public void login(ClientRemoteInterface clientRemoteInterface, Credentials cred) throws RemoteException {

    }

    @Override
    public void uploadFile(long id, String filename) throws RemoteException {

    }

    @Override
    public void closeConnection(long id) throws RemoteException, SQLException {

    }

    @Override
    public void searchDB(int id, String searchString) throws RemoteException, SQLException {

    }

    @Override
    public String getNextRow(int id) throws RemoteException, SQLException {

        return null;
    }
}
