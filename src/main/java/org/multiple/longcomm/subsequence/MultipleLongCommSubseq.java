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
    
        // Perform multiple sequence alignment
        int[][][] score_keeper = new int[seq1.length + 1][seq2.length + 1][seq3.length + 1];
        String[][][] incoming_direction_keeper = new String[seq1.length + 1][seq2.length + 1][seq3.length + 1];
        Map<String, List<String>> existing_paths = new HashMap<>();

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
        System.out.println("result");
        for(List<String> cSet : resultsCandidates) {
            System.out.println(resultsScore);
            for(String candidate : cSet) {
                System.out.println(candidate);
            }
            System.out.println();
        }

 


        long endTime = System.currentTimeMillis();
        System.out.println(String.format("Time: %d", Math.floorDiv(endTime-startTime, 1000)));
    }

    public static List<Object> findCommonSubseq(List<String[]> nucs, Scoring scoring) throws Exception, IllegalStateException {

        List<List<List<Integer>>> scoreKeeper = new ArrayList<List<List<Integer>>>();
        List<List<List<String>>> incomingDirectionKeeper = new ArrayList<List<List<String>>>();

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

        Map<PathNode, List<PathNode>> existingPaths = new HashMap<PathNode, List<PathNode>>();

        // walkBackwardsNoRecurse(scoreKeeper, incomingDirectionKeeper, nucs, existingPaths, nucs.get(0).length, nucs.get(1).length, nucs.get(2).length, 0);
        walkBackwards(scoreKeeper, incomingDirectionKeeper, nucs, existingPaths, nucs.get(0).length, nucs.get(1).length, nucs.get(2).length, 0);

        if (DEBUG) {
            for(Map.Entry<PathNode, List<PathNode>> kvp : existingPaths.entrySet()) {
                System.out.println(kvp.getKey().toString() + kvp.getValue().toString());
            }
        }

        for (int i = 0; i < 5; i++) {
            System.out.println();
        }
        
        System.out.println("score: " + scoreKeeper.get(nucs.get(0).length).get(nucs.get(1).length).get(nucs.get(2).length));
    
        for (int i = 0; i < 5; i++) {
            System.out.println();
        }
        
        // List<List<String>> candidates = buildPaths(existinPaths, nucs, [], (), {}, 0);
        int[] zeroIdx = new int[3];
        zeroIdx[0] = 0; zeroIdx[1] = 0; zeroIdx[2] = 0;
        PathNode zeroNode = new PathNode(zeroIdx);
        Map<PathNode, List<List<String>>> foundPathFragments = new HashMap<PathNode, List<List<String>>>();
        List<List<String>> candidates = buildPaths(existingPaths, nucs, zeroNode, foundPathFragments, 0);

        if (DEBUG) {
            System.out.println("candidates");
            for(List<String> candidateSet : candidates) {
                for(String candidateElement : candidateSet) {
                    System.out.println(candidateElement);
                }
            }
        }

        int finalScore = scoreKeeper.get(nucs.get(0).length).get(nucs.get(1).length).get(nucs.get(2).length);

        List<Object> results = new ArrayList<Object>();
        results.add(finalScore);
        results.add(candidates);

        return results;
    
    // candidates = build_paths(existing_paths, nucs, ["","",""], (0,0,0), {}, 0)
    }

    public static void walkBackwards(List<List<List<Integer>>> scoreKeeper, List<List<List<String>>> incomingDirectionKeeper, List<String[]> nucs, Map<PathNode, List<PathNode>> existingPaths, int i, int j, int k, int tabs) throws Exception, IllegalStateException {
        if (i < 0 || j < 0 || k < 0) {
            throw new Exception("  " + new String(new char[tabs]).replace("\0", " ") + "invalid value: " + i + "," + j + "," + k);
        }

        int[] nIdx = new int[3];
        nIdx[0] = i; nIdx[1] = j; nIdx[2] = k;
        PathNode myNode = new PathNode(nIdx);

        if (DEBUG) {
            System.out.println("  " + new String(new char[tabs]).replace("\0"," ") + "cn: " + i + "," + j + "," + k);
        }

        if (existingPaths.containsKey(myNode)) {
            return;
        }

        int[] zeroIdx = new int[3];
        zeroIdx[0] = 0; zeroIdx[1] = 0; zeroIdx[2] = 0;
        if (myNode.equals(new PathNode(zeroIdx))) {
            existingPaths.put(myNode, new ArrayList<>());
            return;
        }

        if (TIMED_STATUS && (NEXT_INTERVAL < System.currentTimeMillis())) {
            System.out.println("timed alert - walk_backwards - node: " + i + "," + j + "," + k);
            NEXT_INTERVAL = System.currentTimeMillis() + SECONDS_CONST_15;
        }

        String[] directions = incomingDirectionKeeper.get(i).get(j).get(k).split("");

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
                prevNodeIdx[0] = i-1; prevNodeIdx[1] = j-1; prevNodeIdx[2] = k-1;
            } else if (dir.equals("2")) {
                prevNodeIdx[0] = i-1; prevNodeIdx[1] = j-1; prevNodeIdx[2] = k;
            } else if (dir.equals("3")) {
                prevNodeIdx[0] = i; prevNodeIdx[1] = j-1; prevNodeIdx[2] = k-1;
            } else if (dir.equals("4")) {
                prevNodeIdx[0] = i; prevNodeIdx[1] = j-1; prevNodeIdx[2] = k;
            } else if (dir.equals("5")) {
                prevNodeIdx[0] = i-1; prevNodeIdx[1] = j; prevNodeIdx[2] = k-1;
            } else if (dir.equals("6")) {
                prevNodeIdx[0] = i-1; prevNodeIdx[1] = j; prevNodeIdx[2] = k;
            } else if (dir.equals("7")) {
                prevNodeIdx[0] = i; prevNodeIdx[1] = j; prevNodeIdx[2] = k-1;
            } else {
                throw new IllegalStateException(String.format("Unexpected 000 branch with node: %s", myNode.toString()));
            }
            walkBackwards(scoreKeeper, incomingDirectionKeeper, nucs, existingPaths, prevNodeIdx[0], prevNodeIdx[1], prevNodeIdx[2], tabs+1);
            PathNode prevNode = new PathNode(prevNodeIdx);        
            existingPaths.get(prevNode).add(myNode);

        }
        existingPaths.put(myNode, new ArrayList<PathNode>());


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
        return;
    }

    public static void walkBackwardsNoRecurse(List<List<List<Integer>>> scoreKeeper, List<List<List<String>>> incomingDirectionKeeper, List<String[]> nucs, Map<PathNode, List<PathNode>> existingPaths, int i, int j, int k, int tabs) throws Exception, IllegalStateException {

        Stack<List<Object>> loopStack = new Stack<List<Object>>();

        List<Object> newStackFrame = new ArrayList<Object>();        
        int[] initNodeIdx = new int[3];
        initNodeIdx[0] = i; initNodeIdx[1] = j; initNodeIdx[2] = k;
        newStackFrame.add(initNodeIdx);
        
        loopStack.push(newStackFrame);

        while (!loopStack.empty()) {
            List<Object> currentStackFrame = loopStack.pop();
            int[] loopNodeIdx = (int[]) currentStackFrame.get(0);

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
                continue;
            }

            int[] zeroIdx = new int[3];
            zeroIdx[0] = 0; zeroIdx[1] = 0; zeroIdx[2] = 0;
            if (myNode.equals(new PathNode(zeroIdx))) {
                existingPaths.put(myNode, new ArrayList<>());
                try {
                    PathNode followNode = (PathNode) currentStackFrame.get(1);
                    existingPaths.get(myNode).add(followNode);
                } catch (IndexOutOfBoundsException e) {
                    int ij = 0;
                    // no-op, this can happen at times
                }
                continue;
            }

            if (TIMED_STATUS && (NEXT_INTERVAL < System.currentTimeMillis())) {
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

                // loopStack.push(followIdx);
                // walkBackwards(scoreKeeper, incomingDirectionKeeper, nucs, existingPaths, followIdx[0], followIdx[1], followIdx[2], tabs+1);
                // PathNode curNode = new PathNode(followIdx);        
                System.out.println("in loop " + Arrays.toString(prevNodeIdx));
                nextStackFrame.add(prevNodeIdx);
                nextStackFrame.add(myNode);
                loopStack.push(nextStackFrame);

                // existingPaths.get(curNode).add(myNode);
    
            }

            existingPaths.put(myNode, new ArrayList<PathNode>());
            try {
                PathNode followNode = (PathNode) currentStackFrame.get(1);
                existingPaths.get(myNode).add(followNode);
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

    public static List<List<String>> buildPaths(Map<PathNode, List<PathNode>> existingPaths, List<String[]> nucs, PathNode currentNode, Map<PathNode, List<List<String>>> foundPathFragments, int tabs) throws IllegalStateException {
        if (!existingPaths.containsKey(currentNode) || existingPaths.get(currentNode).size() == 0) {
            List<String> fragmentsSet = new ArrayList<String>(Arrays.asList("", "", ""));
            List<List<String>> fragmentsSets = new ArrayList<List<String>>();
            fragmentsSets.add(fragmentsSet);
            foundPathFragments.put(currentNode, fragmentsSets);
            return foundPathFragments.get(currentNode);
        }

        if (foundPathFragments.containsKey(currentNode)) {
            return foundPathFragments.get(currentNode);
        }

        if (TIMED_STATUS && NEXT_INTERVAL < System.currentTimeMillis()) {
            System.out.println(String.format("timed alert - buld_paths - node: %s", currentNode.toString()));
            NEXT_INTERVAL = System.currentTimeMillis() + SECONDS_CONST_15;
        }

        String skipChar = "-";
        List<List<String>> returnPaths = new ArrayList<List<String>>();

        for(PathNode nextNode : existingPaths.get(currentNode)) {
            List<List<String>> returnFragments = buildPaths(existingPaths, nucs, nextNode, foundPathFragments, tabs+1);

            if (DEBUG) {
                System.out.println(currentNode.toString());
                System.out.println(nextNode.toString());
                for(List<String> ls : returnFragments) {
                    for(String s : ls) {
                        System.out.println(s);
                    }
                }
            }

            if(nextNode.getValues()[0] > currentNode.getValues()[0] && nextNode.getValues()[1] > currentNode.getValues()[1] && nextNode.getValues()[2] > currentNode.getValues()[2]) {
                for(List<String> fragmentSet : returnFragments) {
                    List<String> newSet = new ArrayList<String>();
                    newSet.add(nucs.get(0)[currentNode.getValues()[0]] + fragmentSet.get(0));
                    newSet.add(nucs.get(1)[currentNode.getValues()[1]] + fragmentSet.get(1));
                    newSet.add(nucs.get(2)[currentNode.getValues()[2]] + fragmentSet.get(2));
                    returnPaths.add(newSet);
                }
            } else if(nextNode.getValues()[0] > currentNode.getValues()[0] && nextNode.getValues()[1] > currentNode.getValues()[1] && nextNode.getValues()[2] == currentNode.getValues()[2]) {
                for(List<String> fragmentSet : returnFragments) {
                    List<String> newSet = new ArrayList<String>();
                    newSet.add(nucs.get(0)[currentNode.getValues()[0]] + fragmentSet.get(0));
                    newSet.add(nucs.get(1)[currentNode.getValues()[1]] + fragmentSet.get(1));
                    newSet.add(skipChar + fragmentSet.get(2));
                    returnPaths.add(newSet);
                }
            } else if(nextNode.getValues()[0] > currentNode.getValues()[0] && nextNode.getValues()[1] == currentNode.getValues()[1] && nextNode.getValues()[2] > currentNode.getValues()[2]) {
                for(List<String> fragmentSet : returnFragments) {
                    List<String> newSet = new ArrayList<String>();
                    newSet.add(nucs.get(0)[currentNode.getValues()[0]] + fragmentSet.get(0));
                    newSet.add(skipChar + fragmentSet.get(1));
                    newSet.add(nucs.get(2)[currentNode.getValues()[2]] + fragmentSet.get(2));
                    returnPaths.add(newSet);
                }
            } else if(nextNode.getValues()[0] == currentNode.getValues()[0] && nextNode.getValues()[1] > currentNode.getValues()[1] && nextNode.getValues()[2] > currentNode.getValues()[2]) {
                for(List<String> fragmentSet : returnFragments) {
                    List<String> newSet = new ArrayList<String>();
                    newSet.add(skipChar + fragmentSet.get(0));
                    newSet.add(nucs.get(1)[currentNode.getValues()[1]] + fragmentSet.get(1));
                    newSet.add(nucs.get(2)[currentNode.getValues()[2]] + fragmentSet.get(2));
                    returnPaths.add(newSet);
                }
            } else if(nextNode.getValues()[0] > currentNode.getValues()[0] && nextNode.getValues()[1] == currentNode.getValues()[1] && nextNode.getValues()[2] == currentNode.getValues()[2]) {
                for(List<String> fragmentSet : returnFragments) {
                    List<String> newSet = new ArrayList<String>();
                    newSet.add(nucs.get(0)[currentNode.getValues()[0]] + fragmentSet.get(0));
                    newSet.add(skipChar + fragmentSet.get(1));
                    newSet.add(skipChar + fragmentSet.get(2));
                    returnPaths.add(newSet);
                }
            } else if(nextNode.getValues()[0] == currentNode.getValues()[0] && nextNode.getValues()[1] > currentNode.getValues()[1] && nextNode.getValues()[2] == currentNode.getValues()[2]) {
                for(List<String> fragmentSet : returnFragments) {
                    List<String> newSet = new ArrayList<String>();
                    newSet.add(skipChar + fragmentSet.get(0));
                    newSet.add(nucs.get(1)[currentNode.getValues()[1]] + fragmentSet.get(1));
                    newSet.add(skipChar + fragmentSet.get(2));
                    returnPaths.add(newSet);
                }
            } else if(nextNode.getValues()[0] == currentNode.getValues()[0] && nextNode.getValues()[1] == currentNode.getValues()[1] && nextNode.getValues()[2] > currentNode.getValues()[2]) {
                for(List<String> fragmentSet : returnFragments) {
                    List<String> newSet = new ArrayList<String>();
                    newSet.add(skipChar + fragmentSet.get(0));
                    newSet.add(skipChar + fragmentSet.get(1));
                    newSet.add(nucs.get(2)[currentNode.getValues()[2]] + fragmentSet.get(2));
                    returnPaths.add(newSet);
                }
            } else {
                throw new IllegalStateException(String.format("build_paths unexpected current_node %s = next_node %s.", currentNode.toString(), nextNode.toString()));
            }
        }


        foundPathFragments.put(currentNode, returnPaths);

        if (DEBUG) {
            for(List<String> ls : returnPaths) {
                for(String s : ls) {
                    System.out.println(s);
                }
            }
        }
        return returnPaths;
    }
}