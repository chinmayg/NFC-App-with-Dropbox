package com.example.nfc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class WelcomeActivity extends TabActivity {

	
	private TabHost mTabHost;
	private Button logout;
	private WelcomeActivity welcomeActivityHelper;
	
	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mReadTagFilters;
	private IntentFilter[] mWriteTagFilters;
	
	private Tag detectedTag;
	private IntentFilter[] ndefExchangeFilters_;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		String nfcData ="";
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        
        // Intent filters for reading a note from a tag or exchanging over p2p.
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("application/com.example.nfc");
        } catch (IntentFilter.MalformedMimeTypeException e) { }
        ndefExchangeFilters_ = new IntentFilter[] { ndefDetected };
	        
		
		welcomeActivityHelper =  this;
		logout = (Button) findViewById(R.id.button_Logout);
		logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				writeTag(WelcomeActivity.this,detectedTag, "Arjun Passi is the Best");
			}
		});
		
		mTabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		
		//Encrypt Tab
		intent = new Intent(this,EncryptActivity.class);
		intent.putExtra("nfcData", nfcData);
		spec = mTabHost.newTabSpec("encrypt").setIndicator("Encrypt")
				.setContent(intent);
		mTabHost.addTab(spec);
		
		//Create URL Tab
		intent = new Intent(this,CreateURLActivity.class);
		intent.putExtra("nfcData", nfcData);
		spec = mTabHost.newTabSpec("createURL").setIndicator("Create URL")
				.setContent(intent);
		mTabHost.addTab(spec);
		
		//Change Settings Tab
		intent = new Intent(this,ChangeSettingActivity.class);
		intent.putExtra("nfcData", nfcData);
		spec = mTabHost.newTabSpec("changeSetting").setIndicator("Change Setting")
				.setContent(intent);
		mTabHost.addTab(spec);
		
		//Read Tag Tab
		intent = new Intent(this,DecryptActivity.class);
		intent.putExtra("nfcData", nfcData);
		spec = mTabHost.newTabSpec("readTag").setIndicator("Read Tag")
				.setContent(intent);
		mTabHost.addTab(spec);
	}
	
	@Override
    protected void onNewIntent(Intent intent) {
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        	String nfcData = intent.getParcelableExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            // Display the data on the tag
	        Toast.makeText(this, detectedTag.toString() + nfcData, Toast.LENGTH_SHORT).show();
        }
        
        else if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
        	detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        	// Read the first record which contains the NFC data
	        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	        NdefRecord relayRecord = ((NdefMessage)rawMsgs[0]).getRecords()[0];
	        String nfcData = new String(relayRecord.getPayload());
	        // Display the data on the tag
	        Toast.makeText(this, nfcData, Toast.LENGTH_SHORT).show();
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}
	
	protected void onResume(){
		super.onResume();
		mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mReadTagFilters, null);
	}

	@SuppressLint("NewApi")
	public static boolean writeTag(Context context, Tag tag, String data) {     
	    // Record to launch Play Store if app is not installed
	    NdefRecord appRecord = NdefRecord.createApplicationRecord("application/com.example.nfc");
	 
	    // Record with actual data we care about
	    NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
	                                            new String("application/com.example.nfc").getBytes(Charset.forName("US-ASCII")),
	                                            null, data.getBytes());
	 
	    // Complete NDEF message with both records
	    NdefMessage message = new NdefMessage(new NdefRecord[] {relayRecord, appRecord});
	 
	    try {
	        // If the tag is already formatted, just write the message to it
	        Ndef ndef = Ndef.get(tag);
	        if(ndef != null) {
	            ndef.connect();
	 
	            // Make sure the tag is writable
	            if(!ndef.isWritable()) {
	                return false;
	            }
	 
	            // Check if there's enough space on the tag for the message
	            int size = message.toByteArray().length;
	            if(ndef.getMaxSize() < size) {
	                return false;
	            }
	 
	            try {
	                // Write the data to the tag
	                ndef.writeNdefMessage(message);
	                return true;
	            } catch (TagLostException tle) {
	                return false;
	            } catch (IOException ioe) {
	                return false;
	            } catch (FormatException fe) {
	                return false;
	            }
	        // If the tag is not formatted, format it with the message
	        } else {
	            NdefFormatable format = NdefFormatable.get(tag);
	            if(format != null) {
	                try {
	                    format.connect();
	                    format.format(message);
	                    return true;
	                } catch (TagLostException tle) {
	                    return false;
	                } catch (IOException ioe) {
	                    return false;
	                } catch (FormatException fe) {
	                    return false;
	                }
	            } else {
	                return false;
	            }
	        }
	    } catch(Exception e) {
	    }
	 
	    return false;
	}
}
