package se.persandstrom.ploxworm.core.worm;

import java.util.Random;

import se.persandstrom.ploxworm.Constant;
import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.Line;
import android.graphics.Paint;
import android.util.Log;

public abstract class ComputerWorm extends Worm {

	protected static final String TAG = "ComputerWorm";

	protected static final float[] AI_APPLE_CHECKS = new float[] { 10, 30, 60, 100, 133, 166, 200, 233, 266, 300 }; // can
																													// be
																													// long,
																													// time
																													// to
																													// check
																													// is
																													// trivial
	protected static final float[] AI_CRASH_CHECKS = new float[] { 10, 30, 60, 100 }; // cant be long... not as trivial

	protected static final int DIRECTION_STRAIGHT = 0;
	protected static final int DIRECTION_EITHER = 0; // this is a direction for currentCrisisDecision in case it dont
														// care
	protected static final int DIRECTION_LEFT = 1;
	protected static final int DIRECTION_RIGHT = 2;

	protected static final int CRASH_NEITHER = 0;
	protected static final int CRASH_STRAIGHT = 1;
	protected static final int CRASH_LEFT = 2;
	protected static final int CRASH_RIGHT = 3;

	protected int currentDecision;
	protected int currentCrisisDecision;

	protected Random random;

	public ComputerWorm(Core core, Paint paint, float startPositionX, float startPositionY, float startSpeedX,
			float startSpeedY) {
		super(core, paint, startPositionX, startPositionY, startSpeedX, startSpeedY);
		random = new Random();
		if (Constant.DEBUG) Log.d(TAG, "ComputerWorm created");
	}

	protected int isHeadingForCrash() {

		final float currentXforce = xForce;
		final float currentYforce = yForce;

		float wormDegree;

		wormDegree = (float) Math.atan2(currentYforce, currentXforce);
		if (wormDegree < 0) {
			wormDegree += Math.PI * 2;
		}

		wormDegree -= MAX_DEGREE_TURN;
		wormDegree -= MAX_DEGREE_TURN;

		final float leftXforce = (float) android.util.FloatMath.cos(wormDegree);
		final float leftYforce = (float) android.util.FloatMath.sin(wormDegree);

		wormDegree = (float) Math.atan2(currentYforce, currentXforce);
		if (wormDegree < 0) {
			wormDegree += Math.PI * 2;
		}

		wormDegree += MAX_DEGREE_TURN;
		wormDegree += MAX_DEGREE_TURN;

		final float rightXforce = android.util.FloatMath.cos(wormDegree);
		final float rightYforce = android.util.FloatMath.sin(wormDegree);

		Line line;
		for (float length : AI_CRASH_CHECKS) {
			// if (Constant.DEBUG) Log.d(TAG, "length: " + length);
			boolean crashStraight = false;
			boolean crashLeft = false;
			boolean crashRight = false;

			xForce = rightXforce;
			yForce = rightYforce;
			line = makeLine(length, false);
			if (isLineColliding(line)) {
				// if (Constant.DEBUG) Log.d(TAG, "crashRight");
				crashRight = true;
			}

			xForce = leftXforce;
			yForce = leftYforce;
			line = makeLine(length, false);
			if (isLineColliding(line)) {
				// if (Constant.DEBUG) Log.d(TAG, "crashLeft");
				crashLeft = true;
			}

			xForce = currentXforce;
			yForce = currentYforce;
			line = makeLine(length, false);
			if (isLineColliding(line)) {
				// if (Constant.DEBUG) Log.d(TAG, "crashStraight");
				crashStraight = true;
			}

			if (crashLeft && !crashRight) {
				xForce = currentXforce;
				yForce = currentYforce;
				return CRASH_LEFT;
			}
			if (!crashLeft && crashRight) {
				xForce = currentXforce;
				yForce = currentYforce;
				return CRASH_RIGHT;
			}
			if (crashStraight) {
				xForce = currentXforce;
				yForce = currentYforce;
				return CRASH_STRAIGHT;
			}
			if (crashLeft && crashRight) {
				xForce = currentXforce;
				yForce = currentYforce;
				return CRASH_STRAIGHT;
			}
		}

		xForce = currentXforce;
		yForce = currentYforce;

		return CRASH_NEITHER;
	}

	protected boolean isHeadingForApple() {
		for (float distance : AI_APPLE_CHECKS) {
			Line line = makeLine(distance, false);
			if (checkAppleEating(line) != null) {
				// if (Constant.DEBUG) Log.d(TAG, "is heading for apple!");
				return true;
			}
		}
		return false;
	}

	protected void turnLeft(boolean crisis) {
		// if (Constant.DEBUG) Log.d(TAG, "turnLeft: " + crisis);
		float wormDegree = (float) Math.atan2(yForce, xForce);
		if (wormDegree < 0) {
			wormDegree += Math.PI * 2;
		}

		wormDegree -= MAX_DEGREE_TURN;

		xForce = android.util.FloatMath.cos(wormDegree);
		yForce = android.util.FloatMath.sin(wormDegree);
		currentDecision = DIRECTION_LEFT;
		if (crisis) {
			currentCrisisDecision = DIRECTION_LEFT;
		} else {
			currentCrisisDecision = DIRECTION_EITHER;
		}
	}

	protected void turnRight(boolean crisis) {
		// if (Constant.DEBUG) Log.d(TAG, "turnRight: " + crisis);
		float wormDegree = (float) Math.atan2(yForce, xForce);
		if (wormDegree < 0) {
			wormDegree += Math.PI * 2;
		}

		wormDegree += MAX_DEGREE_TURN;

		xForce = android.util.FloatMath.cos(wormDegree);
		yForce = android.util.FloatMath.sin(wormDegree);
		currentDecision = DIRECTION_RIGHT;
		if (crisis) {
			currentCrisisDecision = DIRECTION_RIGHT;
		} else {
			currentCrisisDecision = DIRECTION_EITHER;
		}
	}

	protected void goStraight() {
		// if (Constant.DEBUG) Log.d(TAG, "goStraight");
		currentDecision = DIRECTION_STRAIGHT;
		currentCrisisDecision = DIRECTION_EITHER;
	}

}