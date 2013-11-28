package com.example.nfc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.media.AudioManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class ChangeSettingActivity extends Activity {

	// Reference to the WiFi manager
	private WifiManager mWifiManager;

	// Resources for expandable listview
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_setting);

		// get the listview
		this.expListView = (ExpandableListView) findViewById(R.id.lvExp);

		// preparing list data
		prepareListData();

		this.listAdapter = new ExpandableListAdapter(this, listDataHeader,
				listDataChild);

		// setting list adapter
		this.expListView.setAdapter(listAdapter);

		// Listview on child click listener
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Toast.makeText(
						getApplicationContext(),
						listDataHeader.get(groupPosition)
								+ " : "
								+ listDataChild.get(
										listDataHeader.get(groupPosition)).get(
										childPosition), Toast.LENGTH_SHORT)
						.show();

				if (listDataHeader.get(groupPosition).trim().equals("Audio")) {
					changeAudio(listDataChild
							.get(listDataHeader.get(groupPosition))
							.get(childPosition).trim());
				} else if (listDataHeader.get(groupPosition).trim()
						.equals("Bluetooth")) {
					changeBluetooth(listDataChild
							.get(listDataHeader.get(groupPosition))
							.get(childPosition).trim());
				} else if (listDataHeader.get(groupPosition).trim()
						.equals("Display")) {
					changeDisplay(listDataChild
							.get(listDataHeader.get(groupPosition))
							.get(childPosition).trim());
				}
				return false;
			}
		});

		/*
		 * mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		 * 
		 * if(mWifiManager == null) Toast.makeText(this,
		 * "No Wifi Manager found!", Toast.LENGTH_SHORT).show(); else {
		 * List<WifiConfiguration> connections =
		 * mWifiManager.getConfiguredNetworks(); ArrayList<String>
		 * connectionInfo = new ArrayList<String>();
		 * 
		 * for(int i = 0; i < connections.size(); i++)
		 * connectionInfo.add(connections.get(i).SSID.replace("\"", ""));
		 * 
		 * ListView listView = (ListView) findViewById(R.id.listView_Main);
		 * 
		 * ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
		 * this,android.R.layout.simple_list_item_1, connectionInfo);
		 * 
		 * listView.setAdapter(arrayAdapter); }
		 */
	}

	/*
	 * Change Audio Settings
	 */
	private void changeAudio(String mode) {
		AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (mode.equals("Normal")) {
			am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			Toast.makeText(this, "Phone Set to Normal Mode", Toast.LENGTH_SHORT)
					.show();
		} else if (mode.equals("Silent")) {
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			Toast.makeText(this, "Phone Set to Silent Mode", Toast.LENGTH_SHORT)
					.show();
		} else {
			am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			Toast.makeText(this, "Phone Set to Vibrate Mode",
					Toast.LENGTH_SHORT).show();
		}

	}

	/*
	 * Changes Bluetooth Settings
	 */

	private void changeBluetooth(String mode) {
		BluetoothAdapter blue = BluetoothAdapter.getDefaultAdapter();
		if (mode.equals("Enable")) {
			if (!blue.isEnabled()) {
				blue.enable();
				Toast.makeText(this, "Bluetooth Enabled", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(this, "Bluetooth Already Enabled",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			blue.disable();
		}
	}

	/*
	 * Change Display Settings
	 */

	private void changeDisplay(String mode) {
		if (mode == "Low") {
			android.provider.Settings.System.putInt(this.getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS, 0);
			Toast.makeText(this, "Brightness Set Low",
					Toast.LENGTH_SHORT).show();
		} else if (mode == "Medium") {
			android.provider.Settings.System.putInt(this.getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS, 80);
			Toast.makeText(this, "Brightness Set Medium",
					Toast.LENGTH_SHORT).show();
		} else {
			android.provider.Settings.System.putInt(this.getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS, 250);
			Toast.makeText(this, "Brightness Set High",
					Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * Preparing the list data
	 */
	private void prepareListData() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		// Adding child data
		listDataHeader.add("Wifi");
		listDataHeader.add("Audio");
		listDataHeader.add("Display");
		listDataHeader.add("Bluetooth");

		// Adding child data

		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		ArrayList<String> connectionInfo = null;

		if (mWifiManager == null)
			Toast.makeText(this, "No Wifi Manager found!", Toast.LENGTH_SHORT)
					.show();
		else {
			List<WifiConfiguration> connections = mWifiManager
					.getConfiguredNetworks();
			connectionInfo = new ArrayList<String>();

			for (int i = 0; i < connections.size(); i++)
				connectionInfo.add(connections.get(i).SSID.replace("\"", ""));
		}

		ArrayList<String> audio = new ArrayList<String>();
		audio.add("Normal");
		audio.add("Silent");
		audio.add("Vibrate");

		ArrayList<String> display = new ArrayList<String>();
		display.add("Low");
		display.add("Medium");
		display.add("High");

		ArrayList<String> bluetooth = new ArrayList<String>();
		bluetooth.add("Enable");
		bluetooth.add("Disable");

		listDataChild.put(listDataHeader.get(0), connectionInfo); // Header,
																	// Child
																	// data
		listDataChild.put(listDataHeader.get(1), audio);
		listDataChild.put(listDataHeader.get(2), display);
		listDataChild.put(listDataHeader.get(3), bluetooth);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_setting, menu);
		return true;
	}

}

/*
 * <ListView android:id="@+id/listView1" android:layout_width="match_parent"
 * android:layout_height="wrap_content" android:layout_alignParentBottom="true"
 * android:layout_alignParentLeft="true" android:layout_below="@+id/fileOut" >
 * 
 * </ListView>
 */