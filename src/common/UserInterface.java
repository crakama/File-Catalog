package common;

import server.model.FileCatalogDAO;

import java.io.Serializable;

public interface UserInterface extends Serializable {
    // Food for thought: Client should use this to set name and pass from CMD


    String getName();

    String getPassword();
}
