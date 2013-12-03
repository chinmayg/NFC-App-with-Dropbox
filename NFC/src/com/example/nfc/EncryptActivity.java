package com.example.nfc;

import java.io.File;
import java.net.URISyntaxException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.dropbox.chooser.android.DbxChooser;

import encyrption.EncryptionHelper;
import encyrption.Security;

//-------------------------------------------------------------------------
/**
*  The activity that requests the user to choose a file from dropbox, downloads
*  the chosen file, encrypts the file, and uploads it to Dropbox.
*
*  @author Bishwamoy Sinha Roy and Chinmay Ghotkar
*  @version Nov 20, 2013
*/
public class EncryptActivity extends Activity implements Security {

	private Button openFile;
	private DbxChooser mChooser;
	static final String APP_KEY = "1p4kimx81tdhsce";
	static final int DBX_CHOOSER_REQUEST = 0;
	private static final int FILE_SELECT_CODE = 0;
	private String fileDown = "";
	private static final String TAG = "DBUpload";

	private TextView out;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_encrypt);
		out = (TextView) findViewById(R.id.textView_output);

		mChooser = new DbxChooser(APP_KEY);
		this.openFile = (Button) findViewById(R.id.button_openFile);
		this.openFile.setOnClickListener(new OnClickListener() {
			// clicking the button initiates the Dropbox file chooser pop-up
			@Override
			public void onClick(View v) {
				mChooser.forResultType(DbxChooser.ResultType.DIRECT_LINK)
						.launch(EncryptActivity.this, DBX_CHOOSER_REQUEST);
			}
		});
	}

	// -------------------------------------------------------------------------
	/**
	 *  This method is called once the user has chosen a file from the dropbox
	 *  chooser
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {		
		case FILE_SELECT_CODE:
			if (resultCode == RESULT_OK) {
				// get result (the link of the file) and download file.
				DbxChooser.Result result = new DbxChooser.Result(data);
				fileDown = result.getLink().toString().substring(56);
				fileDown = fileDown.replace("%20", " ");
				Log.i(TAG, "The file's download is: " + fileDown);
				DBDownloadTask dbDown = new DBDownloadTask(
						EncryptActivity.this, fileDown, EncryptActivity.this);
				dbDown.execute();
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// -------------------------------------------------------------------------
	/**
	 *  This method is called by the download task once the file has been downloaded
	 *  This method encrypts file.
	 */
	public void handleSecurity(String fileLoc, String fileName) {
		// create destination path in external
		String encryptedName = "encrypted" + fileName.substring(1);
		String dest = this.getCacheDir().getAbsolutePath() + "/"
				+ encryptedName;
		// object to encrypt file
		EncryptionHelper helper = new EncryptionHelper(10);
		String key = helper.encrypt(fileLoc, dest);
		// upload file at destination
		String DBPath = "AppUploads/";
		DBUploadTask up = new DBUploadTask(this, DBPath, new File(dest));
		up.execute();
		
		// write appt. string to NFC
		writeNFC(key, "/" + DBPath + encryptedName);

		// display the key and the url to the user
		StringBuilder toScreen = new StringBuilder();
		toScreen.append("KEY:" + key + "\n");
		toScreen.append("URL:/" + DBPath + encryptedName + "\n");
		dispString(toScreen.toString());
	}
	
	// -------------------------------------------------------------------------
	/**
	 *  This method is called to display text on screen to user
	 */
	public void dispString(String message) {
		if (message.length() == 0) {
			out.setText("", TextView.BufferType.NORMAL);
			return;
		}
		out.setText(message, TextView.BufferType.NORMAL);
		out.setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	// -------------------------------------------------------------------------
	/**
	 *  This method is called to write the key and url to the NFC
	 *  Basically modifies a static string in welcomeActivity
	 */
	public void writeNFC(String key, String URL)
	{
		StringBuilder toNFC = new StringBuilder();
		toNFC.append("DA:\n");
		toNFC.append("key:" + key + "\n");
		toNFC.append("url:" + URL + "\n");

		WelcomeActivity.nfcWriteData = toNFC.toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.encrypt, menu);
		return true;
	}

}
