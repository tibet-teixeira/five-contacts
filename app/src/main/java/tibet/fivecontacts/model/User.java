package tibet.fivecontacts.model;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;

public class User implements Serializable {
    private String name;
    private String login;
    private String password;
    private String email;
    private boolean keepConnected;
    private Set<String> saveContacts;

    /**
     * @param name
     * @param login
     * @param password
     * @param email
     * @param keepConnected
     * @param saveContacts
     */
    public User(String name, String login, String password, String email, boolean keepConnected, Set<String> saveContacts) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.email = email;
        this.keepConnected = keepConnected;
        this.saveContacts = saveContacts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isKeepConnected() {
        return keepConnected;
    }

    public void setKeepConnected(boolean keepConnected) {
        this.keepConnected = keepConnected;
    }

    public Set<String> getSaveContacts() {
        return saveContacts;
    }

    public void setSaveContacts(Set<String> saveContacts) {
        this.saveContacts = saveContacts;
    }
}