import java.util.Scanner;
import java.util.ArrayList;
import java.net.*;
import java.io.*;

/**
 * This class is run on client-side. It has a ClientInput and ClientListener class.
 * Run with "sudo java Client".
 */
public class Client extends Thread {
    private String username;
    private String hostname;
    private ClientListener cl;
    private ClientInput ci;
    private DatagramSocket socket;
    private int port;
    private Scanner sc;
    private ArrayList<String> past_messages;
    private int cypher_shift = 5;

    /**
     * The constructor for Client. Enstantiates important non-changing variables such as the hostname and main server port number.
     */
    public Client(){
        System.out.print("Please type in a username: ");
        sc = new Scanner(System.in);
        username = sc.nextLine();
        hostname = "localhost";
        port = 17;
        past_messages = new ArrayList<>();
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        Client new_client = new Client();
        new_client.start();
    }

    /**
     * Creates a connection between the client and server side over the network.
     * Also creates and starts the ClientListener and ClientInput objects.
     * Finally calls the service method of Client.
     */
    public void run(){
        this.setup();

        cl = new ClientListener(this.socket);
        ci = new ClientInput(sc);
        cl.start();
        ci.start();
        
        this.service();
    }

    /**
     * Establishes a connection with the Server. Does not stop until a connection is established.
     */
    public void setup(){

        boolean online = false;
        
        while(!online){ 
            try {
                InetAddress address = InetAddress.getByName(hostname);
                
                DatagramPacket request = new DatagramPacket(username.getBytes(), username.length(), address, port);
                socket.send(request);
                
                byte[] buffer = new byte[1024];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);
    
                String message = new String(buffer, 0, response.getLength());

    
                System.out.println(message);
                System.out.println();

                this.port = Integer.parseInt(message.substring(message.indexOf(":") + 2, message.length()));
                
                online = true;

            } catch (SocketTimeoutException ex) {
                System.out.println("Timeout error: " + ex.getMessage());
                ex.printStackTrace();
            } catch (IOException ex) {
                System.out.println("Client error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * An infinite loop that checks for new inputs and received messages.
     * If a new message has been received or a new input has been added, this method deals with those cases.
     */
    public void service() {
        Message message;
        String input;
        int dummy = 0; //have to add this for some weird reason. Most likely due to mulithreading.
        int pos_first_colon;
        int pos_second_colon;
        String receiver;
        String text;

        for(;;) {
            dummy = dummy + 1;
            
            if(cl.received_messages.size() > 0){
                message = cl.received_messages.poll();

                if(message.getMessageType() == 1){ // A normal message received
                    System.out.print(message.getSenderString() + ": ");
                    try {
                        this.send(message.getSenderString() + ":" + username + ":4:#Client received message!\n", InetAddress.getByName(hostname), this.port);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(message.getMessageBody());
            }

            if(ci.getInputsSize() > 0){
                input = ci.inputs.poll();

                if(input.equals("3")){
                    System.out.println("#PAST MESSAGES#\n");
                    for(int i = 0; i < past_messages.size(); i++){
                        System.out.println(past_messages.get(i));
                        System.out.println("");
                    }
                    System.out.println("###############");
                } else 
                
                if(input.equals("2")){
                    try {
                        this.send(username + ":" + username + ":2:<<GetUsers>>", InetAddress.getByName(hostname), this.port);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                } else

                if(input.substring(0,1).equals("1")){
                    pos_first_colon = input.indexOf(":");
                    pos_second_colon = input.indexOf(":", pos_first_colon + 1);

                    receiver = input.substring(pos_first_colon + 1, pos_second_colon);
                    text = input.substring(pos_second_colon + 1, input.length());

                    past_messages.add("To: " + receiver + "\n\t" + text);

                    try {
                        this.send(receiver + ":" + username + ":1:" + text, InetAddress.getByName(hostname), this.port);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    System.out.println("#Message sent!");
                }
                
            }
            
            if(dummy > 800000){
                System.out.print("");
                dummy = 0;
            }

        } // end of infinite loop.
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
     * This method sends a DatagramPacket to the desired destination.
     * @param message A correctly-formatted (to protocol) String representation of the message.
     * @param address The Inet Address of the receiver.
     * @param port The port number of the receiving device.
     */
    public void send(String message, InetAddress address, int port) {
        message = encrypt(message, cypher_shift);
        DatagramPacket dg = new DatagramPacket(message.getBytes(), message.length(), address, port);
        try {
            this.socket.send(dg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }   
    
}
