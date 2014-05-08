package com.feeling.messagefeeling;

import java.util.ArrayList;

import org.json.JSONException;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SmsRetrieval {
	
	/** The main method for the SmsRetrieval. */
	public static double[] smsMain(Context context, String number, boolean direction) {
		ArrayList<Sms> messagesFirst = null;
		String[] locations = new String[] {"address", "date", "body"};;		
		Uri contentUri;
		if (direction) {
			contentUri = Uri.parse("content://sms/inbox");
		} else {
			contentUri = Uri.parse("content://sms/sent");
		}
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(
					contentUri,
		            locations,
		            null,
		            null,
		            null);
			
			if (cursor.moveToFirst()) {
				messagesFirst = new ArrayList<Sms>();
				do {
					String address = cursor.getString(0);
					if (address.equals(number)) {
						String body = cursor.getString(2);
						Sms sms = new Sms(body, address, direction);
						messagesFirst.add(sms);
					}
				} while (cursor.moveToNext());
				cursor.close();
				return new double[] {GetSentiment.eval(messagesFirst), messagesFirst.size()};
			} else {
				cursor.close();
				return new double[] {0.0, 0.0};
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				cursor.close();
			} catch (NullPointerException n) {
				
			}
			return new double[] {0.0, 0.0};
		}
	}

	
	/** Returns the sentiment of the most recent NUMBER of messages. */
	public static double getRecentSentiment(Context context, int number, boolean direction) {
		int count = number;
		Uri SMS_INBOX_CONTENT_URI;
		if (direction) {
			SMS_INBOX_CONTENT_URI = Uri.parse("content://sms/inbox");
		} else {
			SMS_INBOX_CONTENT_URI = Uri.parse("content://sms/sent");
		}
		String WHERE_CONDITION = "address = " + number;
		Cursor cursor = context.getContentResolver().query(
	            SMS_INBOX_CONTENT_URI,
	            new String[] {"address", "date", "body" },
	            WHERE_CONDITION,
	            null,
	            null);
		cursor.moveToFirst();
		ArrayList<Sms> messages = new ArrayList<Sms>();
		do {
			String address = cursor.getString(0);
			String body = cursor.getString(2);
			Sms sms = new Sms(body, address, direction);
			messages.add(sms);
			count -= 1;
		} while (cursor.moveToNext() && count > 0);
		double sentiment = 0.0;
		try {
			sentiment = GetSentiment.eval(messages);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		cursor.close();
		return sentiment;
	}
	
	/** Returns a list of contacts with whom the user has had a conversation. */
	public static ArrayList<String> getContacts(Context context) {
		ArrayList<String> lst = new ArrayList<String>();
		Cursor cursor = context.getContentResolver().query(
				Uri.parse("content://sms/inbox"),
	            new String[] {"address"},
	            null,
	            null,
	            "date DESC");
		cursor.moveToFirst();
		do {
			String address = cursor.getString(0);
			if (!lst.contains(address)) lst.add(address);
		} while (cursor.moveToNext());
		return lst;
	}
}
