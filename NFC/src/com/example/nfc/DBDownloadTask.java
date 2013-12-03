/*
 * Created by Chinmay Ghotkar
 * Date Modified:11/24/2013
 * 
 * This class was created to download files from dropbox. 
 * It will pop up a download dialog and progress bar while the app downloads a file in
 * the background.
 *
 */

package com.example.nfc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

import encyrption.Security;

public class DBDownloadTask extends AsyncTask<Boolean, Long, Boolean> {

	private Security invokingActivity;
	
	private Context mContext;
	private final ProgressDialog mDialog;
	private DropboxAPI<AndroidAuthSession> mApi;
	private String mPath;
	private static final String TAG = "DBDownload";

	private FileOutputStream mFos;

	private Long mFileLen;
	private String mErrorMsg;

	public DBDownloadTask(Context context, String dropboxPath, Security invoker) {
		// We set the context this way so we don't accidentally leak activities
		mContext = context.getApplicationContext();
		invokingActivity = invoker;
		mApi = DropboxSession.getApi();
		mPath = dropboxPath;	
		
		mDialog = new ProgressDialog(context);
		mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDialog.setProgress(0);
		mDialog.setMax(100);
		mDialog.setMessage("Downloading File");

		mDialog.show();
	}

	@Override
	protected Boolean doInBackground(Boolean... params) {
		try {
			// Get the metadata for a directory
			Entry file = mApi.metadata(mPath, 1000, null, true, null);

			String path = file.path;
			mFileLen = file.bytes;

			String cachePath = mContext.getCacheDir().getAbsolutePath() + "/"
					+ file.fileName();

			try {
				mFos = new FileOutputStream(cachePath);
			} catch (FileNotFoundException e) {
				mErrorMsg = "Couldn't create a local file to store the image";
				return false;
			}

			DropboxFileInfo info = mApi.getFile(path, null, mFos,
					new ProgressListener() {
						@Override
						public long progressInterval() {
							// Update the progress bar every half-second or so
							return 100;
						}

						@Override
						public void onProgress(long bytes, long total) {
							publishProgress(bytes);
						}
					});
			Log.i(TAG, "The file's download is: " + info.getFileSize() + " "
					+ cachePath);
			return true;

		} catch (DropboxUnlinkedException e) {
			// The AuthSession wasn't properly authenticated or user unlinked.
		} catch (DropboxPartialFileException e) {
			// We canceled the operation
			mErrorMsg = "Download canceled";
		} catch (DropboxServerException e) {
			// Server-side exception. These are examples of what could happen,
			// but we don't do anything special with them here.
			if (e.error == DropboxServerException._304_NOT_MODIFIED) {
				// won't happen since we don't pass in revision with metadata
			} else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
				// Unauthorized, so we should unlink them. You may want to
				// automatically log the user out in this case.
			} else if (e.error == DropboxServerException._403_FORBIDDEN) {
				// Not allowed to access this
			} else if (e.error == DropboxServerException._404_NOT_FOUND) {
				// path not found (or if it was the thumbnail, can't be
				// thumbnailed)
			} else if (e.error == DropboxServerException._406_NOT_ACCEPTABLE) {
				// too many entries to return
			} else if (e.error == DropboxServerException._415_UNSUPPORTED_MEDIA) {
				// can't be thumbnailed
			} else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
				// user is over quota
			} else {
				// Something else
			}
			// This gets the Dropbox error, translated into the user's language
			mErrorMsg = e.body.userError;
			if (mErrorMsg == null) {
				mErrorMsg = e.body.error;
			}
		} catch (DropboxIOException e) {
			// Happens all the time, probably want to retry automatically.
			mErrorMsg = "Network error.  Try again.";
		} catch (DropboxParseException e) {
			// Probably due to Dropbox server restarting, should retry
			mErrorMsg = "Dropbox error.  Try again.";
		} catch (DropboxException e) {
			// Unknown error
			mErrorMsg = "Unknown error.  Try again.";
		}
		return false;
	}

	@Override
	protected void onProgressUpdate(Long... progress) {
		int percent = (int) (100.0 * (double) progress[0] / mFileLen + 0.5);
		mDialog.setProgress(percent);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// mDialog.dismiss();
		mDialog.cancel();
		if (result) {
			int lastForwardSlash = mPath.lastIndexOf('/');
			String source = mContext.getCacheDir().getAbsolutePath()
					+ mPath.substring(lastForwardSlash);
			invokingActivity.handleSecurity(source, mPath.substring(lastForwardSlash));
		} else {
			// Couldn't download it, so show an error
			showToast(mErrorMsg);
		}
	}

	private void showToast(String msg) {
		Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
		error.show();
	}

}
