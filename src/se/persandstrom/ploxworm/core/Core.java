package se.persandstrom.ploxworm.core;

import java.util.ArrayList;

import se.persandstrom.ploxworm.core.worm.HumanWorm;
import se.persandstrom.ploxworm.core.worm.Worm;
import se.persandstrom.ploxworm.core.worm.ai.StupidWorm;
import se.persandstrom.ploxworm.core.worm.board.Board;
import se.persandstrom.ploxworm.core.worm.board.BoardManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class Core extends AccelerometerInterface {

	protected static final String TAG = "Core";

	// point related constants
	private static final int POINTS_APPLE = 50;
	private static final int POINTS_VICTORY = 500;

	// handler constants:
	private static final int HANDLER_REFRESH_SCORE_BOARD = 0;
	private static final int HANDLER_SHOW_DEATH = 1;

	final GameController gameController;

	// the game thread:
	GameThread gameThread;

	public boolean gameStarted = false;
	public boolean gameRunning = false;

	// number of tics since start!
	int counter = 0;

	int level;
	long score = 0;
	int aliveAiCount = 0;

	Board board;
	ArrayList<Worm> wormList;

	// specific game settings:

	// resurrect on death and never spawn gold apple
	private boolean eternalGame = false;

	/**
	 * 
	 * @param gameController
	 * @param gameView
	 *            the view holding the actual game
	 * @param scoreBoard
	 *            the view that shows the score
	 * @param titleView
	 * @param messageView
	 */
	private Core(GameController gameController) {
		this.gameController = gameController;
	}

	public void setLevel(int level) {
		this.level = level;
		Board board = BoardManager.getBoard(this, level);
		this.board = board;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public void startGame(int level, long score) {
		// if (Constant.DEBUG)
		// Log.d(TAG, "startGame: " + level + ", " + score);

		Board board = BoardManager.getBoard(this, level);
		this.level = level;
		this.score = score;
		this.board = board;

		wormList = board.getWormList();
		setAiCounter(wormList);

		gameController.setNewBoard(board);

		gameController.setTitle(board.title);

		new StartCountDownThread().execute();
	}

	public void startGame() {
		// if (Constant.DEBUG)
		// Log.d(TAG, "startGame");

		wormList = board.getWormList();

		setAiCounter(wormList);

		gameController.setNewBoard(board);

		gameController.setTitle(board.title);

		new StartCountDownThread().execute();
	}

	private void setAiCounter(ArrayList<Worm> wormList) {
		for (Worm worm : wormList) {
			if (worm.isAi()) {
				aliveAiCount++;
			}
		}
	}

	/**
	 * Starts the game thread.
	 */
	public void go() {

		gameStarted = true;
		gameRunning = true;
		if (gameThread != null) {
			gameThread.run = false;
		}
		gameThread = new GameThread();
		gameThread.start();

		gameController.setScoreBoard(String.valueOf(score));
	}

	public void stop() {
		counter = 0;
		gameStarted = false;
		gameRunning = false;
	}

	public void death(boolean waitBeforeExiting) {
		stop();

		if (waitBeforeExiting) {
			sendToHandler(HANDLER_SHOW_DEATH);
			new DeathCountDownThread().execute();
		} else {
			end();
		}
	}

	private void end() {
		gameController.end(score);
	}

	/**
	 * Moves the gameworld one tic forward!
	 */
	public void tic() {

		counter++;

		// maybe it would be prettier to call tic on the board? I dunno, think
		// about it.
		// we cant till for ALL worms.... what if computer worm dies?
		int size = wormList.size() - 1;
		for (int i = size; i >= 0; i--) {
			Worm worm = wormList.get(i);
			int makeMove = worm.makeMove();
			if (makeMove == Worm.MOVE_DEATH) {
				if (worm instanceof HumanWorm) {
					death(true);
				} else {
					if (eternalGame) {
						worm.reset();
					} else {
						worm.isAlive = false;
						aliveAiCount--;
					}
				}
			}
		}

		gameController.render();
	}

	public void ateApple() {
		increaseScore(POINTS_APPLE);
	}

	public void victory() {

		if (eternalGame) {

		}

		increaseScore(POINTS_VICTORY);
		stop();

		gameController.victory(score);
	}

	private void increaseScore(int newScore) {
		score += newScore;
		sendToHandler(HANDLER_REFRESH_SCORE_BOARD);
	}

	@Override
	public void acceleration(float xAcc, float yAcc) {
		// xAcc = -event.values[0];
		// yAcc = event.values[1];
		this.xAcc = xAcc;
		this.yAcc = yAcc;
	}

	public void sendToHandler(int what) {
		Message msg = new Message();
		msg.what = what;
		handler.sendMessage(msg);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
//			if (Constant.DEBUG)
//				Log.d(TAG, "handleMessage: " + msg.what);
			switch (msg.what) {
			case HANDLER_REFRESH_SCORE_BOARD:
				gameController.setScoreBoard(String.valueOf(score));
				break;
			case HANDLER_SHOW_DEATH:
				gameController.showMessage();
				gameController.setMessage("Death!");
				break;
			}
		}
	};

	public void backPress() {
		death(false);
	}

	private class GameThread extends Thread {

		long duration;

		public boolean run = true;

		@Override
		public void run() {

			while (gameRunning && run) {
				try {
					Thread.sleep(Math.max(0, 50 - duration));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				long currentTimeMillis = System.currentTimeMillis();
				tic();
				duration = System.currentTimeMillis() - currentTimeMillis;
				// if (Constant.DEBUG) Log.d(TAG, "tic time: " + duration);
			}
		}
	}

	private class StartCountDownThread extends AsyncTask<Void, Integer, Void> {

		private static final int STEP_WAITING_TIME = 300;

		int countSteps = 3;

		public StartCountDownThread() {
		}

		@Override
		protected Void doInBackground(Void... params) {

			do {
				publishProgress(countSteps);
				try {
					Thread.sleep(STEP_WAITING_TIME);
				} catch (InterruptedException quiet) {
				}
				countSteps--;
			} while (countSteps > 0);

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			gameController.setMessage(String.valueOf(values[0]));
		}

		@Override
		protected void onPostExecute(Void result) {
			gameController.hideTitle();
			gameController.hideMessage();

			go();
		}
	}

	private class DeathCountDownThread extends AsyncTask<Void, Integer, Void> {

		private static final int STEP_WAITING_TIME = 750;

		public DeathCountDownThread() {
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
			end();
		}
	}

	/**
	 * Builder class
	 * 
	 * @author Per Sandström
	 * 
	 */
	public static class Builder {

		private Core core;

		private int level = 1;
		private long score = 0;
		boolean makePlayersToAi = false;
		boolean eternalGame = false;

		private boolean isBuilt = false;

		public Builder(GameController gameController) {
			core = new Core(gameController);
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public void setScore(long score) {
			this.score = score;
		}

		public void setEternalGame(boolean eternalGame) {
			this.eternalGame = eternalGame;
		}

		public void setMakePlayersToAi(boolean makePlayersToAi) {
			this.makePlayersToAi = makePlayersToAi;
		}

		public Core build() {
			if (isBuilt) {
				throw new IllegalStateException("cannot build twice");
			} else {
				setupCore();
				isBuilt = true;
				return core;
			}
		}

		public Core start() {
			setupCore();
			core.startGame();
			return core;
		}

		/**
		 * set the variables that has been specified to the core
		 */
		private void setupCore() {
			core.setLevel(level);
			core.setScore(score);
			core.eternalGame = eternalGame;
			if (eternalGame) {
				makePlayersToAi();
				core.board.setAppleEatGoal(Integer.MAX_VALUE / 2);
			}
		}

		/**
		 * Set all human controlled worms to become computers controlled.
		 */
		private void makePlayersToAi() {
			ArrayList<Worm> wormList = core.board.getWormList();
			for (int i = 0; i < wormList.size(); i++) {
				Worm worm = wormList.get(i);
				if (worm instanceof HumanWorm) {
					StupidWorm stupidWorm = new StupidWorm(core, worm.paint,
							worm.xPos, worm.yPos, worm.xForce, worm.yForce);
					stupidWorm.init(worm.board);
					wormList.remove(i);
					wormList.add(i, stupidWorm);
				}
			}
		}
	}
}