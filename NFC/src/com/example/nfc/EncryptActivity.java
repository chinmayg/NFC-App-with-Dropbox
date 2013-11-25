package com.example.nfc;

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

public class EncryptActivity extends Activity {

	private Button openFile;
	private DbxChooser mChooser;
	static final String APP_KEY = "1p4kimx81tdhsce";
    static final int DBX_CHOOSER_REQUEST = 0; 
	private static final int FILE_SELECT_CODE = 0;
	private String fileDown = "/Photos/2013-11-18-23-32-30.jpg";
	private static final String TAG = "DBUpload";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_encrypt);
		
        mChooser = new DbxChooser(APP_KEY);
		this.openFile = (Button)findViewById(R.id.button_openFile);
		this.openFile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//showFileChooser();
				mChooser.forResultType(DbxChooser.ResultType.DIRECT_LINK).launch(EncryptActivity.this,DBX_CHOOSER_REQUEST );
			}
		});
	}
	
//	private void showFileChooser(){
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//		intent.setType("*/*"); 
//		intent.addCategory(Intent.CATEGORY_OPENABLE);
//		
//		try {
//	        startActivityForResult(
//	                Intent.createChooser(intent, "Select a File to Upload"),
//	                FILE_SELECT_CODE);
//	    } catch (android.content.ActivityNotFoundException ex) {
//	        // Potentially direct the user to the Market with a Dialog
//	        Toast.makeText(this, "Please install a File Manager.", 
//	                Toast.LENGTH_SHORT).show();
//	    }	
//	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
			case FILE_SELECT_CODE:
				if(resultCode == RESULT_OK){
					
					DbxChooser.Result result = new DbxChooser.Result(data);	                
	                showLink(R.id.textView_output, result.getLink());
	                fileDown = result.getLink().toString().substring(56);
	                Log.i(TAG, "The file's download is: " + fileDown);
	                DBDownloadTask dbDown = new DBDownloadTask(EncryptActivity.this,fileDown);
	                dbDown.execute();
//					Uri uri = data.getData();
////					output.append(uri.toString());
////					output.append("\n");
//					try {
//						String path = getPath(this,uri);
//						output.append(path);
//						EncryptionHelper encryptor = new EncryptionHelper(10);
//						String key = encryptor.encrypt(path, "storage/emulated/0/Android/data/com.dropbox.android/files/scratch/test.txt");
//						output.append(key);
//						
////						File file = new File(path);
////						output.append(file.getName());
////						output.append("\n\n");
////						output.append(file.toString());
//						//output.append(getPath(this,uri));
//					} catch (URISyntaxException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
				}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    private void showLink(int id, Uri uri) {
        TextView v = (TextView) findViewById(id);
        if (uri == null) {
            v.setText("", TextView.BufferType.NORMAL);
            return;
        }
        v.setText(uri.toString(), TextView.BufferType.NORMAL);
        v.setMovementMethod(LinkMovementMethod.getInstance());
    }
	
	public static String getPath(Context context, Uri uri) throws URISyntaxException {
	    if ("content".equalsIgnoreCase(uri.getScheme())) {
	        String[] projection = { "_data" };
	        Cursor cursor = null;

	        try {
	            cursor = context.getContentResolver().query(uri, projection, null, null, null);
	            int column_index = cursor.getColumnIndexOrThrow("_data");
	            if (cursor.moveToFirst()) {
	                return cursor.getString(column_index);
	            }
	        } catch (Exception e) {
	            // Eat it
	        }
	    }
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }

	    return null;
	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.encrypt, menu);
		return true;
	}

}
