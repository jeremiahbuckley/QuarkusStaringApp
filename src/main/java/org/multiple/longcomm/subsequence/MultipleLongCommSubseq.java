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


public class MultipleLongCommSubseq {
    public static Boolean VERBOSE = false;
    public static Boolean DEBUG = false;
    public static Boolean TIMED_STATUS = false;
    public static Integer SECONDS_CONST_15 = 15 * 1000;
    public static Long NEXT_INTERVAL = 0L;

    public static Boolean USE_3D_STRATEGY = false;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        NEXT_INTERVAL = startTime + SECONDS_CONST_15;

        List<String[]> nucs = new ArrayList<String[]>();
        Scanner sc = null;
        try {
            File file = new File(args[0]);
            sc = new Scanner(file);
            while (sc.hasNext()) {
                nucs.add(sc.next().split(""));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }

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
            } else if (args[i].equals("-n")) {
                USE_3D_STRATEGY = true;
            } else {
                System.out.println(String.format("No match for %d - %s", i, args[i]));
            }

        }

        if (VERBOSE) {
            for (String[] n : nucs) {
                System.out.println(Arrays.toString(n));
            }
        }

        int matchReward = 1;
        int mismatchPenalty = 0;
        int indelPenalty = -1;
        Scoring scoring = new Scoring(matchReward, mismatchPenalty, indelPenalty);

        List<Object> results = new ArrayList<Object>();
        try {
            results = findCommonSubseqMDA(nucs, scoring);
        } catch (Exception e) {
            System.out.print(e);
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

    public static void setVerbosity(Boolean verbose, Boolean timedStatus, Boolean debug, Boolean use3dstrategy) {
        VERBOSE = verbose;
        TIMED_STATUS = timedStatus;
        DEBUG = debug;
        USE_3D_STRATEGY = use3dstrategy;
    }

    public static List<Object> findCommonSubseqMDA(List<String[]> nucs, Scoring scoring) throws Exception, IllegalStateException {


        MultiDimensionalLCSStrategy mdlcss = new MultiDimensionalLCSStrategy(VERBOSE, DEBUG, TIMED_STATUS, NEXT_INTERVAL, SECONDS_CONST_15);
        if (USE_3D_STRATEGY) { 
            mdlcss = new ThreeDimensionalLCSStrategy(VERBOSE, DEBUG, TIMED_STATUS,  NEXT_INTERVAL, SECONDS_CONST_15);
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

        List<Object> results = new ArrayList<Object>();
        results.add(finalScore);
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