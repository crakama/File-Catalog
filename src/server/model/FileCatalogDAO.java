package server.model;

import common.UserInterface;
import server.controller.FileServerImpl;

import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This Database Handler [data access object (DAO)], it encapsulates all database calls in the catalogue application.
 * JDBC and database details are hidden from client and Server objects.
 */

public class FileCatalogDAO {
    private PreparedStatement createUserStmt,deleteUserStmt,
            listFilesStmt,findUserStmt,uploadFileStmt;
    Connection conn;
    private static final String USER_TABLE="USER";
    private static final String FILE_TABLE="file";
    private static final String USER_COLUMN_NAME="USERNAME";
    private static final String PASS_COLUMN_NAME ="PASS";
    private static final String FILEB ="files";
    private static final String FNAME ="FNAME";
    private static final String FSIZE ="FSIZE";
    private static final String FOWNER ="FOWNER";
    private static final String ACCESS_P ="ACCESS_P";

    public FileCatalogDAO(String datasource, String dbms) throws SQLException, ClassNotFoundException {
        conn = createDatasource(datasource,dbms);
        queryStatements(conn);
    }

    public FileCatalogDAO() {

    }
    public FileCatalogDAO(String datasource,String dbms,String filename, String owner,
                          String accessPerm, int size, byte[] filedata) throws SQLException, ClassNotFoundException {
        conn = connectToFileCatalogDB(datasource,dbms);
        uploadfileDB(filename,owner,accessPerm,size,filedata);
    }

    private Connection createDatasource(String datasource, String dbms) throws
            ClassNotFoundException, SQLException{
        conn = connectToFileCatalogDB(datasource,dbms);
        if (!tableExists(conn)) {
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE TABLE " + USER_TABLE
                    + " (" + USER_COLUMN_NAME + " VARCHAR(32) PRIMARY KEY, "
                    + PASS_COLUMN_NAME + " VARCHAR(32))");

            statement.executeUpdate("CREATE TABLE " + FILE_TABLE
                    + " (" + FNAME + " VARCHAR(32) PRIMARY KEY, "
                    + FOWNER + " VARCHAR(32), "
                    + FSIZE + " INT , "
                    + ACCESS_P + " VARCHAR(32), "
                    + FILEB + " LONGBLOB )");



        }
        return conn;
    }
    /**
     * @registerUser Creates a new user, userInterface is the user to be created
     * @param userInterface is an instance of UserImpl object but of type UserInterface,
     * referencing instance of the class as instance of the interface
     * @throwsSQLException
     */
    public void registerUser(UserInterface userInterface) throws SQLException {
        createUserStmt.setString(1, userInterface.getName());
        createUserStmt.setString(2,userInterface.getPassword());
        int rows = createUserStmt.executeUpdate();
        if(rows != 1){
            System.out.println("Unable to register new user!!");
        }
    }

    //Set resultset TO list of type UserImpl to be accessed by client
    public List<FileServerImpl> listFiles() throws SQLException {
        List<FileServerImpl> filelist = new ArrayList<>();
        listFilesStmt = conn.prepareStatement("SELECT * FROM file");
        ResultSet resultSet = listFilesStmt.executeQuery();
        while(resultSet.next()){
            filelist.add(new FileServerImpl(resultSet.getString("filename"),
                                        resultSet.getInt("filesize"),
                                        resultSet.getString("fileowner"),
                                        resultSet.getString("accessPerm")));
        }
        return  filelist;
    }

    public UserImpl findUser(String username) throws SQLException{
        findUserStmt = conn.prepareStatement("SELECT * FROM "
                + USER_TABLE
                + " WHERE name = ?");
        findUserStmt.setString(1,username);
        ResultSet rs = findUserStmt.executeQuery();
        if(rs.next()){
            return new UserImpl(username,rs.getString(PASS_COLUMN_NAME),this);
        }
        return null;
    }

    public void deleteUser(UserImpl userimpl) throws SQLException{
        deleteUserStmt.setString(1,userimpl.getName());
        deleteUserStmt.executeUpdate();
    }
    public void uploadfileDB(String filename, String owner,
                             String accessPerm, int size, byte[] fdatabytes) throws SQLException, ClassNotFoundException {

        uploadFileStmt = conn.prepareStatement("INSERT INTO file values (?, ?, ?, ?, ?)");
        uploadFileStmt.setString(1,filename);
        uploadFileStmt.setString(2,owner);
        uploadFileStmt.setString(3,accessPerm);
        uploadFileStmt.setInt(4,size);
        uploadFileStmt.setBytes(5,fdatabytes);
        uploadFileStmt.executeUpdate();
    }

    private void queryStatements(Connection conn) throws SQLException {
        createUserStmt = conn.prepareStatement("INSERT INTO " + USER_TABLE + " VALUES(?, ?)");

        deleteUserStmt = conn.prepareStatement("DELETE FROM "
                                                    + USER_TABLE
                                                    + " WHERE name = ?");

        findUserStmt = conn.prepareStatement("SELECT * FROM "
                                                    + USER_TABLE
                                                    + " WHERE name = ?");


/*        uploadFileStmt = conn.prepareStatement("INSERT INTO "
                + FILE_TABLE + " VALUES (? )");*/
        //uploadFileStmt = conn.prepareStatement("INSERT INTO file (files) values (?)");

    }


    private boolean tableExists(Connection connection) throws SQLException {
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connection.getMetaData();
        try (ResultSet rs = dbm.getTables(null, null, null, null)) {
            for (; rs.next();) {
                if (rs.getString(tableNameColumn).equals(USER_TABLE)) {
                    return true;
                }
            }
            return false;
        }
    }




    //TO DO write SQL statement to add user to the database



    public Connection connectToFileCatalogDB(String datasource,String dbms) throws ClassNotFoundException, SQLException {
        if(dbms.equalsIgnoreCase("mysql")){
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + datasource, "root", "root");
            return conn;
        }
        System.out.println("Connection Returned Null: Unable to create datasource, unknown dbms.");
        return null;
    }



}
