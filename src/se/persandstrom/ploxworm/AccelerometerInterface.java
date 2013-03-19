package se.persandstrom.ploxworm;

public class AccelerometerInterface {

	// Accelerometer variables:
	protected float xAcc;
	protected float yAcc;

    public void acceleration(float xAcc, float yAcc) {
        this.xAcc = xAcc;
        this.yAcc = yAcc;
    }
}
