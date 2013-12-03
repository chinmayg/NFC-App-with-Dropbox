package com.example.nfc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import encyrption.EncryptionHelper;
import encyrption.Security;

import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//-------------------------------------------------------------------------
/**
*  This activity is hidden till a NFC chip is brought closer
*  Once blown up, it downloads the file from dropbox and decrypts it
*
*  @author Bishwamoy Sinha Roy
*  @version Nov 24, 2013
*/
public class DecryptActivity extends Activity implements Security {

	private TextView file_out;
	private Button returnToPrev;
	private Button seePreview;

	String key;
	String url;
	String decryptedFile = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_decrypt);

		// get intent to access the extras (has the NFC data)
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();

		file_out = (TextView) findViewById(R.id.fileOut);
		returnToPrev = (Button) findViewById(R.id.doneButton);
		seePreview = (Button) findViewById(R.id.previewButton);
		returnToPrev.setEnabled(false);
		seePreview.setEnabled(false);

		file_out.setText("Dont click the buttons yet.");

		returnToPrev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		seePreview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPreview(decryptedFile);
			}
		});

		// make sure NFCdata is readable
		if (bundle.containsKey("nfcData")) {
			// get data from the NFC
			// key and url (KVPs)
			String nfcData = bundle.getString("nfcData");
			String[] info = nfcData.split("\n");
			if (info.length != 3) {
				Toast.makeText(
						this,
						"ERROR: Incorrect formatting of nfcData, click button!",
						Toast.LENGTH_LONG).show();
				returnToPrev.setEnabled(true);
			} else {
				if (info[1].substring(0, info[1].indexOf(":")).equals("key")
						&& info[2].substring(0, info[2].indexOf(":")).equals(
								"url")) {
					key = info[1].substring(info[1].indexOf(":") + 1);
					url = info[2].substring(info[2].indexOf(":") + 1);
				} else {
					Toast.makeText(
							this,
							"ERROR: Incorrect formatting of nfcData, click button!",
							Toast.LENGTH_LONG).show();
					returnToPrev.setEnabled(true);
				}

			}
		} else {
			Toast.makeText(this,
					"ERROR: Incorrect formatting of nfcData, click button!",
					Toast.LENGTH_LONG).show();
			returnToPrev.setEnabled(true);
		}

		DBDownloadTask dbDown = new DBDownloadTask(DecryptActivity.this, url,
				DecryptActivity.this);
		dbDown.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.decrypt, menu);
		return true;
	}

	// -------------------------------------------------------------------------
	/**
	 *  This method is called by the download task once the file has been downloaded
	 *  This method decrypts file.
	 */
	public void handleSecurity(String fileLoc, String fileName) {
		// make sure external storage is reachable
		if (!isExternalStorageReadable() && !isExternalStorageWritable()) {
			file_out.setText("Cant write to external storage!!!");
			return;
		}
		this.decryptedFile = this.getExternalCacheDir().getAbsolutePath() + "/"
				+ "encrypted" + fileName.substring(1);
		// decrypt file
		EncryptionHelper helper = new EncryptionHelper(10);
		boolean success = helper.decrypt(fileLoc, this.decryptedFile, key);
		if (success) {
			file_out.setText("File stored at " + decryptedFile);
		} else {
			this.file_out.setText("Error decrypting!");
		}
		// set buttons as accessible
		this.returnToPrev.setEnabled(true);
		this.seePreview.setEnabled(true);
	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	//-------------------------------------------------------------------------
	/**
	*  Used to show a preview of the decrypted file.
	*/
	public void showPreview(String fileLoc) {
		// iterate through file and print a maximum of 6 lines
		try {
			file_out.setText("Preview:" + "\n");
			BufferedReader file = new BufferedReader(new FileReader(fileLoc));
			int lines = 0;
			String line = "";
			while ((line = file.readLine()) != null && lines < 6) {
				file_out.append(line + "\n");
				lines++;
			}
			file.close();
		} catch (IOException e) {
			file_out.append("Problem accessing file!");
			e.printStackTrace();
		}
	}
}
