package org.multiple.longcomm.subsequence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;



public class Testing {

    private List<Object> initTestFromFile(String inFile) {


        List<String[]> nucs = new ArrayList<String[]>();
        InputStream inStream = getClass().getResourceAsStream(inFile);
        try (Scanner sc = new Scanner(inStream)) {
            while (sc.hasNext()) {
                nucs.add(sc.next().strip().split(""));
            }
        }
        
        int match_reward = 1;
        int mismatch_penalty = 0;
        int indel_penalty = 0;

        Scoring scoring = new Scoring(match_reward, mismatch_penalty, indel_penalty);
    
        MultipleLongCommSubseq mlcs = new MultipleLongCommSubseq();
        mlcs.setVerbosity(false, false, false, false);


        List<Object> results = new ArrayList<Object>();
        try {
            // results = mlcs.findCommonSubseq(nucs,  scoring);
            results = mlcs.findCommonSubseqMDA(nucs,  scoring);
        } catch (Exception e) {
            System.out.print(e.getStackTrace());
        }
        return results;
    }

    private void fileBasedTest(String inFile, String assertFile) {
        long startTime = System.currentTimeMillis();

        List<Object> results = initTestFromFile(inFile);

        int assertPathScore = -1;
        List<String> assertMatchSequences = new ArrayList<String>();
        InputStream assertStream = getClass().getResourceAsStream(assertFile);
        try {
            Boolean foundEmptyLine = false;
            Boolean foundScore = false;
            try(Scanner sc = new Scanner(assertStream)) {
                while (sc.hasNext()) {
                    if (!foundEmptyLine) {
                        if (!foundScore) {
                            assertPathScore = Integer.valueOf(sc.nextLine());
                            foundScore = true;
                        } else {
                            String ams = sc.nextLine().strip();
                            if (ams.length() == 0) {
                                foundEmptyLine = true;
                            } else {
                                assertMatchSequences.add(ams);
                            }
                        }
                    } else {
                        sc.nextLine();
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        System.out.println((int)results.get(0));
        System.out.println(assertPathScore);
        assertEquals((int)results.get(0), assertPathScore);

        Boolean foundWinner = false;


        List<List<String>> resultMatchSequencesSets = (List<List<String>>) results.get(1);
        for (List<String> resultMatchSequences : resultMatchSequencesSets) {
            List<Integer> assertMatchedIndexes = new ArrayList<Integer>();
            for (String resultSequence : resultMatchSequences) {
                for(int i = 0; i < assertMatchSequences.size(); i++) {
                    if (!assertMatchedIndexes.contains(i)) {
                        if (resultSequence.equals(assertMatchSequences.get(i))) {
                            assertMatchedIndexes.add(i);
                        }
                    }
                }
            }
            if (resultMatchSequences.size() == assertMatchSequences.size()) {
                foundWinner = true;
            }
        }
        assertTrue(foundWinner);

        long endTime = System.currentTimeMillis();
        System.out.println(String.format("Time: %d", (endTime-startTime) / 1000));
    }

    public void iterativeTestHarness(String fileId) {
        // String rt = "/src/test/resources/";
        String rt = "/";
        fileBasedTest(rt.concat("input/sample_" + fileId + ".txt"), rt.concat("output/sample_" + fileId + ".txt"));
    }
    @Test
    public void testSample0() {
        iterativeTestHarness("0");

    }
    @Test
    public void testSample1() {
        iterativeTestHarness("1");
    }
    @Test
    public void testSample2() {
        iterativeTestHarness("2");
    }
    @Test
    public void testSample3() {
        iterativeTestHarness("3");
    }
    @Test
    public void testSample4() {
        iterativeTestHarness("4");
    }
    @Test
    public void testSample5() {
        iterativeTestHarness("5");
    }
    @Test
    public void testSample6() {
        iterativeTestHarness("6");
    }

    // @Test
    // @Tag("slow")
    public void testSample7() {
        iterativeTestHarness("7");
    }
    
    // @Test
    // @Tag("slow")
    public void testSample8() {
        iterativeTestHarness("8");
    }
    @Test
    public void testSample9() {
        iterativeTestHarness("9");
    }
    @Test
    public void testSample10() {
        iterativeTestHarness("10");
    }
    @Test
    public void testSample11() {
        iterativeTestHarness("11");
    }
    @Test
    public void testSample12() {
        iterativeTestHarness("12");
    }
    @Test
    public void testSample13() {
        iterativeTestHarness("13");
    }
    // @Test
    public void testSample14() {
        iterativeTestHarness("14");
    }
    // @Test
    public void testSample15() {
        iterativeTestHarness("15");
    }
    // @Test
    public void testSample16() {
        iterativeTestHarness("16");
    }
    // @Test
    public void testSample17() {
        iterativeTestHarness("17");
    }
    // @Test
    public void testSample18() {
        iterativeTestHarness("18");
    }
    // @Test
    public void testSample19() {
        iterativeTestHarness("19");
    }
    // @Test
    public void testSample20() {
        iterativeTestHarness("20");
    }

}