package se.persandstrom.ploxworm.campaign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import se.persandstrom.ploxworm.Constant;
import se.persandstrom.ploxworm.GameActivity;
import se.persandstrom.ploxworm.R;
import se.persandstrom.ploxworm.core.worm.board.BoardManager;

/**
 * The activity that is shown between each level in "campaign mode"
 *
 * @author Per Sandström
 */
public class UnlockLevelsCampaign extends Activity implements View.OnClickListener {

    protected static final String TAG = "UnlockLevelsCampaign";

    public static final String INTENT_EXTRA_START_LEVEL = "start_level";

    private static final int REQUEST_CODE_START_GAME = 2313131;

    private Button startButton;
    private TextView messageView;
    private TextView statusView;
    private TextView scoreView;
    private TextView rewardView;

    int startLevel;

    int currentLevel;
    long currentScore;
    long endScore;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_START_GAME:
                String message = null;
                String status = null;

                currentScore = data.getLongExtra(GameActivity.RESULT_EXTRA_POINTS, 0);
                String score = "Score: " + currentScore;

                switch (resultCode) {
                    case GameActivity.RESULT_DEATH:
                        message = "YOU DIED!";
                        status = "You made it to level " + currentLevel;
                        //startButton.setVisibility(View.INVISIBLE);
                        currentLevel = startLevel;
                        endScore = currentScore;
                        currentScore = 0;
                        startButton.setText("try again");
                        break;
                    case GameActivity.RESULT_VICTORY:
                        currentLevel++;
                        message = "YOU MADE IT!";
                        if (currentLevel > BoardManager.TOTAL_LEVELS) {
                            status = "YOU WON THE GAME!";
                            startButton.setVisibility(View.INVISIBLE);
                        } else {
                            status = "You advanced to level " + currentLevel;
                        }
                        startButton.setText("continue");
                        break;
                }

                String reward = "No reward";

                messageView.setText(message);
                statusView.setText(status);
                scoreView.setText(score);
                rewardView.setText(reward);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Constant.DEBUG) Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_menu);

        messageView = (TextView) findViewById(R.id.message);
        statusView = (TextView) findViewById(R.id.status);
        scoreView = (TextView) findViewById(R.id.score);
        rewardView = (TextView) findViewById(R.id.reward);

        startButton = (Button) findViewById(R.id.start);
        startButton.setOnClickListener(this);

        Intent intent = getIntent();

        int startLevel = intent.getIntExtra(INTENT_EXTRA_START_LEVEL, 1);

//		if (levelStartString != null) {
//			startLevel = Integer.valueOf(levelStartString);
//		} else {
//			startLevel = 1;
//		}

        currentLevel = startLevel;

        currentScore = 0;

        startLevel(currentLevel, currentScore);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                startLevel(currentLevel, currentScore);
                break;
        }
    }

    private void startLevel(int level, long score) {
        Intent startLevelIntent = new Intent(this, GameActivity.class);
        startLevelIntent.putExtra(GameActivity.INTENT_EXTRA_START_LEVEL, level);
        startLevelIntent.putExtra(GameActivity.INTENT_EXTRA_POINTS, score);
        startActivityForResult(startLevelIntent, REQUEST_CODE_START_GAME);
    }
}
