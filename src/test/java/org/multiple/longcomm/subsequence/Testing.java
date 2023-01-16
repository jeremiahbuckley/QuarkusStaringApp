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
            File file = new File("path/to/file.txt");
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

        List<String> resultMatchSequences = (List<String>) results.get(1);
        if ((resultMatchSequences.get(0) == assertMatchSequences.get(0) && resultMatchSequences.get(1) == assertMatchSequences.get(2) && resultMatchSequences.get(2) == assertMatchSequences.get(2)) || 
            (resultMatchSequences.get(0) == assertMatchSequences.get(0) && resultMatchSequences.get(2) == assertMatchSequences.get(1) && resultMatchSequences.get(2) == assertMatchSequences.get(2)) || 
            (resultMatchSequences.get(1) == assertMatchSequences.get(0) && resultMatchSequences.get(0) == assertMatchSequences.get(2) && resultMatchSequences.get(2) == assertMatchSequences.get(2)) || 
            (resultMatchSequences.get(1) == assertMatchSequences.get(0) && resultMatchSequences.get(2) == assertMatchSequences.get(0) && resultMatchSequences.get(2) == assertMatchSequences.get(2)) || 
            (resultMatchSequences.get(2) == assertMatchSequences.get(0) && resultMatchSequences.get(0) == assertMatchSequences.get(1) && resultMatchSequences.get(2) == assertMatchSequences.get(2)) || 
            (resultMatchSequences.get(2) == assertMatchSequences.get(0) && resultMatchSequences.get(1) == assertMatchSequences.get(0) && resultMatchSequences.get(2) == assertMatchSequences.get(2)))
                foundWinner = true;
        assertTrue(foundWinner);
    }

    @Test
    public void testSample0() {
        String rt = "/Users/jeremiahbuckley/Documents/code/java-basic-app/tst/QuarkusStartingApp/src/test/resources/";
        fileBasedTest(rt.concat("input/sample_0.txt"), rt.concat("output/sample_0.txt"));
    }

    @Test
    public void testSample2() {
        String rt = "/Users/jeremiahbuckley/Documents/code/java-basic-app/tst/QuarkusStartingApp/src/test/resource/";
        fileBasedTest(rt.concat("input/sample_1.txt"), rt.concat("output/sample_1.txt"));
    }
}