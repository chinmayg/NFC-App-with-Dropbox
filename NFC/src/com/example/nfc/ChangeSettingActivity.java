package com.example.nfc;

import java.util.ArrayList;
import java.util.List;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ChangeSettingActivity extends Activity {

	//Reference to the WiFi manager
	private WifiManager mWifiManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_setting);
		
		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		if(mWifiManager == null)
			Toast.makeText(this, "No Wifi Manager found!", Toast.LENGTH_SHORT).show();
		else {
			List<WifiConfiguration> connections = mWifiManager.getConfiguredNetworks();
			ArrayList<String> connectionInfo = new ArrayList<String>();
			
			for(int i = 0; i < connections.size(); i++)
				connectionInfo.add(connections.get(i).SSID.replace("\"", ""));
			
			ListView listView = (ListView) findViewById(R.id.listView1);
			
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
					this,android.R.layout.simple_list_item_1, connectionInfo);
			
			listView.setAdapter(arrayAdapter);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_setting, menu);
		return true;
	}

}
