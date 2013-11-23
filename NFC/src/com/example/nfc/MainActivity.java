package com.example.nfc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


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





/*import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	//Defining the GUI elements
	private EditText username, password;
	private Button login;
	private MainActivity activityHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.username = (EditText) findViewById(R.id.editText_Username);
		this.password = (EditText) findViewById(R.id.editText_Password);
		this.login = (Button) findViewById(R.id.button_Login);
		this.activityHelper = this;
		
		this.login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(username.getText().toString().trim().matches("") || 
						password.getText().toString().trim().matches("")){
					Toast.makeText(activityHelper, "Missing Information. Please Check Again!!!",
							Toast.LENGTH_SHORT).show();
					
					
				}else{
					
					//Later add error checking 
					Intent intentToWelcome = new Intent(activityHelper, WelcomeActivity.class);
					activityHelper.startActivity(intentToWelcome);
				}	
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}*/


/*
 <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingBottom="@dimen/activity_vertical_margin"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    tools:context=".WelcomeActivity" >

    <TextView
        android:id="@+id/textView_Welcome"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/hello_world" />

    <TabHost
        android:id="@android:id/tabhost"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_alignParentLeft="true"
        android:layout_alignParentTop="true" >

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical" >

            <TabWidget
                android:id="@android:id/tabs"
                android:layout_width="match_parent"
                android:layout_height="wrap_content" >
            </TabWidget>

            <FrameLayout
                android:id="@android:id/tabcontent"
                android:layout_width="match_parent"
                android:layout_height="match_parent" >

                <LinearLayout
                    android:id="@+id/encrypt"
                    android:text="Encrypt"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent" >
                </LinearLayout>
                
                <LinearLayout
                    android:id="@+id/createUrl"
                    android:text="Create URL"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent" >
                    
                    <Button 
				        android:id="@+id/button_Login"
				        android:layout_below ="@+id/layout_password"
				 		android:layout_width="fill_parent"
				        android:layout_height="wrap_content"
				        android:layout_weight="1"
				        android:text="LOGIN/REGISTER2" />
                </LinearLayout>

                <LinearLayout
                    android:id="@+id/changeSetting"
                    android:text="Change Settings"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent" >
                </LinearLayout>

                <LinearLayout
                    android:id="@+id/readTag"
                    android:text="Read Tag"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent" >
                </LinearLayout>
            </FrameLayout>
        </LinearLayout>
    </TabHost>

</RelativeLayout>
*/
