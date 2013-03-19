package se.persandstrom.ploxworm;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import se.persandstrom.ploxworm.core.GameController;
import se.persandstrom.ploxworm.core.worm.Worm;
import se.persandstrom.ploxworm.core.worm.board.Board;
import se.persandstrom.ploxworm.view.GameView;

/**
 * User: pesandst
 * Date: 2013-03-18
 * Time: 14:20
 */
public class AndroidGameController extends AccelerometerInterface implements GameController {

    static final String TAG = "GameController";

    // handler constants:
    private static final int HANDLER_REFRESH_SCORE_BOARD = 0;
    private static final int HANDLER_SHOW_MESSAGE = 1;
    private static final int HANDLER_HIDE_TITLE = 2;
    private static final int HANDLER_HIDE_MESSAGE = 3;
    private static final int HANDLER_SET_SCORE = 4;

    private final Activity activity;

    final GameView gameView;

    final TextView scoreBoard;
    final TextView titleView;
    final TextView messageView;

    public AndroidGameController(Activity activity, GameView gameView, TextView scoreBoard, TextView titleView,
                          TextView messageView) {
        this.activity = activity;
        this.gameView = gameView;
        this.scoreBoard = scoreBoard;
        this.titleView = titleView;
        this.messageView = messageView;
    }

    public float getXacc(Worm player) {
        return xAcc;
    }

    public float getYacc(Worm player) {
        return yAcc;
    }

    public void end(long score) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(GameActivity.RESULT_EXTRA_POINTS, score);
        activity.setResult(GameActivity.RESULT_DEATH, resultIntent);
        activity.finish();
    }

    public void victory(long score) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(GameActivity.RESULT_EXTRA_POINTS, score);
        activity.setResult(GameActivity.RESULT_VICTORY, resultIntent);
        activity.finish();
    }

    public void setScoreBoard(String score) {
        if (isUiThread()) {
            if (scoreBoard != null) {
                scoreBoard.setText(score);
            }
        } else {
            sendToHandler(HANDLER_SET_SCORE, score);
        }
    }

    public void setTitle(String title) {
        Log.d(TAG, "setTitle:" + title + ", " + isUiThread() + ", " + titleView);
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    public void setMessage(String message) {

        //if ui thread
        if (isUiThread()) {
            if (messageView != null) {
                messageView.setText(message);
            }
        } else {
            sendToHandler(HANDLER_SHOW_MESSAGE, message);
        }
    }

    public void hideTitle() {
        if (isUiThread()) {
            if (titleView != null) {
                titleView.setVisibility(View.INVISIBLE);
            }
        } else {
            sendToHandler(HANDLER_HIDE_TITLE);
        }
    }

    public void hideMessage() {
        if (isUiThread()) {
            if (messageView != null) {
                messageView.setVisibility(View.INVISIBLE);
            }
        } else {
            sendToHandler(HANDLER_HIDE_MESSAGE);
        }
    }

    public void showMessage() {
        if (messageView != null) {
            messageView.setVisibility(View.VISIBLE);
        }
    }

    public void setNewBoard(Board board) {
        gameView.setNewBoard(board);
    }

    public void render() {
        gameView.render();
    }

    private void sendToHandler(int what) {
        Message msg = new Message();
        msg.what = what;
        handler.sendMessage(msg);
    }

    private void sendToHandler(int what, int extra) {
        Message msg = new Message();
        msg.what = what;
        msg.arg1 = extra;
        handler.sendMessage(msg);
    }

    private void sendToHandler(int what, String extra) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = extra;
        handler.sendMessage(msg);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Constant.DEBUG) Log.d(TAG, "handleMessage: " + msg.what);
            switch (msg.what) {
                case HANDLER_REFRESH_SCORE_BOARD:
                    setScoreBoard(String.valueOf(msg.arg1));
                    break;
                case HANDLER_SHOW_MESSAGE:
                    String message = (String) msg.obj;
                    showMessage();
                    setMessage(message);
                case HANDLER_HIDE_TITLE:
                    hideTitle();
                    break;
                case HANDLER_HIDE_MESSAGE:
                    hideMessage();
                    break;
                case HANDLER_SET_SCORE:
                    String score = (String) msg.obj;
                    setScoreBoard(score);
            }
        }
    };



    private class DeathCountDownThread extends AsyncTask<Void, Integer, Void> {

        private static final int STEP_WAITING_TIME = 750;

        private final long score;

        public DeathCountDownThread(long score) {
            this.score = score;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Thread.sleep(STEP_WAITING_TIME);
            } catch (InterruptedException quiet) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            end(score);
        }
    }

    public void endWithWait(long score) {
        sendToHandler(HANDLER_SHOW_MESSAGE, "Death!");  //XXX magical string
        new DeathCountDownThread(score).execute();
    }

    public void updateScore(long score) {
        //TODO this is only an int now, beware for overflow!
        sendToHandler(HANDLER_REFRESH_SCORE_BOARD, (int) score);
    }

    private boolean isUiThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
