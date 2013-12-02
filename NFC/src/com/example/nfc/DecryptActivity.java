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

		if (bundle.containsKey("nfcData")) {
			String nfcData = bundle.getString("nfcData");
			String[] info = nfcData.split("\n");
			if (info.length != 3) {
				Toast.makeText(
						this,
						"ERROR: Incorrect formatting of nfcData, click button!",
						Toast.LENGTH_LONG).show();
			} else {
				if (info[1].substring(0, info[1].indexOf(":")).equals("key")
						&& info[2].substring(0, info[2].indexOf(":")).equals(
								"url")) {
					key = info[1].substring(info[1].indexOf(":"));
					url = info[1].substring(info[1].indexOf(":"));
				} else {
					Toast.makeText(
							this,
							"ERROR: Incorrect formatting of nfcData, click button!",
							Toast.LENGTH_LONG).show();
				}

			}
		} else {
			Toast.makeText(this,
					"ERROR: Incorrect formatting of nfcData, click button!",
					Toast.LENGTH_LONG).show();
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

	public void handleSecurity(String fileLoc, String fileName) {
		if (!isExternalStorageReadable() && !isExternalStorageWritable()) {
			file_out.setText("Cant write to external storage!!!");
			return;
		}
		this.decryptedFile = this.getExternalCacheDir().getAbsolutePath() + "/"
				+ "encrypted" + fileName.substring(1);
		EncryptionHelper helper = new EncryptionHelper(10);
		boolean success = helper.decrypt(fileLoc, this.decryptedFile, key);
		if (success) {
			file_out.setText("File stored at " + decryptedFile);
		} else {
			this.file_out.setText("Error decrypting!");
		}
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

	public void showPreview(String fileLoc) {
		try {
			file_out.setText("Preview:" + "\n");
			BufferedReader file = new BufferedReader(new FileReader(fileLoc));
			int lines = 0;
			String line = "";
			while ((line = file.readLine()) != null && lines < 6) {
				file_out.append(line + "\n");
			}
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			file_out.append("Problem accessing file!");
			e.printStackTrace();
		}
	}
}
