package com.feeling.messagefeeling;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

import android.content.Context;

public class FileInteract {
	
	public static final String FILENAME = "user_data.txt";
	
	/** Returns a HashMap from a string key (contact phone number) to a HashMap containing
	 *  mappings from different pieces of data from each conversation with that contact. */
	public static HashMap<String, String> readFile(Context context) {
		FileInputStream fis = null;
		String s = null;
		try {
			fis = context.openFileInput(FILENAME);
			byte[] dataArray = new byte[fis.available()];
			if (dataArray.length == 0) {
				return null;
			}
			while (fis.read(dataArray) != -1) {
				s = new String(dataArray);
			}			
			fis.close();			
			HashMap<String, String> map = new HashMap<String, String>();
			Scanner scan = new Scanner(s);
			while (scan.hasNext()) {
				String match = scan.nextLine();
				String[] listDict = match.split(":");
				map.put(listDict[0], listDict[1]);
			}
			scan.close();
			return map;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}		
	}

	static void writeData(Context context, String number, double overallSentiment) {
		HashMap<String, String> map = readFile(context);
		if (map == null) {
			map = new HashMap<String, String>();
		}
		map.put(number, String.format(Locale.US, "%.1f", overallSentiment));
		try {
			FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
			for (String contact : map.keySet()) {
				fos.write(contact.getBytes());
				fos.write(":".getBytes());
				fos.write(map.get(contact).getBytes());
				fos.write("\n".getBytes());
			}
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}