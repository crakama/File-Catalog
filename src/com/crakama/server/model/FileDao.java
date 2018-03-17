package com.crakama.server.model;

public class FileDao {
    public String registerUser() {
        return "User registered successfully!!";
    }

    /**
     * Queries the DB, and returns a value of type Object(User) for a specific user found
     * @param name
     * @return
     */
    public User findUserByName(String name) {
        //TODO: Query the database and return
        return new User(name, "password",this);
    }
}
