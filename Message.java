/**
 * This class represents a message request from one User to another.
 * Message objects are stacked in a static ArrayList in Port.java.
 */
public class Message {
    private User sender;
    private String sender_string;
    private User receiver;
    private String receiver_string;
    private String message_body;
    private int message_type;
        // - 1 Mail
        // - 2 User List Request
        // - 3 Server Confirmation
        // - 4 Client Confirmation

        /**
         * Message constructor 1 (used on Server-side)
         * @param sender
         * @param receiver
         * @param body
         * @param type
         */
    public Message(User sender, User receiver, String body, int type) {
        this.sender = sender;
        this.receiver = receiver;
        this.message_body = body;
        this.message_type = type;
    }

    /**
     * Message constructor 2 (String version for use on Client-side)
     * @param sender
     * @param receiver
     * @param body
     * @param type
     */
    public Message(String sender, String receiver, String body, int type) {
        this.sender_string = sender;
        this.receiver_string = receiver;
        this.message_body = body;
        this.message_type = type;
    }

    /**
     * @return username of the sender User.
     */
    public String getSenderString(){
        return this.sender_string;
    }

    /**
     * @return The sender User object.
     */
    public User getSender(){
        return this.sender;
    }

    /**
     * 
     * @return username of the receiver User.
     */
    public String getReceiverString(){
        return this.receiver_string;
    }

    /**
     * 
     * @return The receiver User object.
     */
    public User getReceiver(){
        return this.receiver;
    }

    /**
     * 
     * @return The message body. Contains the main message.
     */
    public String getMessageBody(){
        return this.message_body;
    }

    /**
     * 
     * @return The message type, used to identify what kind of information is being received.
     */
    public int getMessageType(){
        return this.message_type;
    }

    /**
     * 
     * @return A formatted String representation of all the message data. This is what is sent over the network.
     */
    public String getMessageData(){
        return receiver.getUsername() + ":" + sender.getUsername() + ":" + this.message_type + ":" + this.message_body;
    }

    public String toString(){ //careful, string version != constructor version.
        String receiver;
        String sender;
        if(this.receiver == null){
            receiver = "null";
        } else {
            receiver = this.receiver.getUsername();
        }
        if(this.sender == null){
            sender = "null";
        } else {
            sender = this.sender.getUsername();
        }
        return receiver + ":" + sender + ":" + this.message_type + ":" + this.message_body;
    }
}
