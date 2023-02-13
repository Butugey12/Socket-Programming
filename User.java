import java.util.*;
import java.net.*;

/**
 * This class is mostly used on Server-side.
 * It makes transferring and extraction of information easier and clearer.
 */
public class User {
    private String username;
    private InetAddress address;
    private int port_number;

    /**
     * The constructor for the User class.
     * @param username User's username.
     * @param address Inet Address of User's place of residence.
     * @param port_number Port number which the User is using at their place of residence.
     */
    public User(String username, InetAddress address, int port_number) {
        this.username = username;
        this.address = address;
        this.port_number = port_number;
    }

    /**
     * 
     * @return The username of the user.
     */
    public String getUsername(){
        return this.username;
    }

    /**
     * 
     * @return The Inet Address of the user.
     */
    public InetAddress getAddress(){
        return this.address;
    }

    /**
     * 
     * @return The port number of the user.
     */
    public int getPort(){
        return this.port_number;
    }

    public String toString(){
        return "User: " + this.getUsername() + "\nAddress: " + this.getAddress() + "\nPort: " + this.getPort();
    }
}
