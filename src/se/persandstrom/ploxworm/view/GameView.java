package se.persandstrom.ploxworm.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import se.persandstrom.ploxworm.Constant;
import se.persandstrom.ploxworm.core.Line;
import se.persandstrom.ploxworm.core.worm.Worm;
import se.persandstrom.ploxworm.core.worm.board.Apple;
import se.persandstrom.ploxworm.core.worm.board.Board;
import se.persandstrom.ploxworm.core.worm.board.Obstacle;

import java.util.ArrayList;

public class GameView extends View {

    protected static final String TAG = "GameView";

    private static final float START_POSITION_MARKER_RADIUS = 5;

    // fixa så färgerna hålls av objekten
    final Paint wormPaint1;
    final Paint wormPaint2;
    final Paint wormPaint3;
    final Paint wormPaint4;
    final Paint wormPaint5;
    final Paint wormPaint6;
    final Paint wormPaint7;
    final Paint wormPaint8;
    final Paint obstaclePaint;
    final Paint applePaint;
    final Paint goldPaint;

    // TODO men usch, gör så alla färger är static
    public static Paint[] wormPaints;

    boolean sizeSet = false;

    Renderer renderer;
    Board board;
    ArrayList<Obstacle> obstacleList;
    ArrayList<Apple> appleList;

    float xNormalizer;
    float yNormalizer;

    ArrayList<Worm> wormList;

    public GameView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        if (Constant.DEBUG) Log.d(TAG, "GameView created");

        wormPaint1 = new Paint();
        wormPaint1.setARGB(255, 200, 200, 200); // GRÅ
        wormPaint2 = new Paint();
        wormPaint2.setARGB(255, 0x00, 0x33, 0xCC); // BLÅ
        wormPaint3 = new Paint();
        wormPaint3.setARGB(255, 0x00, 0xCC, 0x00); // mörk-grön
        wormPaint4 = new Paint();
        wormPaint4.setARGB(255, 0xFF, 0xFF, 0x00); // gul
        wormPaint5 = new Paint();
        wormPaint5.setARGB(255, 0xFF, 0x99, 0x00); // orange
        wormPaint6 = new Paint();
        wormPaint6.setARGB(255, 0x33, 0xFF, 0x00); // ljus-grön
        wormPaint7 = new Paint();
        wormPaint7.setARGB(255, 0xFE, 0xBF, 0xEF); // rosa
        wormPaint8 = new Paint();
        wormPaint8.setARGB(255, 0x99, 0x00, 0x99); // lila

        obstaclePaint = new Paint();
        obstaclePaint.setARGB(255, 0xB0, 0xB0, 0xB0);
        applePaint = new Paint();
        applePaint.setARGB(255, 255, 0, 0);
        goldPaint = new Paint();
        goldPaint.setARGB(255, 0xFF, 0xD0, 0);

        wormPaints = new Paint[]{wormPaint1, wormPaint2, wormPaint3, wormPaint4, wormPaint5, wormPaint6, wormPaint7,
                wormPaint8};
    }

    public void setNewBoard(Board board) {
        renderer = new Renderer();
        this.board = board;
        this.obstacleList = board.getObstacles();
        appleList = board.getApples();
        this.wormList = board.getWormList();
        sizeSet = false;
    }

    @Override
    protected void onDraw(final Canvas canvas) {

        if (board == null) {
            return;
        }

        // calculate the normalizer if it has not already been done:
        if (!sizeSet) {
            float xSize = getWidth();
            float ySize = getHeight();

            xNormalizer = (xSize / board.getXSize());
            yNormalizer = (ySize / board.getYSize());

            sizeSet = true;

            if (xSize == 0) {
                throw new AssertionError("wtf width was 0");
            }
        }

        // draw the worms:
        if (wormList != null) {
            for (Worm worm : wormList) {
                ArrayList<Line> lineList = worm.getLineList();
                final int size = lineList.size();
                for (int i = 1; i < size; i++) {
                    Line line = lineList.get(i);

                    float xStart = (float) line.xStart * xNormalizer;
                    float yStart = (float) line.yStart * yNormalizer;
                    float xStop = (float) line.xStop * xNormalizer;
                    float yStop = (float) line.yStop * yNormalizer;

                    canvas.drawLine(xStart, yStart, xStop, yStop, wormPaints[worm.color]);
                }
                if (size == 0) { // if the worm has not started yet!
                    double xPos = worm.xPos;
                    double yPos = worm.yPos;
                    canvas.drawCircle((float) xPos * xNormalizer, (float) yPos * yNormalizer,
                            START_POSITION_MARKER_RADIUS, wormPaints[worm.color]);
                }
            }

            // draw the obstacles:
            for (Obstacle obstacle : obstacleList) {
                renderer.renderObstacle(canvas, obstaclePaint, obstacle, xNormalizer, yNormalizer);
//				obstacle.onDraw(canvas, xNormalizer, yNormalizer, obstaclePaint);
            }

            // draw the apples:
            for (Apple apple : appleList) {
                if (apple.isGold) {
//					apple.onDraw(canvas, xNormalizer, yNormalizer, goldPaint);
                    renderer.renderApple(canvas, goldPaint, apple, xNormalizer, yNormalizer);
                } else {
//					apple.onDraw(canvas, xNormalizer, yNormalizer, applePaint);
                    renderer.renderApple(canvas, applePaint, apple, xNormalizer, yNormalizer);
                }
            }
        }

        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = widthMeasureSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void render() {
        postInvalidate(0, 0, getWidth(), getHeight());
    }


}
