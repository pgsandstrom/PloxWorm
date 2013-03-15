package se.persandstrom.ploxworm.core;

import se.persandstrom.ploxworm.core.worm.board.Board;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

public class GameController {

	private final Activity activity;

	final GameView gameView;

	final TextView scoreBoard;
	final TextView titleView;
	final TextView messageView;

	public GameController(Activity activity, GameView gameView,
			TextView scoreBoard, TextView titleView, TextView messageView) {
		this.activity = activity;
		this.gameView = gameView;
		this.scoreBoard = scoreBoard;
		this.titleView = titleView;
		this.messageView = messageView;
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

	}

	public void setTitle(String title) {
		if (titleView != null) {
			titleView.setText(title);
		}
	}

	public void setMessage(String message) {

	}

	public void hideTitle() {
		if (titleView != null) {
			titleView.setVisibility(View.INVISIBLE);
		}
	}

	public void hideMessage() {
		if (messageView != null) {
			messageView.setVisibility(View.INVISIBLE);
		}
	}

	public void showMessage() {
		messageView.setVisibility(View.VISIBLE);
	}

	public void setNewBoard(Board board) {
		gameView.setNewBoard(board);
	}

	public void render() {
		gameView.render();
	}

}
