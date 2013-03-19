package se.persandstrom.ploxworm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import se.persandstrom.ploxworm.campaign.PickGameActivity;
import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.GameController;
import se.persandstrom.ploxworm.view.GameView;

/**
 * The main screen
 * @author Per Sandström
 *
 */
public class HomeScreenActivity extends Activity implements View.OnClickListener {

	Core gameCore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.start_menu);

		findViewById(R.id.singleplayer).setOnClickListener(this);

		setupBackgroundGame();
	}

	private void setupBackgroundGame() {
		GameView gameView = (GameView) findViewById(R.id.gameview);
		Core.Builder builder = new Core.Builder(new AndroidGameController(this, gameView, null, null, null));
		builder.setLevel(7);
		builder.setMakePlayersToAi(true);
		builder.setEternalGame(true);
		gameCore = builder.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!gameCore.gameRunning) {
			gameCore.gameRunning = true;
			gameCore.go();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (gameCore.gameRunning) {
			gameCore.gameRunning = false;
		}
	}

	@Override
	protected void onDestroy() {
		gameCore.stop();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		gameCore.backPress();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.singleplayer:
			Intent singlePlayerIntent = new Intent(this, PickGameActivity.class);
			startActivity(singlePlayerIntent);
			break;
		}
	}
}
