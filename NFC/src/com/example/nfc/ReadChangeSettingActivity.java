package com.example.nfc;

import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * This class is responsible for parsing the NFC data
 * from the tag. After the data is parsed the WiFi,
 * Blue tooth, Audio, and Display settings are changed.
 * @author Arjun Passi, Siddartha Tondapu
 *
 */
public class ReadChangeSettingActivity extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_change_setting);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		if(bundle.containsKey("nfcData")){
			String nfcData = bundle.getString("nfcData");
			if(nfcData.split("\n").length != 7){
				toast("Invalid Data");
				finish();
				return;
			}
			changeAudio(getAudioSetting(nfcData));
			changeBluetooth(getBluetoothSetting(nfcData));
			changeDisplay(getDisplaySetting(nfcData));
			
			String SSID = getSSID(nfcData);
			String pass = getPassword(nfcData);
			String sec = getSecurityType(nfcData);
			connectWiFi(SSID, sec, pass);
		}
		
		finish();
	}
	
	
	/**
	 * This method changes the Audio Settings
	 * @param mode
	 */
	private void changeAudio(String mode) {
		AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (mode.equals("N"))
			am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		else if (mode.equals("S"))
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		else if(mode.equals("V"))
			am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
	}
	
	/**
	 * This method changes the display settings
	 * @param mode
	 */
	private void changeDisplay(String mode) {
		if (mode.equals("L"))
			android.provider.Settings.System.putInt(this.getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS, 0);
		else if (mode.equals("M"))
			android.provider.Settings.System.putInt(this.getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS, 80);
		else if(mode.equals("H"))
			android.provider.Settings.System.putInt(this.getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS, 250);
	}
	
	/**
	 * This method changes the Blue tooth settings
	 * @param mode
	 */
	private void changeBluetooth(String mode) {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (bluetoothAdapter == null)
			return;
		else if (mode.equals("E"))
			bluetoothAdapter.enable();
		else if(mode.equals("D"))
			bluetoothAdapter.disable();
	}
	
	/**
	 * This method returns the Audio setting on the tag
	 * @param nfcData
	 * @return
	 */
	private String getAudioSetting(String nfcData){
		String[] list = nfcData.split("\n");
		return list[1].replace("Audio ", "");
	}
	
	/**
	 * This method returns the display setting on the tag
	 * @param nfcData
	 * @return
	 */
	private String getDisplaySetting(String nfcData){
		String[] list = nfcData.split("\n");
		return list[2].replace("Display ", "");
	}
	
	/**
	 * This method returns the blue tooth setting
	 * @param nfcData
	 * @return
	 */
	private String getBluetoothSetting(String nfcData){
		String[] list = nfcData.split("\n");
		return list[3].replace("BT ", "");
	}
	
	/**
	 * This method returns the SSID of the network
	 * @param nfcData
	 * @return
	 */
	private String getSSID(String nfcData){
		String[] list = nfcData.split("\n");
		return list[4].replace("SSID ", "");
	}
	
	/**
	 * This method returns the password of the network
	 * @param nfcData
	 * @return
	 */
	private String getPassword(String nfcData){
		String[] list = nfcData.split("\n");
		return list[5].replace("PASS ", "");
	}
	
	/**
	 * This methods returns the security of the network
	 * @param nfcData
	 * @return
	 */
	private String getSecurityType(String nfcData){
		String[] list = nfcData.split("\n");
		return list[6].replace("SEC ", "");
	}
	
	/**
	 * This method attempts to connect to the provided network.
	 * The network security type must be WPA-PSK
	 * @param SSID
	 * @param security
	 * @param password
	 */
	private void connectWiFi(String SSID, String security, String password){
		
		WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mWifiManager.setWifiEnabled(true);
		WifiConfiguration wc = new WifiConfiguration();
		wc.SSID = "\"" + SSID + "\"";
		
		if(security.equals("WPA-PSK")){
			wc.preSharedKey = "\"" + password + "\"";
		}
		
		else if(security.equals("WPA-EAP")){
			wc.preSharedKey = "\"" + password + "\"";
			toast("WPA-EAP security not supported");
			return;
		}
		
		else if(security.equals("WEP")){
			 wc.wepKeys[0] = "\"" + password + "\"";
			 toast("WEP security not supported");
			 return;
		}
		
		else if(security.equals("OPEN")){}
		
		else
			return;
		
		mWifiManager.addNetwork(wc);
		List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
		for (WifiConfiguration i : list) {
			if (i.SSID != null && i.SSID.equals("\"" + SSID + "\"")) {
				mWifiManager.disconnect();
				mWifiManager.enableNetwork(i.networkId, true);
				mWifiManager.reconnect();
				break;
			}
		}
	}
	
	/**
	 * Displays a toast to the user
	 * @param message
	 */
	private void toast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
}
