package com.crakama.server.model;

import java.sql.*;

public class FileDao {
    private PreparedStatement createUserStmt,deleteUserStmt,
            findUserStmt,loginUserStmt,createFileInfo,findFileStmt;
    private static final String FILE_TABLE="file";
    private static final String FILEINFO_TABLE="fileinfo";
    private static final String USER_COLUMN_NAME="USERNAME";
    private static final String PASS_COLUMN_NAME ="PASS";
    private static final String FILEB ="files";
    private static final String FNAME ="fname";
    private static final String FSIZE ="fsize";
    private static final String FOWNER ="fowner";
    private static final String ACCESS_MODE ="filemode";
    private static final String[] tables = new String[2];

    public FileDao(String dbms,String datasource){
        try {
            Connection connection = createDataSource(dbms,datasource);
            sqlQueryStatements(connection);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("FileDao() " + e.getMessage());
        }
    }

     /**
     * Step 1: Reference to database session/Connection object
     * Used to send SQL statements to the database
     * @param datasource
     * @param dbms
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection connectDB(String dbms,String datasource) throws ClassNotFoundException, SQLException {
        if(dbms.equalsIgnoreCase("mysql")){
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + datasource, "root", "root");
        }else {
            System.out.println("Connection Returned Null: Unable to create datasource, unknown dbms.");
            return null;
        }
    }
    /**
     * Step 2: Create statement Objects -Initialisation
     * @return
     */
    private void sqlQueryStatements(Connection conn) throws SQLException {
        createUserStmt = conn.prepareStatement("INSERT INTO " + tables[0] + " VALUES(?, ?)");

        deleteUserStmt = conn.prepareStatement("DELETE FROM "
                + tables[0]
                + " WHERE USERNAME = ?");

        findUserStmt = conn.prepareStatement("SELECT * FROM "
                + tables[0]
                + " WHERE USERNAME = ?");
        findFileStmt = conn.prepareStatement("SELECT * FROM "
                + tables[1]
                + " WHERE fname = ?");
        loginUserStmt = conn.prepareStatement("SELECT * FROM "
                + tables[0]
                + " WHERE USERNAME = ? AND PASS= ?");
        createFileInfo = conn.prepareStatement("INSERT INTO " + tables[1] + " VALUES(?, ?, ?, ?)");
        //createFileInfo = conn.prepareStatement("INSERT INTO " + tables[1] + " VALUES(?, ?)");
    }

    /**
     * Step 3: Create tables to db
     * @null, same as *, gets information about all tables in the DB
     * Check if the resultset's 3rd column contains info about the table be created
     * @return
     */
    private Connection createDataSource(String dbms, String datasource) throws SQLException, ClassNotFoundException {
        Connection connection = connectDB(dbms,datasource);
        checkTables(connection);
        return connection;
    }

    /**
     * Check if table exists
     * @rs.getString retrieve the value on column 3(table column)
     * @param conn
     * @return
     * @throws SQLException
     */
    private void checkTables(Connection conn){
        DatabaseMetaData dbm = null;
        tables[0] = "userInfo";
        tables[1] = "fileInfo";
        try {
            dbm = conn.getMetaData();
            for(int i= 0;i<tables.length;i++){
                try (ResultSet rs = dbm.getTables(null, null, tables[i], null)) {
                    if(!rs.next()) {
                        createTable(conn,tables[i]);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("checkTables() " + e.getMessage());
        }

    }

    private void createTable(Connection con, String table) throws SQLException {
        Statement statement = con.createStatement();
        System.out.println("Parameters at createTable" + table);
        if(table.equalsIgnoreCase("userInfo")){
            statement.executeUpdate("CREATE TABLE " + table
                    + " (" + USER_COLUMN_NAME + " VARCHAR(32) PRIMARY KEY, "
                    + PASS_COLUMN_NAME + " VARCHAR(32))" );
        }else {
            statement.executeUpdate("CREATE TABLE " + table
                    + " (" + FNAME + " VARCHAR(32), "
                    + FOWNER + " VARCHAR(32), "
                    + ACCESS_MODE + " VARCHAR(32), "
                    + FSIZE + " INT)");
        }
    }


    public User registerUser(UserInterface userImpl) {
        String name = userImpl.getUserName();
        String password = userImpl.getPassword();
        try {
            createUserStmt.setString(2,password);
            createUserStmt.setString(1,name);
            int row = createUserStmt.executeUpdate();
            if(row != 1){
                String resp = "Unable to register user to database";
                return new User(resp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new User(name,password);
    }

    /**
     * Queries the DB, and returns a value of type Object(User) for a specific user found
     * @param name
     * @return
     */
    public User findUserByName(String name) {
        //TODO: Query the database and return
        try {
            findUserStmt.setString(1,name);
            ResultSet rs = findUserStmt.executeQuery();
            if(rs.next()){
                return new User(name,rs.getString(PASS_COLUMN_NAME),this);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User deleteUser(String userName, String password) {
        try {
            deleteUserStmt.setString(1,userName);
            deleteUserStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  new User(userName,password);
    }

    public int saveToDB(FileInterface fileInterface) {
        String name = fileInterface.getUserName();
        String owner = fileInterface.getOwner();
        String access = fileInterface.getAccessMode();
        int size = fileInterface.getSize();
        try {
            createFileInfo.setString(1,name);
            createFileInfo.setString(2,owner);
            createFileInfo.setString(3,access);
            createFileInfo.setInt(4,size);
            int row = createFileInfo.executeUpdate();
            if(row != 1){
                int code = 0;
                return code;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
    //TODO: Take care of same column name for file and user tables
    public FileCatalog findFileByName(String name) {
        try {
            findFileStmt.setString(1,name);
            ResultSet rs = findFileStmt.executeQuery();
            if(rs.next()){
                return new FileCatalog(name,rs.getString(FOWNER),rs.getString(ACCESS_MODE),this);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
