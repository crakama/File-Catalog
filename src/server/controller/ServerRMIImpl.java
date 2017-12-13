package server.controller;

import common.ClientRemoteInterface;
import common.Credentials;
import common.ServerRMIInterface;
import common.UserInterface;
import server.model.FileCatalogDAO;
import server.model.UserImpl;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

/**
 *
 * @RemoteServerImpl implements all methods defined in @ServerRMIInterface for the Remote Server
 * Implements methods to be accessed by client remotely e.g client calls on uploadFile method on the server stub
 * to get the server upload the file to DB
 */
public class ServerRMIImpl extends UnicastRemoteObject implements ServerRMIInterface,Serializable {

FileCatalogDAO fileCatalogDAO;
    UserImpl user,file;
    private String datasource, dbms,logname,logpass;

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
    public synchronized void uploadFileInfo(String fname, String fowner, String faccessmode, int fsize) throws SQLException {
        file = new UserImpl(fname, fowner,faccessmode,fsize,fileCatalogDAO);
        fileCatalogDAO.uploadFileInfo(file);
    }

    @Override
    public synchronized UserInterface login(String name, String pass) throws SQLException {
        //fileCatalogDAO.loginUser(name, pass);
       try {
            return fileCatalogDAO.loginUser(name);
        } catch (Exception e) {
            System.out.println("Could not login you into your account.");


        return null;
       }
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
