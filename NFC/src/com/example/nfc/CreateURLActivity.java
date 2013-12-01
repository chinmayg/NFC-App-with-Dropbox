package com.example.nfc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dropbox.chooser.android.DbxChooser;
import com.dropbox.client2.DropboxAPI;


public class CreateURLActivity extends Activity {
	
	private Button openFile;
	private DbxChooser mChooser;
	static final String APP_KEY = "1p4kimx81tdhsce";
    static final int DBX_CHOOSER_REQUEST = 0; 
	private static final int FILE_SELECT_CODE = 0;
	private String fileDown = "";
	DropboxAPI.DropboxLink mShareLink;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_url);
	
        mChooser = new DbxChooser(APP_KEY);
		this.openFile = (Button)findViewById(R.id.button_openFile);
		this.openFile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mChooser.forResultType(DbxChooser.ResultType.DIRECT_LINK).launch(CreateURLActivity.this,DBX_CHOOSER_REQUEST );
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
			case FILE_SELECT_CODE:
				if(resultCode == RESULT_OK){
					
					DbxChooser.Result result = new DbxChooser.Result(data);	
					fileDown = result.getLink().toString().substring(56);
					fileDown = fileDown.replace("%20", " ");
					DBShareLinkGenerator dbGen = new DBShareLinkGenerator(CreateURLActivity.this,fileDown,CreateURLActivity.this);
					dbGen.execute();
				}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_url, menu);
		return true;
	}

}
