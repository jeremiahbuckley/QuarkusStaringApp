package org.multiple.longcomm.subsequence;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.lang.Math;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

import javax.naming.SizeLimitExceededException;

import java.lang.IllegalStateException;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;

public class MultipleLongCommSubseq {
    public static Boolean VERBOSE = false;
    public static Boolean DEBUG = false;
    public static Boolean TIMED_STATUS = false;
    public static Integer SECONDS_CONST_15 = 15 * 1000;
    public static Long NEXT_INTERVAL = 0L;

    public static Boolean USE_3D_STRATEGY = false;

    public static void main(String[] args) {
        MultipleLongCommSubseq multipleLongCommSubseq = new MultipleLongCommSubseq();

        MultipleLongCommonSubsequenceInput input = multipleLongCommSubseq.getInputFromCommand(args);

        List<Object> results = multipleLongCommSubseq.doNucleotideCompare(input);

        multipleLongCommSubseq.printResults(results);
    }

    public MultipleLongCommSubseq() {

    }

    public MultipleLongCommonSubsequenceInput getInputFromCommand(String[] args) {
        File f = new File(args[0]);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            System.out.print(e);
        }
        MultipleLongCommonSubsequenceInput input = getWorkingDatasetFromFile(new BufferedInputStream(fis));

        for(int i=1; i < args.length; i++) {
            if (args[i].equals("-v")) {
                input.setVerbose(true);
            } else if (args[i].equals("-vv")) {
                input.setVerbose(true);
                input.setTimedStatus(true);
            } else if (args[i].equals("-vvv")) {
                input.setVerbose(true);
                input.setTimedStatus(true);
                input.setDebug(true);
            } else if (args[i].equals("-n")) {
                USE_3D_STRATEGY = true;
            } else {
                System.out.println(String.format("No match for %d - %s", i, args[i]));
            }
        }

        if (input.getVerbose()) {
            for (String[] n : input.getNucs()) {
                System.out.println(Arrays.toString(n));
            }
        }

        return input;
    }

    public MultipleLongCommonSubsequenceInput getWorkingDatasetFromFile(InputStream inStream)  {
        MultipleLongCommonSubsequenceInput input = new MultipleLongCommonSubsequenceInput();

        // File file = new File(fileName);
        // try (Scanner sc = new Scanner(file)){
        // InputStream inStream = getClass().getResourceAsStream(fileName);
        try (Scanner sc = new Scanner(inStream)) {
            int matchReward = Integer.parseInt(sc.nextLine().trim());
            int mismatchPenalty = Integer.parseInt(sc.nextLine().trim());
            int indelPenalty = Integer.parseInt(sc.nextLine().trim());
            List<String[]> nucs = new ArrayList<String[]>();
            while (sc.hasNext()) {
                nucs.add(sc.next().split(""));
            }
            input.init(matchReward, mismatchPenalty, indelPenalty, nucs);
        }

        return input;

    }

    public List<Object> doNucleotideCompare(MultipleLongCommonSubsequenceInput input) {
        List<Object> results = new ArrayList<Object>();
        try {
            results = findCommonSubseq(input);
        } catch (Exception e) {
            System.out.print(e);
        }

        return results;
    }

    public void printResults(List<Object> results) {
        int resultsScore = (int) results.get(0);
        long diffTime = (long) results.get(1);
        List<List<String>> resultsCandidates = (List<List<String>>) results.get(2);

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

        System.out.println(String.format("Time: %.4f", diffTime / 1000.00));

    }

    public static List<Object> findCommonSubseq(MultipleLongCommonSubsequenceInput input) throws Exception, IllegalStateException {
        long startTime = System.currentTimeMillis();
        NEXT_INTERVAL = startTime + SECONDS_CONST_15;

        List<String[]> nucs = input.getNucs();
        Scoring scoring = input.getScoring();
        VERBOSE = input.getVerbose();
        TIMED_STATUS = input.getTimedStatus();
        DEBUG = input.getDebug();
        USE_3D_STRATEGY = input.getUse3DStrategy();

        MultiDimensionalLCSStrategy mdlcss = new MultiDimensionalLCSStrategy(VERBOSE, DEBUG, TIMED_STATUS, NEXT_INTERVAL, SECONDS_CONST_15);
        System.out.println("maybe using multidm strategy");
        if (USE_3D_STRATEGY) { 
            mdlcss = new ThreeDimensionalLCSStrategy(VERBOSE, DEBUG, TIMED_STATUS,  NEXT_INTERVAL, SECONDS_CONST_15);
            System.out.println("using 3d strategy");
        }
        Map<List<Integer>, List<List<Integer>>> existingPaths = mdlcss.scoreSpace(nucs, scoring);

        if (DEBUG) {
            for(Map.Entry<List<Integer>, List<List<Integer>>> kvp : existingPaths.entrySet()) {
                System.out.println(kvp.getKey().toString() + kvp.getValue().toString());
            }
        }
        
        List<List<List<Integer>>> candidatePaths = null;
        try {
            candidatePaths = buildPaths(existingPaths, nucs);
        } catch (Exception e) {
            System.out.print(e);
            throw e;
        }

        List<List<String>> candidates = null;
        try {
            candidates = buildCandidateSets(candidatePaths, nucs);
        } catch (Exception e) {
            System.out.print(e);
            throw e;
        }

        if (DEBUG) {
            System.out.println("candidates");
            System.out.println(candidates.size());
        }

        int finalScore = mdlcss.getFinalScore();

        long endTime = System.currentTimeMillis();

        List<Object> results = new ArrayList<Object>();
        results.add(finalScore);
        results.add(endTime-startTime);
        results.add(candidates);

        return results;
    
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