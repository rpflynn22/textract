package com.feeling.messagefeeling;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		Bundle b = getIntent().getExtras();
		final String number = b.getString("contact");
		final String name = b.getString("name");
		if (name != null) {
			setTitle(name);
		} else {
			setTitle(number);
		}
		SmsRetrieval.smsMain(this, number, true);
		double[] sentSentimentPair = SmsRetrieval.smsMain(this, number, false);
		double sentSentiment = sentSentimentPair[0];
		int numSent = (int) sentSentimentPair[1];
		String sentSentimentString = String.format(Locale.US, "%.1f", sentSentiment);
		double[] inboxSentimentPair = SmsRetrieval.smsMain(this, number, true);
		double inboxSentiment = inboxSentimentPair[0];
		int numInbox = (int) inboxSentimentPair[1];
		String inboxSentimentString = String.format(Locale.US, "%.1f", inboxSentiment);
		double overallSentiment = ((numSent * sentSentiment) + (numInbox * inboxSentiment)) / (numSent + numInbox);
		String overallSentimentString = String.format(Locale.US, "%.1f", overallSentiment);
		FileInteract.writeData(this, number, overallSentiment);
		int image1 = pickImage(overallSentiment);
		int image2 = pickImage(sentSentiment);
		int image3 = pickImage(inboxSentiment);
		TextView overallScoreTV = (TextView) findViewById(R.id.overallScoreText);
		TextView sentScoreTV = (TextView) findViewById(R.id.sentValText);
		TextView inboxScoreTV = (TextView) findViewById(R.id.inboxValText);
		ImageView bigScore = (ImageView) findViewById(R.id.overallImage);
		ImageView sentScore = (ImageView) findViewById(R.id.sentImage);
		ImageView inboxScore = (ImageView) findViewById(R.id.inboxImage);
		Button apolButton = (Button) findViewById(R.id.sendApology);
		apolButton.setText("Apologize!");
		overallScoreTV.setText(overallSentimentString);
		sentScoreTV.setText(sentSentimentString);
		inboxScoreTV.setText(inboxSentimentString);
		bigScore.setImageResource(image1);
		sentScore.setImageResource(image2);
		inboxScore.setImageResource(image3);
		if (sentSentiment > -8) {
			apolButton.setVisibility(View.GONE);
		}
		apolButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent sendIntent = new Intent(Intent.ACTION_VIEW);         
				sendIntent.setData(Uri.parse("sms:"));
				String msg = "Hi";
				if (name != null) {
					String[] nameLst = name.split(" ");
					msg += " " + nameLst[0];
				}
				msg += ". It seems that my texts to you have been somewhat negative "
						+ "lately, and I would like to apologize. I hope to talk soon.";
				sendIntent.putExtra("sms_body", msg);
				sendIntent.putExtra("address", number);
				sendIntent.setData(Uri.parse("smsto:" + number));
				startActivity(sendIntent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.details, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
	   Intent setIntent = new Intent("com.feeling.messagefeeling.MAINACTIVITY");
	   startActivity(setIntent);
	   finish();
	}
	
	public Integer pickImage(double sentiment) {
		if (sentiment > 7) {
			return R.drawable.very_good_icon;
		} else if (sentiment > 2.5) {
			return R.drawable.good_icon;
		} else if (sentiment > -1.5) {
			return R.drawable.fair_plus_icon;
		} else if (sentiment > -5) {
			return R.drawable.fair_minus_icon;
		} else if (sentiment > -7.5) {
			return R.drawable.bad_icon;
		} else {
			return R.drawable.really_bad_icon;
		}
	}

}
