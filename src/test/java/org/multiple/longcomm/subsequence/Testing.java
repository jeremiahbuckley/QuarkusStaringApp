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

    private MultipleLongCommonSubseqenceOutput initTestFromFile(String inFile) {


        MultipleLongCommSubseq mlcs = new MultipleLongCommSubseq();

        MultipleLongCommonSubsequenceInput input = mlcs.getWorkingDatasetFromFile(getClass().getResourceAsStream(inFile));
        input.setDebug(false);
        input.setVerbose(false);
        input.setTimedStatus(false);
        input.setUse3DStrategy(false);

        try {
            return mlcs.findCommonSubseq(input);
        } catch (Exception e) {
            System.out.print(e.getStackTrace());
        }
        return null;
    }

    private void fileBasedTest(String inFile, String assertFile) {
        long startTime = System.currentTimeMillis();

        MultipleLongCommonSubseqenceOutput results = initTestFromFile(inFile);

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

        System.out.println(results.getScore());
        System.out.println(assertPathScore);
        assertEquals(results.getScore(), assertPathScore);

        Boolean foundWinner = false;


        for (List<String> resultMatchSequences : results.getMatchSets()) {
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
            if (resultMatchSequences.size() == assertMatchedIndexes.size()) {
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
    // @Test
    public void testSample0() {
        iterativeTestHarness("0");

    }
    // @Test
    public void testSample1() {
        iterativeTestHarness("1");
    }
    // @Test
    public void testSample2() {
        iterativeTestHarness("2");
    }
    // @Test
    public void testSample3() {
        iterativeTestHarness("3");
    }
    // @Test
    public void testSample4() {
        iterativeTestHarness("4");
    }
    // @Test
    public void testSample5() {
        iterativeTestHarness("5");
    }
    // @Test
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
    // @Test
    public void testSample9() {
        iterativeTestHarness("9");
    }
    // @Test
    public void testSample10() {
        iterativeTestHarness("10");
    }
    // @Test
    public void testSample11() {
        iterativeTestHarness("11");
    }
    // @Test
    public void testSample12() {
        iterativeTestHarness("12");
    }
    // @Test
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