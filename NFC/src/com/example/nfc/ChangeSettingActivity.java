package com.example.nfc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dropbox.chooser.android.R.color;

import android.media.AudioManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

/**
 * This Activity provides the user an interface to choose
 * Audio, Blue tooth, WiFi, and diplay settings that can be
 * programmed on an NFC tag.
 * @author Arjun Passi, Siddartha Tondapu
 *
 */
public class ChangeSettingActivity extends Activity {

	// Reference to the WiFi manager
	private WifiManager mWifiManager;
	private RelativeLayout settingsLayout;

	// Resources for expandable listview
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;

	private String mSSID = "";
	private String mPassword = "";
	private String mSecurityType = "";
	private String mAudioSetting = "";
	private String mBrightnessSetting = "";
	private String mBluetoothSetting = "";

	private List<WifiConfiguration> mConnections;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_setting);

		// get the listview
		expListView = (ExpandableListView) findViewById(R.id.lvExp);

		// preparing list data
		prepareListData();

		listAdapter = new ExpandableListAdapter(this, listDataHeader,
				listDataChild);

		// setting list adapter
		expListView.setAdapter(listAdapter);

		// Listview on child click listener
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

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
				} else if (listDataHeader.get(groupPosition).trim()
						.equals("Wifi")) {
					mSSID = mConnections.get(childPosition).SSID.replace("\"", "");
					mSecurityType = passwordType(mConnections.get(childPosition));
					mPassword = "";
					prepareNfcString();
					if(mSecurityType.equals("WPA-PSK"))
						getWiFiPasswordFromUser(mSSID);
					else if(!mSecurityType.equals("OPEN"))
						toast(mSecurityType + "security not supported");
					else if(mSecurityType.equals("OPEN"))
						toast(mSSID + " selected");
				}
				return false;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		prepareListData();
	}
	
	/**
	 * This method changes the Audio Settings
	 * @param mode
	 */
	private void changeAudio(String mode) {
		if (mode.equals("Normal")) {
			mAudioSetting = "N";
			toast("Phone Set to Normal Mode");
		} else if (mode.equals("Silent")) {
			mAudioSetting = "S";
			toast("Phone Set to Silent Mode");
		} else {
			mAudioSetting = "V";
			toast("Phone Set to Vibrate Mode");
		}
		prepareNfcString();
	}

	/**
	 * This method changes the Blue tooth settings
	 * 
	 * @param mode
	 */
	private void changeBluetooth(String mode) {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (bluetoothAdapter == null)
			toast("Device does not support bluetooth!");

		else if (mode.equals("Enable")) {
			if (!bluetoothAdapter.isEnabled()) {
				mBluetoothSetting = "E";
				toast("Bluetooth Enabled");
			}
			else
				toast("Bluetooth Already Enabled");
		}
		else {
			toast("Bluetooth Disabled!");
			mBluetoothSetting = "D";
		}
		
		prepareNfcString();
	}

	/**
	 * This method changes the display settings
	 * 
	 * @param mode
	 */
	private void changeDisplay(String mode) {
		if (mode == "Low") {
			mBrightnessSetting = "L";
			toast("Brightness Set Low");
		} else if (mode == "Medium") {
			mBrightnessSetting = "M";
			toast("Brightness Set Medium");
		} else {
			mBrightnessSetting = "H";
			toast("Brightness Set High");
		}
		
		prepareNfcString();
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
			toast("No Wifi Manager found!");
		else {
			mWifiManager.setWifiEnabled(true);
			mConnections = mWifiManager.getConfiguredNetworks();
			connectionInfo = new ArrayList<String>();
			for (int i = 0; i < mConnections.size(); i++)
				connectionInfo.add(mConnections.get(i).SSID.replace("\"", "") + " ("
						+ passwordType(mConnections.get(i)) + ")");
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

		listDataChild.put(listDataHeader.get(0), connectionInfo);
		listDataChild.put(listDataHeader.get(1), audio);
		listDataChild.put(listDataHeader.get(2), display);
		listDataChild.put(listDataHeader.get(3), bluetooth);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.change_setting, menu);
		return true;
	}

	/**
	 * This method is responsible for getting a WiFi password
	 * from a user given the network name.
	 * @param networkName
	 */
	@SuppressWarnings("deprecation")
	private void getWiFiPasswordFromUser(final String networkName) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("WiFi Password");
		alertDialog.setMessage("Please provde the password for " + networkName
				+ "!");

		final EditText input = new EditText(this);
		alertDialog.setView(input);

		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mPassword = input.getText().toString().trim();
				prepareNfcString();
				toast(networkName + " selected");
			}
		});
		alertDialog.show();
	}
	
	/**
	 * This method returns the password type of a WiFi network
	 * @param wifiConfig
	 * @return password type
	 */
	private String passwordType(WifiConfiguration wifiConfig){
		
		if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA_PSK))
	        return "WPA-PSK";
		else  if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA_EAP) ||
	    		wifiConfig.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
	        return "WPA-EAP";
	    }
	    return (wifiConfig.wepKeys[0] != null) ? "WEP" : "OPEN";
	}
	
	/**
	 * This method prepares a NFC string to be written
	 * to the tag.
	 */
	private void prepareNfcString(){
		StringBuilder build = new StringBuilder();
		build.append("RCSA:\n");
		build.append("Audio " + mAudioSetting + "\n");
		build.append("Display " + mBrightnessSetting + "\n");
		build.append("BT " + mBluetoothSetting + "\n");
		build.append("SSID " + mSSID + "\n");
		build.append("PASS " + mPassword + "\n");
		build.append("SEC " + mSecurityType + "\n");
		
		WelcomeActivity.nfcWriteData = build.toString();
	}

	/**
	 * This method puts a toast on the screen
	 * @param message
	 */
	private void toast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

}
