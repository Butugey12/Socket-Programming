//Server example
//Run this first with "java server 17" (after using javac to compile) MUST USE "sudo"
import java.net.*;
import java.io.*;

public class server {
    // the socket used by the server.
    private DatagramSocket socket;
 
    // constructor called after main method.
    public server(int port) throws SocketException {
        socket = new DatagramSocket(port);
        System.out.println("Socket has been created!");
    }
 
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
 
        try {
            // attempt to create the server and thus the DatagramSocket at port 17.
            server server = new server(port);
            // after socket creation, run the service (listening forever for request).
            server.service();
        } catch (SocketException ex) {
            System.out.println("Socket error: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
 
    private void service() throws IOException {
        while (true) {
            // wait for incoming DatagramPacket..
            DatagramPacket request = new DatagramPacket(new byte[1], 1);
            
            // the program will hang until a request is received:
            socket.receive(request);
            System.out.println("request recieved!");
            
            // this is the message to be sent, wrapped as a byte.
            String quote = "It works...";
            byte[] buffer = quote.getBytes();
 
            // you can get the senders address and port from the DatagramPacket!
            InetAddress clientAddress = request.getAddress();
            int clientPort = request.getPort();
 
            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            socket.send(response);
        }
    }
 
}