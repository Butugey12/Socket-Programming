import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.*;

/**
 * This class is meant to run in parallel with Client.java.
 * It records all input from the user via the terminal, prompting them along the way.
 */
public class ClientInput extends Thread {
    public Queue<String> inputs; // A queue that stores all the inputs by the user.
    private Scanner sc; // The Scanner used to get input from the user.

    /**
     * Starts the ClientInput thread.
     */
    public void run() {
        System.out.println("** To send a message: type 1.\n** To request a list of online users: type 2.\n** To request all previous sent messages by you: type 3.");
        this.getInput();
    }

    /**
     * ClientInput contructor.
     * @param sc The scanner object initially created in Client.java.
     */
    public ClientInput(Scanner sc){
        inputs = new ConcurrentLinkedQueue<>();
        this.sc = sc;
    }

    /**
     * The main infinite loop that checks for user input.
     * There are multiple cases for user input which are all handled within this loop.
     */
    public void getInput() {
        String line;
        String receiver;
        
                line = sc.nextLine();

                if(Integer.parseInt(line) == 1) {
                    // normal message
                    System.out.println("Who would you like to send the message to?");
                    System.out.println("Please type their entire username. To get the users list, quit this prompt and type \"2\"");
                    System.out.println("Quit this prompt - q");

                    line = sc.nextLine();
                    if(line.equals("q")){
                        System.out.println("Exiting prompt");
                    } else {
                        receiver = line;
                        System.out.println("What would you like to say?");
                        line = sc.nextLine();
                        inputs.add("1:" + receiver + ":" + line);
                    }

                } else if (Integer.parseInt(line) == 2) {
                    // users list request
                    inputs.add("2");

                } else if(Integer.parseInt(line) == 3){
                    // personal history request
                    inputs.add("3");

                } else {
                    System.out.println("** To send a message: type 1.\n** To request a list of online users: type 2.\n** To request all previous sent messages by you: type 3.");
                }

                getInput();
        
    }

    /**
     * @return The size of the input array.
     */
    public int getInputsSize(){
        return this.inputs.size();
    }
}
