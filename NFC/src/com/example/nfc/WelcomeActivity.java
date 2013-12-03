package com.example.nfc;

import java.io.IOException;
import java.nio.charset.Charset;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The writing portion of the code was taken from
 * https://shanetully.com/2012/12/
 * writing-custom-data-to-nfc-tags-with-android-example/
 * 
 * @author Arjun Passi, Sidhartha Tondapu
 * 
 */
@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class WelcomeActivity extends TabActivity {

	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mReadTagFilters;
	

	private Tag detectedTag;
	private IntentFilter[] ndefExchangeFilters_;

	static String nfcWriteData = "";
	
	private boolean readTag = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		

		setUpNfcFilters();
		// Setting up on click listeners
		setUpWriteButton();
		setUpLogOutButton();
		setUpTabs();

	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String nfcData = intent
					.getParcelableExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			// Display the data on the tag
			Toast.makeText(this, detectedTag.toString() + nfcData,
					Toast.LENGTH_SHORT).show();
		}

		else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			// Read the first record which contains the NFC data
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefRecord relayRecord = ((NdefMessage) rawMsgs[0]).getRecords()[0];
			String nfcData = new String(relayRecord.getPayload());
			
			setTagChooseRead(nfcData);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	protected void onResume() {
		super.onResume();
		mNfcAdapter.enableForegroundDispatch(this, mPendingIntent,
				mReadTagFilters, null);
	}

	@SuppressLint("NewApi")
	public static boolean writeTag(Context context, Tag tag, String data) {
		if(tag == null)
			return false;
		// Record to launch Play Store if app is not installed
		NdefRecord appRecord = NdefRecord
				.createApplicationRecord("com.example.nfc");

		// Record with actual data we care about
		NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				new String("application/com.example.nfc").getBytes(Charset
						.forName("US-ASCII")), null, data.getBytes());

		// Complete NDEF message with both records
		NdefMessage message = new NdefMessage(new NdefRecord[] { relayRecord,
				appRecord });

		try {
			// If the tag is already formatted, just write the message to it
			Ndef ndef = Ndef.get(tag);
			if (ndef != null) {
				ndef.connect();
				// Make sure the tag is writable
				if (!ndef.isWritable())
					return false;
				// Check if there's enough space on the tag for the message
				int size = message.toByteArray().length;
				if (ndef.getMaxSize() < size)
					return false;
				try {
					// Write the data to the tag
					ndef.writeNdefMessage(message);
					return true;
				} catch (TagLostException e) {
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				} catch (FormatException e) {
					e.printStackTrace();
					return false;
				}
				// If the tag is not formatted, format it with the message
			} else {
				NdefFormatable format = NdefFormatable.get(tag);
				if (format != null) {
					try {
						format.connect();
						format.format(message);
						return true;
					} catch (TagLostException e) {
						e.printStackTrace();
						return false;
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					} catch (FormatException e) {
						e.printStackTrace();
						return false;
					}
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private void setTagChooseRead(final String nfcData){
		AlertDialog.Builder readBuilder = new AlertDialog.Builder(this);
		readBuilder.setTitle("NFC Tag Discovered")
				.setMessage("Do you want to read from the tag?")
				.setCancelable(true)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						readTag = true;
						setUp(nfcData);
						dialog.dismiss();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								readTag = false;
								dialog.dismiss();
							}
						});

		AlertDialog readAlert = readBuilder.create();
		if(!readAlert.isShowing())
			readAlert.show();
	}
	
	private void setUp(String nfcData){
		int first = nfcData.indexOf(":");
		if (first < 0)
			first = 0;

		String subString = nfcData.substring(0, first);
		Intent intent;
		// Tag contains data for decryption activity
		if (subString.contains("DA")) {
			intent = new Intent(this, DecryptActivity.class);
			intent.putExtra("nfcData", nfcData);
			WelcomeActivity.this.startActivity(intent);
		}
		// Tag contains data for create url activity
		else if (subString.contains("CUA")) {
			// mTabHost.setCurrentTab(1);
			final TextView message = new TextView(WelcomeActivity.this);
			message.setGravity(Gravity.CENTER);
			String subString2 = nfcData.substring(first + 1,
					nfcData.length()).replace("\n", "");
			String url = subString2 + "?dl=1";
			final SpannableString s = new SpannableString(url);
			Linkify.addLinks(s, Linkify.WEB_URLS);
			message.setText(s);
			message.setMovementMethod(LinkMovementMethod.getInstance());
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Click Link to Start Downloading")
					.setView(message)
					.setCancelable(true)
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							});

			AlertDialog welcomeAlert = builder.create();
			welcomeAlert.show();
		}

		// Tag contains data for change settings activity
		else if (subString.contains("RCSA")) {
			intent = new Intent(this, ReadChangeSettingActivity.class);
			intent.putExtra("nfcData", nfcData);
			WelcomeActivity.this.startActivity(intent);
		}

		else
			// Display the data on the tag
			Toast.makeText(this, "Invalid Data on the Tag! " + nfcData,
					Toast.LENGTH_SHORT).show();
	}
	/**
	 * This method sets ups the on click listener for the program NFC / Write
	 * NFC button
	 */
	private void setUpWriteButton() {
		Button b = (Button) findViewById(R.id.buttonWrite);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final ProgressDialog mDialog = new ProgressDialog(WelcomeActivity.this);
				mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				mDialog.setMessage("Writting on the tag...");
				mDialog.show();
				boolean isWritten = writeTag(WelcomeActivity.this, detectedTag,
						WelcomeActivity.nfcWriteData);
				if(!isWritten)
					Toast.makeText(WelcomeActivity.this,
							"Writing on Tag failed!", Toast.LENGTH_SHORT).show();
				
				else
					Toast.makeText(WelcomeActivity.this,
							"Writing on Tag succedded!", Toast.LENGTH_SHORT).show();
				mDialog.cancel();
			}

		});
	}

	/**
	 * This method sets up the on click listener for the log out button
	 */
	private void setUpLogOutButton() {
		Button b = (Button) findViewById(R.id.button_Logout);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DropboxSession.getInstance(WelcomeActivity.this).logOut();
				Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
				WelcomeActivity.this.startActivity(intent);
			}

		});
	}

	/**
	 * This methods sets up all the tabs on the welcome activity.
	 */
	private void setUpTabs() {
		TabHost mTabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		// Encrypt Tab
		intent = new Intent(this, EncryptActivity.class);
		spec = mTabHost.newTabSpec("encrypt").setIndicator("Encrypt")
				.setContent(intent);
		mTabHost.addTab(spec);

		// Create URL Tab
		intent = new Intent(this, CreateURLActivity.class);
		spec = mTabHost.newTabSpec("createURL").setIndicator("Create URL")
				.setContent(intent);
		mTabHost.addTab(spec);

		// Change Settings Tab
		intent = new Intent(this, ChangeSettingActivity.class);
		spec = mTabHost.newTabSpec("changeSetting")
				.setIndicator("Change Setting").setContent(intent);
		mTabHost.addTab(spec);

	}

	/**
	 * This method sets up the NFC filters.
	 */
	private void setUpNfcFilters() {
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter == null) {
			Toast.makeText(this, "NFC hardware not available on device!",
					Toast.LENGTH_SHORT).show();
			finish();
		}
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		// Intent filters for reading a note from a tag or exchanging over p2p.
		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefDetected.addDataType("application/com.example.nfc");
		} catch (IntentFilter.MalformedMimeTypeException e) {
		}
		ndefExchangeFilters_ = new IntentFilter[] { ndefDetected };
	}

}
