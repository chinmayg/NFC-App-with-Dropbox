package com.example.nfc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 
 * @author Siddartha Tondapu
 * Used as the loginpage. Logs in to the dropbox by prompting a different menu
 *
 */
public class MainActivity extends Activity {
	private static final String TAG = "LinkingActivity";

	// Android widgets
	private Button mSubmit;
	
	static private DropboxSession mDbSession;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Basic Android widgets
		setContentView(R.layout.activity_main);
		mDbSession = DropboxSession.getInstance(this);
		mDbSession.checkAppKeySetup();

		mSubmit = (Button) findViewById(R.id.button_Login);

		mSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Start the remote authentication
				mDbSession.logIn();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(mDbSession.isAuthSuccess()){
			Intent intentToWelcome = new Intent(this, WelcomeActivity.class);
			this.startActivity(intentToWelcome);
			finish();
		}
		else{
			Log.i(TAG, "Error authenticating");
		}
	}

	// This is what gets called on finishing a media piece to import
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_OK) {
			// return from file upload
			if (resultCode == Activity.RESULT_OK) {
				Log.i(TAG, "Went to Welcome Activity: "
						+ resultCode);
			} else {
				Log.w(TAG, "Unknown Activity Result from mediaImport: "
						+ resultCode);
			}
		}
	}
}