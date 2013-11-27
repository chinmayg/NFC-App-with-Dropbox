package com.example.nfc;

import java.io.File;

import com.dropbox.chooser.android.DbxChooser;

import encyrption.EncryptionHelper;
import encyrption.Security;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DecryptActivity extends Activity implements Security {

	private TextView file_out;
	private Button returnToPrev;
	private Button readFile;
	
	String key;
	String url;
	String decryptedFile = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_decrypt);

		file_out = (TextView) findViewById(R.id.fileOut);
		returnToPrev = (Button) findViewById(R.id.doneButton);
		readFile = (Button) findViewById(R.id.readB);

		returnToPrev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		readFile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				file_out.setText("Haven't implemented yet!");
			}
		});
		// nfc Data will be key:url
		// read from intent extras
		key = "";
		url = "";
		
		file_out.setText("Dont click the read button yet.");

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
		this.decryptedFile = this.getCacheDir().getAbsolutePath() + "/" + "encrypted"
				+ fileName;
		EncryptionHelper helper = new EncryptionHelper(10);
		boolean success = helper.decrypt(fileLoc, this.decryptedFile, key);
		if (success) {
			file_out.setText("Click the read button to read file.");
		} else {
			this.file_out.setText("Error decrypting!");
		}
	}
}
