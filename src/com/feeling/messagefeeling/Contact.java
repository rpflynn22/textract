package com.feeling.messagefeeling;

import java.util.ArrayList;

public class Contact {
	
	/** Creates a Contact object. */
	public Contact(String name0, ArrayList<Sms> messages0) {
		name = name0;
		messages = messages0;
	}
	
	/** Sets the messages for this contact. */
	void setMessages(ArrayList<Sms> messages0) {
		messages = messages0;
	}

	/** The list of contacts for a particular contact. */
	ArrayList<Sms> messages = new ArrayList<Sms>();
	/** The name of a particular contact. */
	String name = "";

}
