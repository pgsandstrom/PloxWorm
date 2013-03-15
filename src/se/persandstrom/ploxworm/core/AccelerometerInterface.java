package se.persandstrom.ploxworm.core;

import android.hardware.SensorEvent;

public abstract class AccelerometerInterface {

	// Accelerometer variables:
	public float xAcc;
	public float yAcc;

	public abstract void acceleration(float xAcc, float yAcc);
}
