package common;

import java.sql.SQLException;

public interface ServerTCPInterface {

    void serveClients() throws SQLException, ClassNotFoundException;


}
