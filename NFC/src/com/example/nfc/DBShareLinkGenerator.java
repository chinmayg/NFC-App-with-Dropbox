/*
 * Created by Chinmay Ghotkar
 * Date Modified:12/1/2013
 * 
 * This class will generate a share link for the file path choosen by the user
 *
 */

package com.example.nfc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

public class DBShareLinkGenerator extends AsyncTask<Void, Long, Boolean>{

	DropboxAPI.DropboxLink mShareLink;
	
	private final ProgressDialog mDialog;
	private DropboxAPI<AndroidAuthSession> mApi;
	private String mPath;
	private static final String TAG = "DBGenerate";
	private Activity mParentActivity;
	
	public DBShareLinkGenerator(Context context, String dropboxPath, Activity parent) {
		// We set the context this way so we don't accidentally leak activities
		mApi = DropboxSession.getApi();
		mPath = dropboxPath;	
		mParentActivity = parent;
		mDialog = new ProgressDialog(context);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setMessage("Generating a Sharing URL...");

		mDialog.show();
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			mShareLink = mApi.share(mPath);
		} catch (DropboxException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		mDialog.cancel();
		if(result){
            showLink(R.id.textView_output, mShareLink.url);
            prepareNfcString(mShareLink.url);
            Log.i(TAG,mShareLink.url);
		}
		else{
            Log.e(TAG,"Failed to Generate Share URL!");
		}
	}
	
	
	private void prepareNfcString(String url){
		StringBuilder build = new StringBuilder();
		build.append("CUA:\n");
		build.append(url+"\n");
		
		WelcomeActivity.nfcWriteData = build.toString();
	}
    private void showLink(int id, String uri) {
        TextView v = (TextView) mParentActivity.findViewById(id);
		final SpannableString s = new SpannableString(uri);
		Linkify.addLinks(s, Linkify.WEB_URLS);
		v.setText(s);
		v.setMovementMethod(LinkMovementMethod.getInstance());
        if (uri == null) {
            v.setText("", TextView.BufferType.NORMAL);
            return;
        }
    }

}
