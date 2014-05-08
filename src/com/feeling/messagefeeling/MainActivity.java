package com.feeling.messagefeeling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class MainActivity extends Activity {

	List<RowItem> rowItems;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final ArrayList<String> contactLst = SmsRetrieval.getContacts(this);
		HashMap<String, String> conversations = FileInteract.readFile(this);
		rowItems = new ArrayList<RowItem>();
		final HashMap<String, String> lookup = lookup();
		for (int i = 0; i < contactLst.size(); i += 1) {
			RowItem item;
			String contact = (lookup.get(contactLst.get(i)) != null) ? lookup.get(contactLst.get(i)) : contactLst.get(i);
			try {
				Double sentiment = Double.parseDouble(conversations.get(contactLst.get(i)));
				item = new RowItem(pickImage(sentiment), contact);
			} catch (NullPointerException e) {
				item = new RowItem(R.drawable.fair_plus_icon, contact);
			}
			rowItems.add(item);
		}
		ListView listview = (ListView) findViewById(R.id.list);
		CustomListViewAdapter adapter = new CustomListViewAdapter(this,
                R.layout.list_row, rowItems);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
		            long id) {
				String number = contactLst.get(position);
				Intent i = new Intent("com.feeling.messagefeeling.DETAILSACTIVITY");
				Bundle b = new Bundle();
				b.putString("contact", number);
				b.putString("name", lookup.get(number));
				i.putExtras(b);
		        startActivity(i);
		        finish();
		    }
		});
	}
	
	public HashMap<String, String> lookup() {
		HashMap<String, String> lookup = new HashMap<String, String>();
		String[] locations = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, locations, null, null, null);
		if (cur.moveToFirst()) {
			do {
				String name = cur.getString(0);
				String number = cur.getString(1);
				if (number.charAt(0) != '+') {
					lookup.put(number, name);
					number = "+1" + number;
				}
				lookup.put(number, name);
			} while(cur.moveToNext());
		}
		cur.close();
		return lookup;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public Integer pickImage(Double sentiment) {
		if (sentiment == null) {
			return R.drawable.fair_plus_icon;
		}
		if (sentiment > 7.5) {
			return R.drawable.very_good_icon;
		} else if (sentiment > 4) {
			return R.drawable.good_icon;
		} else if (sentiment > 0) {
			return R.drawable.fair_plus_icon;
		} else if (sentiment > -4) {
			return R.drawable.fair_minus_icon;
		} else if (sentiment > -7.5) {
			return R.drawable.bad_icon;
		} else {
			return R.drawable.really_bad_icon;
		}
	}
}
