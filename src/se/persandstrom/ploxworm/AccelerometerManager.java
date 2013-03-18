package se.persandstrom.ploxworm;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import se.persandstrom.ploxworm.core.AccelerometerInterface;

import java.util.List;

public class AccelerometerManager {

	private static Sensor sensor;
	private static SensorManager sensorManager;
	public static AccelerometerInterface accelerometerInterface;

	/** indicates whether or not Accelerometer Sensor is supported */
	private static Boolean supported;
	/** indicates whether or not Accelerometer Sensor is running */
	private static boolean running = false;

	/**
	 * Returns true if the manager is listening to orientation changes
	 */
	public static boolean isListening() {
		return running;
	}

	/**
	 * Unregisters listeners
	 */
	public static void stopListening() {
		running = false;
		try {
			if (sensorManager != null && sensorEventListener != null) {
				sensorManager.unregisterListener(sensorEventListener);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Returns true if at least one Accelerometer sensor is available
	 */
	public static boolean isSupported(Activity activity) {
		if (supported == null) {
			sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
			List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
			supported = new Boolean(sensors.size() > 0);
		}
		return supported;
	}

	/**
	 * Registers a listener and start listening
	 * 
	 * @param testArea
	 *            callback for accelerometer events
	 */
	public static void startListening(Activity activity, AccelerometerInterface accelerometerInterface) {
		sensorManager = (SensorManager) activity.getBaseContext().getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			sensor = sensors.get(0);
			running = sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_GAME);
			AccelerometerManager.accelerometerInterface = accelerometerInterface;
		}
	}

	/**
	 * The listener that listen to events from the accelerometer listener
	 */
	private static SensorEventListener sensorEventListener = new SensorEventListener() {

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
			float xAcc = -event.values[0];
			float yAcc = event.values[1];
			accelerometerInterface.acceleration(xAcc, yAcc);
		}

	};

}