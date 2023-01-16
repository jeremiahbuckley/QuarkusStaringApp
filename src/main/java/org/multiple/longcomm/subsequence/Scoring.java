package org.multiple.longcomm.subsequence;

import java.util.List;

class Scoring {
    int matchReward;
    int mismatchPenalty;
    int indelPenalty;

    Scoring(int matchReward, int mismatchPenalty, int indelPenalty) {
        this.matchReward = matchReward;
        this.mismatchPenalty = mismatchPenalty;
        this.indelPenalty = indelPenalty;
    }

    public int getMatchVal(List<String> proteinList) {
        boolean match = true;
        if (proteinList.size() > 1) {
            String prevProtein = proteinList.get(0);
            for (int i = 1; i < proteinList.size(); i++) {
                String nextProtein = proteinList.get(i);
                match = match && (prevProtein.equals(nextProtein));
            }
        }

        return match ? this.matchReward : this.indelPenalty;
    }

    public int getMatchReward() {
        return this.matchReward;
    }

    public int getMismatchPenalty() {
        return this.mismatchPenalty;
    }

    public int getIndelPenalty() {
        return this.indelPenalty;
    }
}
