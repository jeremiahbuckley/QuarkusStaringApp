package org.multiple.longcomm.subsequence;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;


// This is an older logic which isn't part of the standard running logic
// It is retained because the iteration logic is waay easier than MultiDimensionalLCS.
// So, for now it is the Source of Truth. If a 3-way comparison is run with both
// this and MultiDimensionalLCS, and their answers are different, this generated
// the right answer
// You can use it by useing the "-n" flag when excecuting the main program
// Without that flag, the program runs MultiDimensionalLCS

public class ThreeDimensionalLCSStrategy extends MultiDimensionalLCSStrategy {


    List<List<List<Integer>>> scoreKeeper = null;
    List<List<List<String>>> incomingDirectionKeeper = null;
    List<String[]> mnucs = null;

    public ThreeDimensionalLCSStrategy(Boolean verbose, Boolean debug, Boolean timed_status, long next_interval, int seconds_const_15)  {
        super(verbose, debug, timed_status, next_interval, seconds_const_15);

    }

    @Override
    public Map<List<Integer>, List<List<Integer>>> scoreSpace(List<String[]> nucs, Scoring scoring) throws Exception, IllegalStateException {

        scoreKeeper = new ArrayList<List<List<Integer>>>();
        incomingDirectionKeeper = new ArrayList<List<List<String>>>();
        mnucs = nucs;

        buildWorkingState(nucs, scoring, scoreKeeper, incomingDirectionKeeper);

        calculateScores(nucs, scoring, scoreKeeper, incomingDirectionKeeper);

        Map<List<Integer>, List<List<Integer>>> existingPaths = new HashMap<List<Integer>, List<List<Integer>>>();

        try {
            walkBackwards(incomingDirectionKeeper, nucs, existingPaths);
        } catch (Exception e) {
            System.out.print(e);
            throw e;
        }

        return existingPaths;
    }

    @Override
    public int getFinalScore() {
        return scoreKeeper.get(mnucs.get(0).length).get(mnucs.get(1).length).get(mnucs.get(2).length);
    }

    public void buildWorkingState(List<String[]> nucs, Scoring scoring, List<List<List<Integer>>> scoreKeeper, List<List<List<String>>> incomingDirectionKeeper) {
        for (int i = 0; i < nucs.get(0).length + 1; i++) {
            scoreKeeper.add(new ArrayList<List<Integer>>());
            incomingDirectionKeeper.add(new ArrayList<List<String>>());
            for (int j = 0; j < nucs.get(1).length + 1; j++) {
                scoreKeeper.get(i).add(new ArrayList<Integer>());
                incomingDirectionKeeper.get(i).add(new ArrayList<String>());
                for (int k = 0; k < nucs.get(2).length + 1; k++) {
                    scoreKeeper.get(i).get(j).add(Integer.MIN_VALUE);
                    incomingDirectionKeeper.get(i).get(j).add("*");
                }
            }
        }

        for (int i = 0; i < nucs.get(0).length + 1; i++) {
            for (int j = 0; j < nucs.get(1).length + 1; j++) {
                scoreKeeper.get(i).get(j).set(0, scoring.getIndelPenalty() * Collections.max(Arrays.asList(i, j)));
                if (i == 0 && j == 0) {
                    incomingDirectionKeeper.get(i).get(j).set(0,"-");
                } else if (i > 0 && j == 0) {
                    incomingDirectionKeeper.get(i).get(j).set(0,"6");
                } else if (i == 0 && j > 0) {
                    incomingDirectionKeeper.get(i).get(j).set(0,"4");
                } else {
                    incomingDirectionKeeper.get(i).get(j).set(0,"246");
                }
            }
        }
        for (int i = 0; i < nucs.get(1).length + 1; i++) {
            for (int j = 0; j < nucs.get(2).length + 1; j++) {
                scoreKeeper.get(0).get(i).set(j, scoring.getIndelPenalty() * Collections.max(Arrays.asList(i, j)));
                if (i == 0 && j == 0) {
                    incomingDirectionKeeper.get(0).get(i).set(j,"-");
                } else if (i > 0 && j == 0) {
                    incomingDirectionKeeper.get(0).get(i).set(j,"4");
                } else if (i == 0 && j > 0) {
                    incomingDirectionKeeper.get(0).get(i).set(j,"7");
                } else {
                    incomingDirectionKeeper.get(0).get(i).set(j,"347");
                }
            }
        }
        for (int i = 0; i < nucs.get(0).length + 1; i++) {
            for (int j = 0; j < nucs.get(2).length + 1; j++) {
                scoreKeeper.get(i).get(0).set(j, scoring.getIndelPenalty() * Collections.max(Arrays.asList(i, j)));
                if (i == 0 && j == 0) {
                    incomingDirectionKeeper.get(i).get(0).set(j,"-");
                } else if (i > 0 && j == 0) {
                    incomingDirectionKeeper.get(i).get(0).set(j,"6");
                } else if (i == 0 && j > 0) {
                    incomingDirectionKeeper.get(i).get(0).set(j,"7");
                } else {
                    incomingDirectionKeeper.get(i).get(0).set(j,"567");
                }
            }
        }

        if (VERBOSE) {
            System.out.println("prep");
            printSpace(scoreKeeper, incomingDirectionKeeper, nucs);
        }
    }

    public void calculateScores(List<String[]> nucs, Scoring scoring, List<List<List<Integer>>> scoreKeeper, List<List<List<String>>> incomingDirectionKeeper) {
        for(int i = 1; i < nucs.get(0).length + 1; i++) {
            for(int j = 1; j < nucs.get(1).length + 1; j++) {
                for(int k = 1; k < nucs.get(2).length + 1; k++) {
                    int score = Collections.max(Arrays.asList(
                        scoreKeeper.get(i-1).get(j-1).get(k-1) + scoring.getMatchVal(Arrays.asList(nucs.get(0)[i-1], nucs.get(1)[j-1], nucs.get(2)[k-1])),
                        scoreKeeper.get(i-1).get(j-1).get(k) + scoring.indelPenalty,
                        scoreKeeper.get(i-1).get(j).get(k-1) + scoring.indelPenalty,
                        scoreKeeper.get(i).get(j-1).get(k-1) + scoring.indelPenalty,
                        scoreKeeper.get(i).get(j).get(k-1) + scoring.indelPenalty,
                        scoreKeeper.get(i).get(j-1).get(k) + scoring.indelPenalty,
                        scoreKeeper.get(i-1).get(j).get(k) + scoring.indelPenalty
                    ));

                    scoreKeeper.get(i).get(j).set(k, score);
                    String dir = "";
                    if (score == scoreKeeper.get(i-1).get(j-1).get(k-1) + scoring.getMatchVal(Arrays.asList(nucs.get(0)[i-1], nucs.get(1)[j-1], nucs.get(2)[k-1]))) {
                        dir = dir.concat("1");
                    }
                    if (score == scoreKeeper.get(i-1).get(j-1).get(k) + scoring.indelPenalty) {
                        dir = dir.concat("2");
                    }
                    if (score == scoreKeeper.get(i).get(j-1).get(k-1) + scoring.indelPenalty) {
                        dir = dir.concat("3");
                    }
                    if (score == scoreKeeper.get(i).get(j-1).get(k) + scoring.indelPenalty) {
                        dir = dir.concat("4");
                    }
                    if (score == scoreKeeper.get(i-1).get(j).get(k-1) + scoring.indelPenalty) {
                        dir = dir.concat("5");
                    }
                    if (score == scoreKeeper.get(i-1).get(j).get(k) + scoring.indelPenalty) {
                        dir = dir.concat("6");
                    }
                    if (score == scoreKeeper.get(i).get(j).get(k-1) + scoring.indelPenalty) {
                        dir = dir.concat("7");
                    }
                    incomingDirectionKeeper.get(i).get(j).set(k, dir);

                }
            }
        }


        if (VERBOSE) {
            System.out.println("complete");
            printSpace(scoreKeeper, incomingDirectionKeeper, nucs);
        }
    }

    public void walkBackwards(List<List<List<String>>> incomingDirectionKeeper, List<String[]> nucs, Map<List<Integer>, List<List<Integer>>> existingPaths) throws Exception, IllegalStateException {

        Stack<List<Object>> loopStack = new Stack<List<Object>>();

        List<Object> newStackFrame = new ArrayList<Object>();        
        List<Integer> lastNode = createLastNode(nucs);
        newStackFrame.add(lastNode);
        newStackFrame.add(Integer.valueOf(0));
        
        loopStack.push(newStackFrame);

        while (!loopStack.empty()) {
            List<Object> currentStackFrame = loopStack.pop();
            List<Integer> currentNode = (List<Integer>) currentStackFrame.get(0);
            Integer tabs = (Integer) currentStackFrame.get(1);

            for(int idx : currentNode) {
                if (idx < 0) {
                    throw new Exception("  " + new String(new char[tabs]).replace("\0", " ") + "invalid value: " + currentNode.toString());    
                }
            }

            if (DEBUG) {
                System.out.println("  " + new String(new char[tabs]).replace("\0"," ") + "cn: " + currentNode.toString());
            }
    
            if (existingPaths.containsKey(currentNode)) {
                List<List<Integer>> followNode = (List<List<Integer>>) currentStackFrame.get(2);
                if (!existingPaths.get(followNode).contains(currentNode)) {
                    existingPaths.get(followNode).add(currentNode);
                }
                continue;
            }

            List<Integer> firstNode = createFirstNode(nucs);
            if (currentNode.equals(firstNode)) {
                if (!existingPaths.containsKey(currentNode)) {
                    existingPaths.put(currentNode, new ArrayList<>());
                }

                try {
                    List<List<Integer>> followNode = (List<List<Integer>>) currentStackFrame.get(2);
                    existingPaths.get(followNode).add(currentNode);
                } catch (IndexOutOfBoundsException e) {
                    int ij = 0;
                    // no-op, this can happen at times
                }
                continue;
            }

            if (TIMED_STATUS && (NEXT_INTERVAL < System.currentTimeMillis())) {
                System.out.println(Long.toString(System.currentTimeMillis()) + " " + Long.toString(System.currentTimeMillis() + NEXT_INTERVAL));
                System.out.println("timed alert - walk_backwards - node: " + currentNode.toString());
                NEXT_INTERVAL = System.currentTimeMillis() + SECONDS_CONST_15;
            }

            String[] directions = incomingDirectionKeeper.get(currentNode.get(0)).get(currentNode.get(1)).get(currentNode.get(2)).split("");

            if (DEBUG) {
                System.out.println(" " + new String(new char[tabs]).replace("\0", " ") + Arrays.toString(directions));
            }

            for (int x = 0; x < directions.length; x++) {
                String dir = directions[x];
                if (DEBUG) {
                    System.out.println(" " + new String(new char[tabs]).replace("\0", " ") + dir);
                }
                List<Integer> prevNode = new ArrayList<Integer>();
                if (dir.equals("1")) {
                    prevNode.add(currentNode.get(0) - 1); prevNode.add(currentNode.get(1) - 1); prevNode.add(currentNode.get(2) - 1);
                } else if (dir.equals("2")) {
                    prevNode.add(currentNode.get(0) - 1); prevNode.add(currentNode.get(1) - 1); prevNode.add(currentNode.get(2)    );
                } else if (dir.equals("3")) {
                    prevNode.add(currentNode.get(0)    ); prevNode.add(currentNode.get(1) - 1); prevNode.add(currentNode.get(2) - 1);
                } else if (dir.equals("4")) {
                    prevNode.add(currentNode.get(0)    ); prevNode.add(currentNode.get(1) - 1); prevNode.add(currentNode.get(2)    );
                } else if (dir.equals("5")) {
                    prevNode.add(currentNode.get(0) - 1); prevNode.add(currentNode.get(1)    ); prevNode.add(currentNode.get(2) - 1);
                } else if (dir.equals("6")) {
                    prevNode.add(currentNode.get(0) - 1); prevNode.add(currentNode.get(1)    ); prevNode.add(currentNode.get(2)    );
                } else if (dir.equals("7")) {
                    prevNode.add(currentNode.get(0)    ); prevNode.add(currentNode.get(1)    ); prevNode.add(currentNode.get(2) - 1);
                } else {
                    throw new IllegalStateException(String.format("Unexpected 000 branch with node: %s", currentNode.toString()));
                }

                List<Object> nextStackFrame = new ArrayList<Object>();

                nextStackFrame.add(prevNode);
                tabs += 1;
                nextStackFrame.add(tabs);
                nextStackFrame.add(currentNode);
                loopStack.push(nextStackFrame);

            }

            if (!existingPaths.containsKey(currentNode)) {
                existingPaths.put(currentNode, new ArrayList<List<Integer>>());
            }

            try {
                List<List<Integer>> followNode = (List<List<Integer>>) currentStackFrame.get(2);
                existingPaths.get(followNode).add(currentNode);
            } catch (IndexOutOfBoundsException e) {
                int ij = 0;
                // no-op, this can happen at times
            }

            if (DEBUG) {
                System.out.println(new String(new char[tabs]).replace("\0", " ") + "findPath - existingPaths");            
                for(Map.Entry<List<Integer>, List<List<Integer>>> kvp : existingPaths.entrySet()) {
                    System.out.println(new String(new char[tabs]).replace("\0", " ") + kvp.getKey().toString());
                    for(List<Integer> n : kvp.getValue()){
                        System.out.println(new String(new char[tabs+1]).replace("\0", " ") + n.toString());
                    }
                }
                System.out.println();
            }
    
        }
        return;
    }

    public void printSpace(List<List<List<Integer>>> scoreKeeper, List<List<List<String>>> incomingDirectionKeeper, List<String[]> nucs) {
        String str1 = "";
        String instr1 = "";

        for(int i = 0; i < nucs.get(0).length + 1; i++) {
            String str2 = "";
            String instr2 = "";

            for(int j = 0; j < nucs.get(1).length + 1; j++) {
                String str3 = Arrays.toString(scoreKeeper.get(i).get(j).toArray());
                String instr3 = Arrays.toString(incomingDirectionKeeper.get(i).get(j).toArray());
                if (str2.length() > 0) {
                    str2 = str2.concat("\n");
                    instr2 = instr2.concat("\n");
                }
                str2 = str2.concat(str3);
                instr2 = instr2.concat(instr3);
            }

            if (str1.length() > 0) {
                str1 = str1.concat("\n\n");
                instr1 = instr1.concat("\n\n");
            }

            str1 = str1.concat(str2);
            instr1 = instr1.concat(instr2);
        }

        System.out.println("scores");
        System.out.println(str1);
        System.out.println();
        System.out.println("directions");
        System.out.println(instr1);
        System.out.println();

    }    

    public static List<Integer> createFirstNode(List<String[]> nucs) {
        List<Integer> node = new ArrayList<Integer>();
        for(int i = 0; i < nucs.size(); i++) {
            node.add(0);
        }
        return node;
    }

    public static List<Integer> createLastNode(List<String[]> nucs) {
        List<Integer> node = new ArrayList<Integer>();
        for(String[] nuc : nucs) {
            node.add(nuc.length);
        }
        return node;
    }
}
