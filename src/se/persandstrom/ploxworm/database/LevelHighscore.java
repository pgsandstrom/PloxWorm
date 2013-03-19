package se.persandstrom.ploxworm.database;

public class LevelHighscore {

	public final long level;
	public final long localHighscore;
	public final long globalHighscore;
	
	public LevelHighscore(long level, long localHighscore, long globalHighscore) {
		this.level = level;
		this.localHighscore = localHighscore;
		this.globalHighscore = globalHighscore;
	}
	
}
