import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.net.*;
import java.io.*;

/**
 *  This class is paired with the Client class.
 *  It listens forever for any messages sent on the port the Client is using.
 *  When it receives a message, it stores it in the ArrayList "received_messages".
 */
public class ClientListener extends Thread {
    private DatagramSocket socket; // The socket that messages will be received into.
    public Queue<Message> received_messages; // Holds all messages received. Most recently added are most recently received.
    private int cypher_shift = 5;

    public void run(){
        listen();
    }

    /**
     * The ClientListener constructor.
     * @param socket The socket that messages will be received into.
     */
    public ClientListener(DatagramSocket socket) {
        this.socket = socket;
        received_messages = new ConcurrentLinkedQueue<>();
    }

    /**
     * The main infinite loop that constantly listens for incoming messages.
     * When a message is received, it is converted into a Message object and added to the received_messages list.
     */
    public void listen(){
        while(true){
            byte[] buffer = new byte[1024];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);

            try {
                socket.receive(response);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String raw_message = new String(buffer, 0, response.getLength());
            Message received_message = decypher(decrypt(raw_message, cypher_shift));

            received_messages.add(received_message);
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
        int type_int = Integer.parseInt(type);

        Message newMessage = new Message(sender, receiver, body, type_int);
        return newMessage;
    }
}
