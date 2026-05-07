package game.util;

/**
 * Tracks kill count and goal.
 * GameController holds one instance and delegates all score updates here.
 */
public class ScoreManager {

    private int score;
    private final int goal;

    public ScoreManager(int goal) {
        this.score = 0;
        this.goal  = goal;
    }

    /** Record one enemy kill (worth 1 point). */
    public void addKill() { addKill(1); }

    /** Record a kill worth {@code points} (e.g. Hard enemies could be worth more). */
    public void addKill(int points) {
        if (hasReachedGoal()) return;
        score += points;
        if (score > goal) score = goal;
    }

    public boolean hasReachedGoal() { return score >= goal; }

    public void reset() { score = 0; }

    public int    getScore()           { return score; }
    public int    getGoal()            { return goal; }
    public int    getRemaining()       { return Math.max(goal - score, 0); }
    public String formatProgress()     { return score + " / " + goal; }
}