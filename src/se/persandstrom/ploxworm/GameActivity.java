package se.persandstrom.ploxworm;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.GameController;
import se.persandstrom.ploxworm.view.GameView;

public class GameActivity extends Activity {

	protected static final String TAG = "GameActivity";

	// intent related constants:
	public static final String INTENT_EXTRA_START_LEVEL = "start_level";
	public static final String INTENT_EXTRA_POINTS = "points";

	// result related constants:
	public static final int RESULT_VICTORY = 1;
	public static final int RESULT_DEATH = 2;
	public static final String RESULT_EXTRA_POINTS = "points";

	Core gameCore;

	MenuItem menuPause;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (Constant.DEBUG)
			Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		// prevent screen from sleeping:
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.game_board);

		GameView gameView = (GameView) findViewById(R.id.gameview);
		TextView scoreBoard = (TextView) findViewById(R.id.score_board);
		TextView titleView = (TextView) findViewById(R.id.title);
		TextView messageView = (TextView) findViewById(R.id.message);
		if (Constant.DEBUG)
			Log.d(TAG, "messageView: " + messageView);

		int level = getIntent().getIntExtra(INTENT_EXTRA_START_LEVEL, 1);
		long points = getIntent().getLongExtra(INTENT_EXTRA_POINTS, 0);

        AndroidGameController gameController = new AndroidGameController(this, gameView, scoreBoard, titleView, messageView);
        Core.Builder builder = new Core.Builder(gameController);

		builder.setLevel(level);
		builder.setScore(points);

		// TODO this is kind of weird since the refactoring

		gameCore = builder.build();
		gameCore.startGame();

		initAccelerometer(this, gameController);

		// gameCore = new Core(this, gameView, scoreBoard, titleView,
		// messageView);
		//
		// gameCore.startGame(level, points);
	}

	private void initAccelerometer(Activity activity,
			AccelerometerInterface accelerometerInterface) {
		if (AccelerometerManager.isSupported(activity)) {
			AccelerometerManager.startListening(activity,
					accelerometerInterface);
		} else {
			if (Constant.DEBUG)
				Log.d(TAG, " ACCELEROMETER NOT SUPPORTED :(:(:(");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// if (gameCore.gameStarted && !gameCore.gameRunning) {
		// gameCore.gameRunning = true;
		// gameCore.go();
		// }
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (gameCore.gameStarted && gameCore.gameRunning) {
			gameCore.gameRunning = false;
		}
	}

	@Override
	protected void onDestroy() {
		AccelerometerManager.stopListening();
		gameCore.stop();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		gameCore.backPress();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		if (Constant.DEBUG)
			Log.d(TAG, "onCreateOptionsMenu");

		menuPause = menu.add(0, 0, 0, "start");
		menuPause.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				if (gameCore.gameRunning) {
					gameCore.gameRunning = false;
				} else {
					gameCore.gameRunning = true;
					gameCore.go();
				}

				return true;
			}
		});

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (Constant.DEBUG)
			Log.d(TAG, "onPrepareOptionsMenu");
		setAllButtonState();
		return true;
	}

	private void setAllButtonState() {
		if (gameCore.gameRunning) {
			menuPause.setTitle("pause");
			menuPause.setIcon(android.R.drawable.ic_media_pause);
		} else {
			menuPause.setTitle("start");
			menuPause.setIcon(android.R.drawable.ic_media_play);
		}
	}

}