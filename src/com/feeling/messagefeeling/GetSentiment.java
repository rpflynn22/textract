package com.feeling.messagefeeling;
 
 
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;
 
public class GetSentiment {
         
    /* Takes in an ArrayList of SMS objects, and returns the overall
     * sentiment by referencing POS on a scale from 0 to 1
     */
    public static double eval(ArrayList<Sms> listOfMessages) throws JSONException {
    	StringBuilder allMessages = new StringBuilder();
    	int charCount = 0;
        for (Sms textMessage : listOfMessages) {
        	charCount += textMessage.text.length() + 1;
        	if (charCount > 50000) {
        		break;
        	}
            allMessages.append(textMessage.text);
            allMessages.append(" ");
        }
        return sentiVal(allMessages.toString());
    }
    
    public static Double sentiVal(String words) {
                 
        JSONObject returnFile;
        try {
            returnFile = new AsyncNetAccess().execute(words).get();
            String label = returnFile.getString("label");
            Double d;
            if (label.equals("neutral")) {
                d = 0.0;
            } else if (label.equals("pos")){
                d = returnFile.getJSONObject("probability").getDouble("pos") * 10.0;
            } else {
                d = returnFile.getJSONObject("probability").getDouble("neg") * (-10.0);
            }
            return d;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0.0;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return 0.0;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0.0;
        }       
    }
}