package se.persandstrom.ploxworm.mp.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.View;
import se.persandstrom.ploxworm.R;

public class BlueToothSetup extends Activity implements View.OnClickListener{
	
	private BluetoothAdapter mBtAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.multiplayer_setup);
		
		// Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		
		}
	}
}
