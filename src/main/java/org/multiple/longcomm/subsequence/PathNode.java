package org.multiple.longcomm.subsequence;

import java.util.Arrays;
import java.util.List;

public class PathNode {

    private final int[] values;

    public PathNode(int[] values) {
        this.values = values;
    }

    public int[] getValues() {
        return values;
    }

    @Override
    public boolean equals(Object another) {
        if (another == this) {
            return true;
        }
        if (another == null) {
            return false;
        }
        if (another.getClass() != this.getClass()) {
            return false;
        }
        PathNode key = (PathNode) another;
        return Arrays.equals(this.values, key.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }

    @Override
    public String toString() {
        return Arrays.toString(this.values);
    }
}