package se.persandstrom.ploxworm.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import se.persandstrom.ploxworm.core.worm.board.Apple;
import se.persandstrom.ploxworm.core.worm.board.Obstacle;
import se.persandstrom.ploxworm.core.worm.board.ObstacleCircle;
import se.persandstrom.ploxworm.core.worm.board.ObstacleRectangle;

import java.util.HashMap;
import java.util.Map;

public class Renderer {

    Map<Apple, RectF> appleRenderMap;
    Map<Obstacle, RectF> obstacleRenderMap;


    public Renderer() {
        appleRenderMap = new HashMap<Apple, RectF>();
        obstacleRenderMap = new HashMap<Obstacle, RectF>();
    }

    public void renderApple(Canvas canvas, Paint paint, Apple apple, float xNormalizer, float yNormalizer) {
        if (apple.exists) {
            RectF rectF = appleRenderMap.get(apple);
            if (rectF == null) {
                rectF = new RectF((apple.positionX - apple.radius) * xNormalizer, (apple.positionY - apple.radius) *
                        yNormalizer, (apple.positionX + apple.radius) * xNormalizer,
                        (apple.positionY + apple.radius) * yNormalizer);
                appleRenderMap.put(apple, rectF);
            }
            canvas.drawOval(rectF, paint);
        }
    }

    public void renderObstacle(Canvas canvas, Paint paint, Obstacle obstacle, float xNormalizer, float yNormalizer) {
        //lack of polymorphism, but whatever...

        if (obstacle instanceof ObstacleCircle) {
            renderCircle((ObstacleCircle) obstacle, canvas, xNormalizer, yNormalizer, paint);
        }

        if (obstacle instanceof ObstacleRectangle) {
            renderRectangle((ObstacleRectangle) obstacle, canvas, xNormalizer, yNormalizer, paint);
        }
    }

    private void renderCircle(ObstacleCircle obstacle, Canvas canvas, float xNormalizer, float yNormalizer,
                              Paint paint) {

        RectF rectF = obstacleRenderMap.get(obstacle);
        if (rectF == null) {
            rectF = new RectF((obstacle.positionX - obstacle.radius) * xNormalizer,
                    (obstacle.positionY - obstacle.radius) * yNormalizer, (obstacle.positionX + obstacle.radius) *
                    xNormalizer, (obstacle.positionY + obstacle.radius) * yNormalizer);
        }
        canvas.drawOval(rectF, paint);
    }

    private void renderRectangle(ObstacleRectangle obstacle, Canvas canvas, float xNormalizer, float yNormalizer,
                                 Paint paint) {

        canvas.drawRect(obstacle.left * xNormalizer, obstacle.top * yNormalizer, obstacle.right * xNormalizer, obstacle.bottom * yNormalizer, paint);
    }
}
