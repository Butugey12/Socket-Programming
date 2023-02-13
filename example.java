//Client example
//Run this after you run server, using "java example" (after compiling with javac)
import java.net.*;
import java.io.*;

public class example {
    
    public static void main(String[] args) {
        
        // setting up hostname and port number
        String hostname = "localhost";
        int port = 17;
 
        try {
            // create InetAddress object using hostname and create DatagramSocket object.
            InetAddress address = InetAddress.getByName(hostname);
            DatagramSocket socket = new DatagramSocket();
 
            while (true) {
 
                // create DatagramPacket object to be sent as the request to the server.
                DatagramPacket request = new DatagramPacket(new byte[1], 1, address, port);
                // send the DatagramPacket through the DatagramSocket:
                socket.send(request);
                
                // create a byte buffer to recieve the response.
                byte[] buffer = new byte[1024];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);
 
                String quote = new String(buffer, 0, response.getLength());
 
                System.out.println(quote);
                System.out.println();
 
                Thread.sleep(10000);
            }
 
        // must include these exceptions!
        } catch (SocketTimeoutException ex) {
            System.out.println("Timeout error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Client error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}