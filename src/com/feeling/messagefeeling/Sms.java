package com.feeling.messagefeeling;
 
public class Sms {
 
    /** The Constructor for the Sms object. */
    public Sms(String text0, String contactName0, boolean inbox0) { //Address is contactName
        text = text0;
        contactName = contactName0;
        inbox = inbox0;
    }
     
    /** The text message. */
    String text;
    /** String representing the person who sent the text. Null if
     *  sent by user (outbox). */
    String contactName;
    /** Boolean value which is set to true when the message was received
     *  by the user. */
    boolean inbox;
}
