import java.net.*;
import java.io.*;

/**
 * This class acts as a listener for the Port and is designed to run in parallel with the associated Port.
 * Sends received messages to the Port by adding messages to the queued_messages list in Port.
 */
public class PortListener extends Thread {

    DatagramSocket socket;
    private int cypher_shift = 5;

    /**
     * Runs the listen() method.
     */
    public void run() {
        listen();
    }

    /**
     * Constructor for a PortListener
     * @param socket The socket which this PortListener will listen on.
     */
    public PortListener(DatagramSocket socket) {
        this.socket = socket;
    }

    /**
     * The main forever loop that checks for incoming messages and handles them.
     */
    public void listen() {
        while(true) {
            System.out.println("PortListener: - listening");
            byte[] buffer = new byte[1024];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);

            try {
                socket.receive(response);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String message_string = new String(buffer, 0, response.getLength());
            System.out.println("Port #" + socket.getPort() + ": A message has been received (still encrypted): " + message_string);
            Message received_message = decypher(decrypt(message_string, cypher_shift));
            System.out.println("Port #" + socket.getPort() + ": A message has been received:" + received_message.toString());
            Port.addMessagetoQueue(received_message);
        }
    }

    /**
     * Decrypts the message by shifting back all the characters in a String.
     * @param encryptedMessage
     * @param shift The shift value.
     * @return The decrpted message.
     */
    public String decrypt(String encryptedMessage, int shift){
        String decrypted = "";
        for(int i = 0; i < encryptedMessage.length(); i++){
            decrypted += (char) ((int)encryptedMessage.charAt(i) - shift);
        }
        return decrypted;
    }

    /**
     * This method takes a String representation of the incoming message (the protocol message).
     * It converts this String into an actual Message object to make processing easier.
     * @param rawString
     * @return
     */
    public Message decypher(String rawString) {
        int pos1 = rawString.indexOf(":");
        int pos2 = rawString.indexOf(":", pos1 + 1);
        int pos3 = rawString.indexOf(":", pos2 + 1);
        String receiver = rawString.substring(0, pos1);
        String sender = rawString.substring(pos1 + 1, pos2);
        String type = rawString.substring(pos2 + 1, pos3);
        String body = rawString.substring(pos3 + 1, rawString.length());

        User sender_user = Server.getUser(sender);
        User receiver_user;
        if(receiver != "null") {
            receiver_user = Server.getUser(receiver);
        } else {
            receiver_user = null;
        }
        int type_int = Integer.parseInt(type);

        Message newMessage = new Message(sender_user, receiver_user, body, type_int);
        return newMessage;
    }

}
