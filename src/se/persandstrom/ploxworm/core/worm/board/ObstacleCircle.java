package se.persandstrom.ploxworm.core.worm.board;

import se.persandstrom.ploxworm.core.Line;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;


public class ObstacleCircle implements Obstacle {

	protected static final String TAG = "ObstacleCircle";

	public final float positionX;
	public final float positionY;

	public final float radius;

	private RectF rectF;

	public ObstacleCircle(float positionX, float positionY, float radius) {
		this.positionX = positionX;
		this.positionY = positionY;

		this.radius = radius;
	}

	@Override
	public boolean isCollide(Line line) {
		float xStop = line.xStop;
		float yStop = line.yStop;

		float distance = (float) Math.sqrt(Math.pow(positionX - xStop, 2) + Math.pow(positionY - yStop, 2));
		//if (Constant.DEBUG) Log.d(TAG, "distance:" + distance);
		if (distance < radius) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onDraw(Canvas canvas, float xNormalizer, float yNormalizer, Paint paint) {
		if (rectF == null) {
			rectF = new RectF((positionX - radius) * xNormalizer, (positionY - radius) * yNormalizer,
					(positionX + radius) * xNormalizer, (positionY + radius) * yNormalizer);
		}
		canvas.drawOval(rectF, paint);
	}
}
