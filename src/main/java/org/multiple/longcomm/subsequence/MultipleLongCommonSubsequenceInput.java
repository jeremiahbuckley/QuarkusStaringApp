package org.multiple.longcomm.subsequence;

import java.util.List;
import java.util.ArrayList;

import org.multiple.longcomm.subsequence.Scoring; 

public class MultipleLongCommonSubsequenceInput {
    List<String[]> nucs = new ArrayList<String[]>();
    Scoring scoring = null;

    public static Boolean VERBOSE = false;
    public static Boolean DEBUG = false;
    public static Boolean TIMED_STATUS = false;
    public static Boolean USE_3D_STRATEGY = false;
    public static Integer SECONDS_CONST_15 = 15 * 1000;
    public static Long NEXT_INTERVAL = 0L;

    public MultipleLongCommonSubsequenceInput() {
        
    }

    public MultipleLongCommonSubsequenceInput(int matchReward, int mismatchPenalty, int indelPenalty, List<String[]> nucs) {
        init(matchReward, mismatchPenalty, indelPenalty, nucs);
    }

    public void init(int matchReward, int mismatchPenalty, int indelPenalty, List<String[]> nucs) {
        this.scoring = new Scoring(matchReward, mismatchPenalty, indelPenalty);
        this.nucs = nucs;
    }

    public Scoring getScoring() {
        return this.scoring;
    }

    public List<String[]> getNucs() {
        return this.nucs;
    }

    public void setVerbose(boolean value) {
        this.VERBOSE = value;
    }

    public boolean getVerbose() {
        return VERBOSE;
    }

    public void setDebug(boolean value) {
        this.DEBUG = value;
    }

    public boolean getDebug() {
        return DEBUG;
    }

    public void setTimedStatus(boolean value) {
        this.TIMED_STATUS = value;
    }

    public boolean getTimedStatus() {
        return TIMED_STATUS;
    }

    public void setUse3DStrategy(boolean value) {
        this.USE_3D_STRATEGY = value;
    }

    public boolean getUse3DStrategy() {
        return this.USE_3D_STRATEGY;
    }

    public void setSecondsConst15(int value) {
        this.SECONDS_CONST_15 = value;
    }

    public int getSecondsConst15() {
        return SECONDS_CONST_15;
    }

    public void setNextInterval(long value) {
        this.NEXT_INTERVAL = value;
    }

    public long getNextInterval() {
        return NEXT_INTERVAL;
    }



}
