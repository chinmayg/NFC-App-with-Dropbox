package com.example.nfc;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class WelcomeActivity extends TabActivity {

	private TabHost mTabHost;
	private Button logout;
	private WelcomeActivity welcomeActivityHelper;
		
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		Intent intentNfc = getIntent();
	    if(intentNfc.getType() != null && intentNfc.getType().equals("application/" + getPackageName())) {
	        // Read the first record which contains the NFC data
	        Parcelable[] rawMsgs = intentNfc.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	        NdefRecord relayRecord = ((NdefMessage)rawMsgs[0]).getRecords()[0];
	        String nfcData = new String(relayRecord.getPayload());
	 
	        // Display the data on the tag
	        Toast.makeText(this, nfcData, Toast.LENGTH_SHORT).show();
	 
	        // Do other stuff with the data...
	 
	        // Just finish the activity
	        finish();
	    }
		
		
		this.welcomeActivityHelper =  this;
		this.logout = (Button) findViewById(R.id.button_Logout);
		this.logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Later add error checking 
				DropboxSession.getInstance(welcomeActivityHelper).logOut();
				Intent intentToMain = new Intent(welcomeActivityHelper, MainActivity.class);
				welcomeActivityHelper.startActivity(intentToMain);
				finish();
			
			}
		});
		this.mTabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		
		//Encrypt Tab
		intent = new Intent(this,EncryptActivity.class);
		spec = mTabHost.newTabSpec("encrypt").setIndicator("Encrypt")
				.setContent(intent);
		mTabHost.addTab(spec);
		
		//Create URL Tab
		intent = new Intent(this,CreateURLActivity.class);
		spec = mTabHost.newTabSpec("createURL").setIndicator("Create URL")
				.setContent(intent);
		mTabHost.addTab(spec);
		
		//Change Settings Tab
		intent = new Intent(this,ChangeSettingActivity.class);
		spec = mTabHost.newTabSpec("changeSetting").setIndicator("Change Setting")
				.setContent(intent);
		mTabHost.addTab(spec);
		
		//Read Tag Tab
		intent = new Intent(this,ReadTagActivity.class);
		spec = mTabHost.newTabSpec("readTag").setIndicator("Read Tag")
				.setContent(intent);
		mTabHost.addTab(spec);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

}
