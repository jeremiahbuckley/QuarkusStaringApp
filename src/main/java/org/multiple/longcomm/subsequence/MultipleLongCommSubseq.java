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
    
// jbtest
Map<List<Integer>, String> testMap = new HashMap<List<Integer>, String>();
List<Integer> key1 = new ArrayList<Integer>(Arrays.asList(Integer.valueOf(8), Integer.valueOf(3), Integer.valueOf(100)));
testMap.put(key1, "foo");
List<Integer> key2 = new ArrayList<Integer>(Arrays.asList(Integer.valueOf(8), Integer.valueOf(3), Integer.valueOf(100)));
testMap.put(key2, "bar");
List<Integer> key3 = new ArrayList<Integer>(Arrays.asList(Integer.valueOf(3), Integer.valueOf(8), Integer.valueOf(100)));
testMap.put(key3, "pop");
List<Integer> key4 = new ArrayList<Integer>(Arrays.asList(Integer.valueOf(3), Integer.valueOf(18), Integer.valueOf(100)));
testMap.put(key4, "fizz");

for(Map.Entry<List<Integer>, String> kvp : testMap.entrySet()) {
    System.out.print(kvp.getKey().toString());
    System.out.println(kvp.getValue());
}

// end jb test


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

        Map<PathNode, List<PathNode>> existingPaths = new HashMap<PathNode, List<PathNode>>();

        walkBackwards(scoreKeeper, incomingDirectionKeeper, nucs, existingPaths);

        if (DEBUG) {
            for(Map.Entry<PathNode, List<PathNode>> kvp : existingPaths.entrySet()) {
                System.out.println(kvp.getKey().toString() + kvp.getValue().toString());
            }
        }
        
        List<List<PathNode>> candidatePaths = buildPaths(existingPaths, nucs);

        List<List<String>> candidates = buildCandidateSets(candidatePaths, nucs);

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

    public static void walkBackwards(List<List<List<Integer>>> scoreKeeper, List<List<List<String>>> incomingDirectionKeeper, List<String[]> nucs, Map<PathNode, List<PathNode>> existingPaths) throws Exception, IllegalStateException {

        Stack<List<Object>> loopStack = new Stack<List<Object>>();

        List<Object> newStackFrame = new ArrayList<Object>();        
        int[] initNodeIdx = new int[3];
        initNodeIdx[0] = nucs.get(0).length; initNodeIdx[1] = nucs.get(1).length; initNodeIdx[2] = nucs.get(2).length;
        newStackFrame.add(initNodeIdx);
        newStackFrame.add(Integer.valueOf(0));
        
        loopStack.push(newStackFrame);

        while (!loopStack.empty()) {
            List<Object> currentStackFrame = loopStack.pop();
            int[] loopNodeIdx = (int[]) currentStackFrame.get(0);
            Integer tabs = (Integer) currentStackFrame.get(1);

            if (loopNodeIdx[0] < 0 || loopNodeIdx[1] < 0 || loopNodeIdx[2] < 0) {
                throw new Exception("  " + new String(new char[tabs]).replace("\0", " ") + "invalid value: " + Arrays.toString(loopNodeIdx));    
            }

            int[] nIdx = new int[3];
            nIdx[0] = loopNodeIdx[0]; nIdx[1] = loopNodeIdx[1]; nIdx[2] = loopNodeIdx[2];
            PathNode myNode = new PathNode(nIdx);

            if (DEBUG) {
                System.out.println("  " + new String(new char[tabs]).replace("\0"," ") + "cn: " + Arrays.toString(nIdx));
            }
    
            if (existingPaths.containsKey(myNode)) {
                PathNode followNode = (PathNode) currentStackFrame.get(2);
                if (!existingPaths.get(followNode).contains(myNode)) {
                    existingPaths.get(followNode).add(myNode);
                }
                continue;
            }

            int[] zeroIdx = new int[3];
            zeroIdx[0] = 0; zeroIdx[1] = 0; zeroIdx[2] = 0;
            if (myNode.equals(new PathNode(zeroIdx))) {
                if (!existingPaths.containsKey(myNode)) {
                    existingPaths.put(myNode, new ArrayList<>());
                }

                try {
                    PathNode followNode = (PathNode) currentStackFrame.get(2);
                    existingPaths.get(followNode).add(myNode);
                } catch (IndexOutOfBoundsException e) {
                    int ij = 0;
                    // no-op, this can happen at times
                }
                continue;
            }

            if (TIMED_STATUS && (NEXT_INTERVAL < System.currentTimeMillis())) {
                System.out.println(Long.toString(System.currentTimeMillis()) + " " + Long.toString(System.currentTimeMillis() + NEXT_INTERVAL));
                System.out.println("timed alert - walk_backwards - node: " + myNode.toString());
                NEXT_INTERVAL = System.currentTimeMillis() + SECONDS_CONST_15;
            }

            String[] directions = incomingDirectionKeeper.get(nIdx[0]).get(nIdx[1]).get(nIdx[2]).split("");

            if (DEBUG) {
                System.out.println(" " + new String(new char[tabs]).replace("\0", " ") + Arrays.toString(directions));
            }

            for (int x = 0; x < directions.length; x++) {
                String dir = directions[x];
                if (DEBUG) {
                    System.out.println(" " + new String(new char[tabs]).replace("\0", " ") + dir);
                }
                int[] prevNodeIdx = new int[3];
                if (dir.equals("1")) {
                    prevNodeIdx[0] = nIdx[0]-1; prevNodeIdx[1] = nIdx[1]-1; prevNodeIdx[2] = nIdx[2]-1;
                } else if (dir.equals("2")) {
                    prevNodeIdx[0] = nIdx[0]-1; prevNodeIdx[1] = nIdx[1]-1; prevNodeIdx[2] = nIdx[2];
                } else if (dir.equals("3")) {
                    prevNodeIdx[0] = nIdx[0]; prevNodeIdx[1] = nIdx[1]-1; prevNodeIdx[2] = nIdx[2]-1;
                } else if (dir.equals("4")) {
                    prevNodeIdx[0] = nIdx[0]; prevNodeIdx[1] = nIdx[1]-1; prevNodeIdx[2] = nIdx[2];
                } else if (dir.equals("5")) {
                    prevNodeIdx[0] = nIdx[0]-1; prevNodeIdx[1] = nIdx[1]; prevNodeIdx[2] = nIdx[2]-1;
                } else if (dir.equals("6")) {
                    prevNodeIdx[0] = nIdx[0]-1; prevNodeIdx[1] = nIdx[1]; prevNodeIdx[2] = nIdx[2];
                } else if (dir.equals("7")) {
                    prevNodeIdx[0] = nIdx[0]; prevNodeIdx[1] = nIdx[1]; prevNodeIdx[2] = nIdx[2]-1;
                } else {
                    throw new IllegalStateException(String.format("Unexpected 000 branch with node: %s", myNode.toString()));
                }

                List<Object> nextStackFrame = new ArrayList<Object>();

                nextStackFrame.add(prevNodeIdx);
                tabs += 1;
                nextStackFrame.add(tabs);
                nextStackFrame.add(myNode);
                loopStack.push(nextStackFrame);

            }

            if (!existingPaths.containsKey(myNode)) {
                existingPaths.put(myNode, new ArrayList<PathNode>());
            }

            try {
                PathNode followNode = (PathNode) currentStackFrame.get(2);
                existingPaths.get(followNode).add(myNode);
            } catch (IndexOutOfBoundsException e) {
                int ij = 0;
                // no-op, this can happen at times
            }

            if (DEBUG) {
                System.out.println(new String(new char[tabs]).replace("\0", " ") + "findPath - existingPaths");            
                for(Map.Entry<PathNode, List<PathNode>> kvp : existingPaths.entrySet()) {
                    System.out.println(new String(new char[tabs]).replace("\0", " ") + kvp.getKey().toString());
                    for(PathNode n : kvp.getValue()){
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

    public static List<List<PathNode>> buildPaths(Map<PathNode, List<PathNode>> existingPaths, List<String[]> nucs) throws IllegalStateException {

        Stack<List<Object>> loopStack = new Stack<List<Object>>();
        List<Object> newStackFrame = new ArrayList<Object>();
        
        int[] endNodeIdx = new int[3];
        endNodeIdx[0] = nucs.get(0).length; endNodeIdx[1] = nucs.get(1).length; endNodeIdx[2] = nucs.get(2).length;
        PathNode endNode = new PathNode(endNodeIdx);
        newStackFrame.add(endNode);

        Map<PathNode, List<List<PathNode>>> foundNodePathFragments = new HashMap<PathNode, List<List<PathNode>>>();
        List<PathNode> nodePathFragments = new ArrayList<PathNode>();
        List<List<PathNode>> nodePathFragmentsSet = new ArrayList<List<PathNode>>();
        nodePathFragmentsSet.add(nodePathFragments);

        foundNodePathFragments.put(endNode, nodePathFragmentsSet);
        newStackFrame.add(nodePathFragmentsSet);

        Integer starttabs = 0;
        newStackFrame.add(starttabs);

        loopStack.push(newStackFrame);

        while(!loopStack.empty()) {
            List<Object> currentStackFrame = loopStack.pop();

            PathNode currentNode = (PathNode) currentStackFrame.get(0);
            List<List<PathNode>> currentNodePathFragmentsSet = (List<List<PathNode>>) currentStackFrame.get(1);
            Integer tabs = (Integer) currentStackFrame.get(2);

            if (TIMED_STATUS && (NEXT_INTERVAL < System.currentTimeMillis())) {
                System.out.println(new String(new char[tabs]).replace("\0", " ") + Long.toString(System.currentTimeMillis()) + " " + Long.toString(System.currentTimeMillis() + SECONDS_CONST_15));
                System.out.println(new String(new char[tabs]).replace("\0", " ") + String.format("timed alert - buld_paths - node: %s", currentNode.toString()));
                NEXT_INTERVAL = System.currentTimeMillis() + SECONDS_CONST_15;
            }
    
            for(PathNode nextNode : existingPaths.get(currentNode)) {  
                List<List<PathNode>> nextPathNodePaths = new ArrayList<List<PathNode>>();

                if(nextNode.getValues()[0] > currentNode.getValues()[0] || nextNode.getValues()[1] > currentNode.getValues()[1] && nextNode.getValues()[2] > currentNode.getValues()[2]) {
                    throw new IllegalStateException(String.format("build_paths unexpected current_node %s = next_node %s.", currentNode.toString(), nextNode.toString()));
                }

                for(List<PathNode> fragmentNodePaths : currentNodePathFragmentsSet) {
                    List<PathNode> newNodePathFragment = new ArrayList<PathNode>();
                    newNodePathFragment.add(currentNode);
                    for(PathNode nextPn : fragmentNodePaths) {
                        newNodePathFragment.add(nextPn);
                    }
                    nextPathNodePaths.add(newNodePathFragment);
                }
    
                if (foundNodePathFragments.containsKey(nextNode)) {
                    for(List<PathNode> pathsSet : nextPathNodePaths) {
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

        int[] startNodeIdx = new int[3];
        startNodeIdx[0] = 0; startNodeIdx[1] = 0; startNodeIdx[2] = 0;
        PathNode sNode = new PathNode(startNodeIdx);

        if (DEBUG) {
            System.out.println("buildPaths - path node link complete");
            if(foundNodePathFragments.get(sNode).size() < 100){
                for (List<PathNode> ls : foundNodePathFragments.get(sNode)) {
                    if (ls.size() < 100) {
                        String debugStr = "";
                        for(PathNode pn : ls) {
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
                System.out.println("Too many to list: " + foundNodePathFragments.get(sNode).size());
            }
        }

        return foundNodePathFragments.get(sNode);
    }


    public static List<List<String>> buildCandidateSets(List<List<PathNode>> fullNodePaths, List<String[]> nucs) throws IllegalStateException {


        int[] startNodeIdx = new int[3];
        startNodeIdx[0] = 0; startNodeIdx[1] = 0; startNodeIdx[2] = 0;
        PathNode sNode = new PathNode(startNodeIdx);

        String skipChar = "-";

        List<List<String>> candidatesSet = new ArrayList<List<String>>();
        long currentCount = 0L;
        long maxCount = fullNodePaths.size() + 1;
        if (maxCount > 10000000) {
            System.out.println("Cutting output in half because it's too large to be useable (" + maxCount + ")");
            maxCount = Math.floorDiv(maxCount, 2);

        }
        for (List<PathNode> ls : fullNodePaths) {
            currentCount += 1;
            if (currentCount < maxCount) {
                if (TIMED_STATUS && (NEXT_INTERVAL < System.currentTimeMillis())) {
                    System.out.println(Long.toString(System.currentTimeMillis()) + " " + Long.toString(System.currentTimeMillis() + SECONDS_CONST_15));
                    System.out.println(String.format("timed alert - buld_paths - candidate sets: %d", currentCount));
                    NEXT_INTERVAL = System.currentTimeMillis() + SECONDS_CONST_15;
                }
        
                PathNode prevNode = sNode;
                PathNode nextNode = null;
                String cs1 = ""; String cs2 = ""; String cs3 = "";
                for(PathNode pn : ls) {
                    nextNode = pn;
                    cs1 += (nextNode.getValues()[0] > prevNode.getValues()[0]) ? nucs.get(0)[prevNode.getValues()[0]] : skipChar;
                    cs2 += (nextNode.getValues()[1] > prevNode.getValues()[1]) ? nucs.get(1)[prevNode.getValues()[1]] : skipChar;
                    cs3 += (nextNode.getValues()[2] > prevNode.getValues()[2]) ? nucs.get(2)[prevNode.getValues()[2]] : skipChar;
                    prevNode = nextNode;
                }
                List<String> candidateSet = new ArrayList<String>();
                candidateSet.add(cs1); candidateSet.add(cs2); candidateSet.add(cs3);
                candidatesSet.add(candidateSet);    
            }
        }

        return candidatesSet;
    }

}