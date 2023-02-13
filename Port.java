import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.net.*;
import java.io.*;

/**
 * Takes a user and an available port & deals with communication between Client and "Server".
 * One object of Port should only deal with one user. Ports may communicate among themselves (they are all considered Server-side).
 */

public class Port extends Thread {
    private User user;  // The user associated with this Port.
    private int server_port_number; // This Port's port number.
    private DatagramSocket socket;  // This Port's socket.
    private PortListener listener;  // This Port's PortListener, which listens for any messages from the Client.
    private static Queue<Message> queued_messages; // Messages to be sent by Ports. Every Port has access to this list.
    private int cypher_shift = 5;

    /**
     * This method sets up the flow of the Port.
     */
    public void run() {
        try {
            this.welcome();
        } catch (Exception e) {
            e.printStackTrace();
        }
        listener.start();
        service();
    }
    
    /**
     * Port constructor.
     * @param user The associated User of this Port.
     * @param server_port_number The Port number this Port will operate on.
     * @throws SocketException
     */
    public Port(User user, int server_port_number) throws SocketException
    {
        this.user = user;
        this.server_port_number = server_port_number;
        queued_messages = new ConcurrentLinkedQueue<>();
        socket = new DatagramSocket(server_port_number);
        listener = new PortListener(socket);
        System.out.println("--  A port has started on " + this.server_port_number);
        Server.addNewOnlinePort(this);
    }

    /**
     * Sends a welcome message to the Client, acting as a confirmation of connection by Server.
     * @throws Exception
     */
    public void welcome() throws Exception{
        Message welcome_message = new Message(null, this.user, "Welcome, " + user.getUsername() + "\nYou are operating on port number: " + server_port_number, 2);
        DatagramPacket packet = new DatagramPacket(welcome_message.getMessageBody().getBytes(), welcome_message.getMessageBody().length(), this.user.getAddress(), this.user.getPort());
        socket.send(packet);
        System.out.println("Port: Welcome message sent to client " + this.user.getUsername());
    }

    /**
     * After the welcome method, this method should run forever while the Port is alive.
     * It checks if there are any queued messages to deal with that are addressed to this Port.
     * The dummy variable is required for this while loop to run. For some reason it stops looping without this.
     */
    public void service(){
        Message message;
        int dummy = 0;

        while(true){
            // Check if you have mail in queued_messages
            dummy = dummy + 1;

            if(queued_messages.size() > 0){
                message = queued_messages.poll();

                if(message.getMessageType() == 4) {
                    System.out.println("Port: got Client message confirmation");
                    Message client_confirmation = new Message(message.getSender(), message.getReceiver(), message.getMessageBody(), 4);
                    this.send(client_confirmation.getMessageData(), message.getReceiver().getAddress(), message.getReceiver().getPort());
                }

                if(message.getMessageType() == 2) {
                    //Create Message object
                    Message to_send = new Message(message.getReceiver(), message.getSender(), Server.getOnlineUsers().toString(), 2);
                    this.send(to_send.getMessageData(), message.getReceiver().getAddress(), message.getReceiver().getPort());
                } 

                if(message.getMessageType() == 1){
                    //Check if user exists
                    if(message.getReceiver() != null) {
                        Message confirmation = new Message(message.getSender(), message.getSender(), "#Server received message!", 3);
                        this.send(confirmation.getMessageData(), message.getSender().getAddress(), message.getSender().getPort());
                        Message to_send = new Message(message.getSender(), message.getReceiver(), message.getMessageBody(), 1);
                        this.send(to_send.getMessageData(), message.getReceiver().getAddress(), message.getReceiver().getPort());
                    } else {
                        Message confirmation = new Message(message.getSender(), message.getSender(), "That user does not exist!\n", 3);
                        this.send(confirmation.getMessageData(), message.getSender().getAddress(), message.getSender().getPort());
                    }
                }
            }

            if(dummy > 10000) {
                dummy = -100;
            }
        }
    }

    /**
     * Encrypts a message using caesar's cipher method. 
     * @param normalMessage
     * @param shift
     * @return
     */
    public String encrypt(String normalMessage, int shift){
        String encrypt = "";
        for(int i = 0; i < normalMessage.length(); i++){
            encrypt += (char) ((int)normalMessage.charAt(i) + shift);
        }
        return encrypt;
    }

    /**
     * Sends a DatagramPacket to a address+port. 
     * @param message
     * @param address
     * @param port
     */
    public void send(String message, InetAddress address, int port){
        message = encrypt(message, cypher_shift);
        DatagramPacket dg = new DatagramPacket(message.getBytes(), message.length(), address, port);
        try {
            this.socket.send(dg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return The User associated with this Port.
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Adds a new message to the queued messages list.
     * @param newMessage
     */
    public static void addMessagetoQueue(Message newMessage) {
        queued_messages.add(newMessage);
        System.out.println(queued_messages.toString());
    }
}
