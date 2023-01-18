package org.multiple.longcomm.subsequence;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;
import java.lang.IllegalStateException;


public class MultipleLongCommSubseq {
    public static Boolean VERBOSE = false;
    public static Boolean DEBUG = false;
    public static Boolean TIMED_STATUS = false;
    public static Integer SECONDS_CONST_15 = 15 * 1000;
    public static Long NEXT_INTERVAL = 0L;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        NEXT_INTERVAL = startTime + SECONDS_CONST_15;

        String nuc1 = "";
        String nuc2 = "";
        String nuc3 = "";
        try {
            File file = new File(args[0]);
            Scanner sc = new Scanner(file);
            nuc1 = sc.nextLine();
            nuc2 = sc.nextLine();
            nuc3 = sc.nextLine();
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String[] seq1 = nuc1.split("");
        String[] seq2 = nuc2.split("");
        String[] seq3 = nuc3.split("");


        for(int i=1; i < args.length; i++) {
            if (args[i].equals("-v")) {
                VERBOSE = true;
            } else if (args[i].equals("-vv")) {
                VERBOSE = true;
                TIMED_STATUS = true;
            } else if (args[i].equals("-vvv")) {
                VERBOSE = true;
                TIMED_STATUS = true;
                DEBUG = true;
            } else {
                System.out.println(String.format("No match for %d - %s", i, args[i]));
            }

        }

        if (VERBOSE) {
            System.out.println(Arrays.toString(seq1));
            System.out.println(Arrays.toString(seq2));
            System.out.println(Arrays.toString(seq3));
        }

        int matchReward = 1;
        int mismatchPenalty = 0;
        int indelPenalty = 0;
        Scoring scoring = new Scoring(matchReward, mismatchPenalty, indelPenalty);

        List<String[]> nucs = new ArrayList<String[]>();
        nucs.add(seq1);
        nucs.add(seq2);
        nucs.add(seq3);

        List<Object> results = new ArrayList<Object>();
        try {
            results = findCommonSubseq(nucs, scoring);
        } catch (Exception e) {
            System.out.print(e.getStackTrace());
            System.out.print(e.getMessage());
        }
        int resultsScore = (int) results.get(0);
        List<List<String>> resultsCandidates = (List<List<String>>) results.get(1);

        System.out.println();
        System.out.println("result, showing sample of total created (" + resultsCandidates.size() + ")");
        int maxOut = 20;
        for(List<String> cSet : resultsCandidates) {
            if (maxOut > 0) {
                System.out.println(resultsScore);
                for(String candidate : cSet) {
                    System.out.println(candidate);
                }
                System.out.println();
                maxOut -= 1;    
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println(String.format("Time: %d", (endTime-startTime) / 1000));
    }

    public static void setVerbosity(Boolean verbose, Boolean timedStatus, Boolean debug) {
        VERBOSE = verbose;
        TIMED_STATUS = timedStatus;
        DEBUG = debug;
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

    public static void buildWorkingState(List<String[]> nucs, Scoring scoring, List<List<List<Integer>>> scoreKeeper, List<List<List<String>>> incomingDirectionKeeper) {
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
                scoreKeeper.get(i).get(j).set(0, scoring.getIndelPenalty());
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
                scoreKeeper.get(0).get(i).set(j, scoring.getIndelPenalty());
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
                scoreKeeper.get(i).get(0).set(j, scoring.getIndelPenalty());
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

    public static void developWorkspace(List<String[]> nucs, Scoring scoring, List<List<List<Integer>>> scoreKeeper, List<List<List<String>>> incomingDirectionKeeper) {
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

    public static List<Object> findCommonSubseq(List<String[]> nucs, Scoring scoring) throws Exception, IllegalStateException {


        List<List<List<Integer>>> scoreKeeper = new ArrayList<List<List<Integer>>>();
        List<List<List<String>>> incomingDirectionKeeper = new ArrayList<List<List<String>>>();

        buildWorkingState(nucs, scoring, scoreKeeper, incomingDirectionKeeper);

        developWorkspace(nucs, scoring, scoreKeeper, incomingDirectionKeeper);

        Map<List<Integer>, List<List<Integer>>> existingPaths = new HashMap<List<Integer>, List<List<Integer>>>();

        try {
            walkBackwards(scoreKeeper, incomingDirectionKeeper, nucs, existingPaths);
        } catch (Exception e) {
            System.out.print(e.getStackTrace());
            throw e;
        }

        if (DEBUG) {
            for(Map.Entry<List<Integer>, List<List<Integer>>> kvp : existingPaths.entrySet()) {
                System.out.println(kvp.getKey().toString() + kvp.getValue().toString());
            }
        }
        
        List<List<List<Integer>>> candidatePaths = null;
        try {
            candidatePaths = buildPaths(existingPaths, nucs);
        } catch (Exception e) {
            System.out.print(e.getStackTrace());
            throw e;
        }

        List<List<String>> candidates = null;
        try {
            candidates = buildCandidateSets(candidatePaths, nucs);
        } catch (Exception e) {
            System.out.print(e.getStackTrace());
            throw e;
        }

        if (DEBUG) {
            System.out.println("candidates");
            System.out.println(candidates.size());
        }

        int finalScore = scoreKeeper.get(nucs.get(0).length).get(nucs.get(1).length).get(nucs.get(2).length);

        List<Object> results = new ArrayList<Object>();
        results.add(finalScore);
        results.add(candidates);

        return results;
    
    }

    public static void walkBackwards(List<List<List<Integer>>> scoreKeeper, List<List<List<String>>> incomingDirectionKeeper, List<String[]> nucs, Map<List<Integer>, List<List<Integer>>> existingPaths) throws Exception, IllegalStateException {

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

    public static void printSpace(List<List<List<Integer>>> scoreKeeper, List<List<List<String>>> incomingDirectionKeeper, List<String[]> nucs) {
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

        System.out.println(str1);
        System.out.println();
        System.out.println(instr1);

    }

    public static List<List<List<Integer>>> buildPaths(Map<List<Integer>, List<List<Integer>>> existingPaths, List<String[]> nucs) throws IllegalStateException {
        List<Integer> lastNode = createLastNode(nucs);

        List<List<Integer>> nodePathFragments = new ArrayList<List<Integer>>();
        List<List<List<Integer>>> nodePathFragmentsSet = new ArrayList<List<List<Integer>>>();
        nodePathFragmentsSet.add(nodePathFragments);

        Map<List<Integer>, List<List<List<Integer>>>> foundNodePathFragments = new HashMap<List<Integer>, List<List<List<Integer>>>>();
        foundNodePathFragments.put(lastNode, nodePathFragmentsSet);

        Integer starttabs = 0;

        List<Object> newStackFrame = new ArrayList<Object>();
        newStackFrame.add(lastNode);
        newStackFrame.add(nodePathFragmentsSet);
        newStackFrame.add(starttabs);

        Stack<List<Object>> loopStack = new Stack<List<Object>>();
        loopStack.push(newStackFrame);

        while(!loopStack.empty()) {
            List<Object> currentStackFrame = loopStack.pop();

            List<Integer> currentNode = (List<Integer>) currentStackFrame.get(0);
            List<List<List<Integer>>> currentNodePathFragmentsSet = (List<List<List<Integer>>>) currentStackFrame.get(1);
            Integer tabs = (Integer) currentStackFrame.get(2);

            if (TIMED_STATUS && (NEXT_INTERVAL < System.currentTimeMillis())) {
                System.out.println(new String(new char[tabs]).replace("\0", " ") + Long.toString(System.currentTimeMillis()) + " " + Long.toString(System.currentTimeMillis() + SECONDS_CONST_15));
                System.out.println(new String(new char[tabs]).replace("\0", " ") + String.format("timed alert - buld_paths - node: %s", currentNode.toString()));
                NEXT_INTERVAL = System.currentTimeMillis() + SECONDS_CONST_15;
            }
    
            for(List<Integer> nextNode : existingPaths.get(currentNode)) {  
                List<List<List<Integer>>> nextPathNodePaths = new ArrayList<List<List<Integer>>>();

                for(int i = 0; i < nucs.size(); i++) {
                    if (nextNode.get(i) > currentNode.get(i)) {
                        throw new IllegalStateException(String.format("build_paths unexpected current_node %s = next_node %s.", currentNode.toString(), nextNode.toString()));
                    }
                }

                for(List<List<Integer>> fragmentNodePaths : currentNodePathFragmentsSet) {
                    List<List<Integer>> newNodePathFragment = new ArrayList<List<Integer>>();
                    newNodePathFragment.add(currentNode);
                    for(List<Integer> nextNodesList : fragmentNodePaths) {
                        newNodePathFragment.add(nextNodesList);
                    }
                    nextPathNodePaths.add(newNodePathFragment);
                }
    
                if (foundNodePathFragments.containsKey(nextNode)) {
                    for(List<List<Integer>> pathsSet : nextPathNodePaths) {
                        foundNodePathFragments.get(nextNode).add(pathsSet);
                    }
                } else {
                    foundNodePathFragments.put(nextNode, nextPathNodePaths);
                }

                List<Object> nextStackFrame = new ArrayList<Object>();
                nextStackFrame.add(nextNode);
                nextStackFrame.add(nextPathNodePaths);
                tabs += 1;
                nextStackFrame.add(tabs);
                loopStack.push(nextStackFrame);
            }
        }

        List<Integer> firstNode = createFirstNode(nucs);

        if (DEBUG) {
            System.out.println("buildPaths - path node link complete");
            if(foundNodePathFragments.get(firstNode).size() < 100){
                for (List<List<Integer>> ls : foundNodePathFragments.get(firstNode)) {
                    if (ls.size() < 100) {
                        String debugStr = "";
                        for(List<Integer> pn : ls) {
                            if (debugStr.length() > 0) {
                                debugStr += ",";
                            }
                            debugStr += pn.toString();
                        }
                        System.out.println(debugStr);
                        System.out.println();    
                    }
                }
            } else {
                System.out.println("Too many to list: " + foundNodePathFragments.get(firstNode).size());
            }
        }

        return foundNodePathFragments.get(firstNode);
    }


    public static List<List<String>> buildCandidateSets(List<List<List<Integer>>> fullNodePaths, List<String[]> nucs) throws IllegalStateException {


        List<Integer> firstNode = createFirstNode(nucs);
        String skipChar = "-";

        List<List<String>> candidatesSet = new ArrayList<List<String>>();
        long currentCount = 0L;
        long maxCount = fullNodePaths.size() + 1;
        if (maxCount > 10000000) {
            System.out.println("Cutting output in half because it's too large to be useable (" + maxCount + ")");
            maxCount = Math.floorDiv(maxCount, 2);

        }
        for (List<List<Integer>> ls : fullNodePaths) {
            currentCount += 1;
            if (currentCount < maxCount) {
                if (TIMED_STATUS && (NEXT_INTERVAL < System.currentTimeMillis())) {
                    System.out.println(Long.toString(System.currentTimeMillis()) + " " + Long.toString(System.currentTimeMillis() + SECONDS_CONST_15));
                    System.out.println(String.format("timed alert - buld_paths - candidate sets: %d", currentCount));
                    NEXT_INTERVAL = System.currentTimeMillis() + SECONDS_CONST_15;
                }
        
                List<Integer> currentNode = firstNode;
                List<Integer> nextNode = null;
                List<String> candidateSet = new ArrayList<String>();
                for(int i = 0; i < nucs.size(); i++) {
                    candidateSet.add("");
                }
                for(List<Integer> pn : ls) {
                    nextNode = pn;
                    for(int i = 0; i < nucs.size(); i++) {
                        candidateSet.set(i, candidateSet.get(i) + ((nextNode.get(i) > currentNode.get(i)) ? nucs.get(i)[currentNode.get(i)] : skipChar));
                    }
                    currentNode = nextNode;
                }
                candidatesSet.add(candidateSet);    
            }
        }

        return candidatesSet;
    }

}