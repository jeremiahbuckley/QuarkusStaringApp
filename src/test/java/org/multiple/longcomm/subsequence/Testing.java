package org.multiple.longcomm.subsequence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;



public class Testing {

    private List<Object> initTestFromFile(String inFile) {

        String nuc1 = "";
        String nuc2 = "";
        String nuc3 = "";
        try {
            File file = new File(inFile);
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
        
        int match_reward = 1;
        int mismatch_penalty = 0;
        int indel_penalty = 0;

        Scoring scoring = new Scoring(match_reward, mismatch_penalty, indel_penalty);
    
        List<String[]> nucs = new ArrayList<String[]>();
        nucs.add(seq1);
        nucs.add(seq2);
        nucs.add(seq3);

        MultipleLongCommSubseq mlcs = new MultipleLongCommSubseq();

        List<Object> results = new ArrayList<Object>();
        try {
            results = mlcs.findCommonSubseq(nucs,  scoring);
        } catch (Exception e) {
            System.out.print(e.getStackTrace());
        }
        return results;
    }

    private void fileBasedTest(String inFile, String assertFile) {
        List<Object> results = initTestFromFile(inFile);

        int assertPathScore = -1;
        List<String> assertMatchSequences = new ArrayList<String>();
        try {
            File file = new File(assertFile);
            try(Scanner sc = new Scanner(file)) {
                assertPathScore = Integer.valueOf(sc.nextLine());
                assertMatchSequences.add(sc.nextLine());
                assertMatchSequences.add(sc.nextLine());
                assertMatchSequences.add(sc.nextLine());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        assertEquals((int)results.get(0), assertPathScore);

        Boolean foundWinner = false;

        List<List<String>> resultMatchSequencesSets = (List<List<String>>) results.get(1);
        for (List<String> resultMatchSequences : resultMatchSequencesSets) {
            if ((resultMatchSequences.get(0).equals(assertMatchSequences.get(0)) && resultMatchSequences.get(1).equals(assertMatchSequences.get(1)) && resultMatchSequences.get(2).equals(assertMatchSequences.get(2))) || 
                (resultMatchSequences.get(0).equals(assertMatchSequences.get(0)) && resultMatchSequences.get(2).equals(assertMatchSequences.get(1)) && resultMatchSequences.get(1).equals(assertMatchSequences.get(2))) || 
                (resultMatchSequences.get(1).equals(assertMatchSequences.get(0)) && resultMatchSequences.get(0).equals(assertMatchSequences.get(1)) && resultMatchSequences.get(2).equals(assertMatchSequences.get(2))) || 
                (resultMatchSequences.get(1).equals(assertMatchSequences.get(0)) && resultMatchSequences.get(2).equals(assertMatchSequences.get(1)) && resultMatchSequences.get(0).equals(assertMatchSequences.get(2))) || 
                (resultMatchSequences.get(2).equals(assertMatchSequences.get(0)) && resultMatchSequences.get(0).equals(assertMatchSequences.get(1)) && resultMatchSequences.get(1).equals(assertMatchSequences.get(2))) || 
                (resultMatchSequences.get(2).equals(assertMatchSequences.get(0)) && resultMatchSequences.get(1).equals(assertMatchSequences.get(1)) && resultMatchSequences.get(0).equals(assertMatchSequences.get(2))))
                    foundWinner = true;
        }
        assertTrue(foundWinner);
    }

    public void iterativeTestHarness(String fileId) {
        String rt = "/Users/jeremiahbuckley/Documents/code/java-basic-app/tst/QuarkusStartingApp/src/test/resources/";
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

    public void testSample7() {
        iterativeTestHarness("7");
    }
    @Test
    public void testSample8() {
        iterativeTestHarness("8");
    }
    @Test
    public void testSample9() {
        iterativeTestHarness("9");
    }

}