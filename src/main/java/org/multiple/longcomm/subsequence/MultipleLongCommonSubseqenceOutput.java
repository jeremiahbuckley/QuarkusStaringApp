package org.multiple.longcomm.subsequence;

import java.util.List;

public class MultipleLongCommonSubseqenceOutput {
    public int score;
    public long timeElapsed;
    public List<List<String>> matchSets;

    public MultipleLongCommonSubseqenceOutput(int score, long timeElapsed, List<List<String>> matchSets) {
        this.score = score;
        this.timeElapsed = timeElapsed;
        this.matchSets = matchSets;
    }

    public int getScore() {
        return score;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public List<List<String>> getMatchSets() {
        return matchSets;
    }
    
}
