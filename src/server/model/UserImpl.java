package server.model;

import common.UserInterface;

public class UserImpl implements UserInterface {
    private  String name, pass,fname,fowner,faccessmode;
    private  int fsize;
    FileCatalogDAO fileCatalogDAO;

    /**
     * arguments passed from @findUser
     * Creates a new user. The UserImpl object will have a database connection.
     * @param name The name of user.
     * @param pass    The password for user.
     * @param fileCatalogDAO     The DAO used to store updates to the database.
     */
    public UserImpl(String name, String pass, FileCatalogDAO fileCatalogDAO){
        this.name = name;
        this.pass = pass;
        this.fileCatalogDAO = fileCatalogDAO;
    }

    public UserImpl(String fname, String fowner, String faccessmode, int fsize, FileCatalogDAO fileCatalogdao) {
        this.fname = fname;
        this.fowner = fowner;
        this.faccessmode = faccessmode;
        this.fsize = fsize;
        this.fileCatalogDAO = fileCatalogdao;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPassword() {
        return pass;
    }

    @Override
    public String getFName() {
        return fname;
    }
    @Override
    public String getFowner() {
        return fowner;
    }
    @Override
    public String getFaccessMode() {
        return faccessmode;
    }
    @Override
    public int getFsize() {
        return fsize;
    }
}
