package org.multiple.longcomm.subsequence;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.config.HttpClientConfig;

import org.apache.http.client.config.RequestConfig;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.HttpClientConfig;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;

import io.restassured.response.Response;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.path.json.JsonPath;
import java.util.List;
import java.util.ArrayList;

import io.restassured.response.ValidatableResponse;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.Scanner;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.Json;
import javax.json.JsonArrayBuilder;


@QuarkusTest
public class QuarkusTesting {
    @Test
    public void testProcessNucleotides() throws IOException {
        URL resource = getClass().getClassLoader().getResource("/input/sample_14.txt");
        File inputFile = new File(resource.getFile());
        String inputJson = new String(Files.readAllBytes(inputFile.toPath()), StandardCharsets.UTF_8);


        RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(10000)
        .setConnectionRequestTimeout(10000)
        .setSocketTimeout(10000)
        .build();
    
    HttpClientConfig httpClientFactory = HttpClientConfig.httpClientConfig()
        .httpClientFactory(() -> HttpClientBuilder.create()
            .setDefaultRequestConfig(requestConfig)
            .build());
    
    RestAssuredConfig config = RestAssured
        .config()
        .httpClient(httpClientFactory);

        ValidatableResponse r = given().config(config)
            .contentType(MediaType.APPLICATION_JSON)
            .body(inputJson)
        .when()
            .post("/nucleotides")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);

        r.body("score", is(2));

        JsonPath jsonPath = r.extract().body().jsonPath();
        List<List<String>> results = jsonPath.getList("matchSets");
        
        Boolean foundWinner = false;
        List<String> assertMatchSequences = new ArrayList<String>();
        assertMatchSequences.add("TT");
        assertMatchSequences.add("TT");



        for (List<String> resultMatchSequences : results) {
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
    }

    private String readInputJsonFromFile(String inFile) throws IOException {
        URL resource = getClass().getClassLoader().getResource(inFile);
        File inputFile = new File(resource.getFile());
        String inputJson = new String(Files.readAllBytes(inputFile.toPath()), StandardCharsets.UTF_8);

        return inputJson;
    }

    private MultipleLongCommonSubseqenceOutput readAssertContentFromFile(String assertFile) throws IOException {

        int assertPathScore = -1;
        List<String> assertMatchSequences = new ArrayList<String>();
        // InputStream assertStream = getClass().getResourceAsStream(assertFile);
        URL resource = getClass().getClassLoader().getResource(assertFile);
        File inputFile = new File(resource.getFile());
        try {
            Boolean foundEmptyLine = false;
            Boolean foundScore = false;
            try(Scanner sc = new Scanner(inputFile)) {
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

        List<List<String>> assertMatchSequencesSetofOne = new ArrayList<>();
        assertMatchSequencesSetofOne.add(assertMatchSequences);

        MultipleLongCommonSubseqenceOutput assertValues = new MultipleLongCommonSubseqenceOutput(assertPathScore, 0, assertMatchSequencesSetofOne);
        return assertValues;
    }

    private void fileBasedTest(String inFile, String assertFile, boolean convertInputFromFlatFileToJson) {

        String inputJson = null;
        MultipleLongCommonSubseqenceOutput assertValues = null;

        try {
            if (convertInputFromFlatFileToJson) {
                inputJson = convertFlatFileToJsonString(inFile);
            } else {
                inputJson = readInputJsonFromFile(inFile);
            }
        } catch (IOException e) {
            assertTrue(false, e.getMessage());
        }

        try{
            assertValues = readAssertContentFromFile(assertFile);
        } catch (IOException e) {
            assertTrue(false, e.getMessage());
        }

        ValidatableResponse r = given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(inputJson)
        .when()
            .post("/nucleotides")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);

        JsonPath jsonPath = r.extract().body().jsonPath();
        
        System.out.println(jsonPath.getInt("score"));
        System.out.println(assertValues.getScore());
    
        r.body("score", is(assertValues.getScore()));

        
        Boolean foundWinner = false;

        List<List<String>> resultsMatchSets = jsonPath.getList("matchSets");
        List<String> assertMatchSequences = assertValues.getMatchSets().get(0);
        for (List<String> resultMatchSequences : resultsMatchSets) {
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

        System.out.println(String.format("Time: %d", (jsonPath.getLong("timeElapsed")) / 1000));
    }

    public String convertFlatFileToJsonString(String inFile) throws IOException {
        URL resource = getClass().getClassLoader().getResource(inFile);
        File inputFile = new File(resource.getFile());
        List<String> lines = Files.readAllLines(inputFile.toPath(), StandardCharsets.UTF_8);
        
        int matchReward = Integer.parseInt(lines.get(0));
        int mismatchPenalty = Integer.parseInt(lines.get(1));
        int indelPenalty = Integer.parseInt(lines.get(2));
        
        List<String> nucs = new ArrayList<>();
        for (int i = 3; i < lines.size(); i++) {
          nucs.add(lines.get(i));
        }
        
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("matchReward", matchReward);
        builder.add("mismatchPenalty", mismatchPenalty);
        builder.add("indelPenalty", indelPenalty);
        
        JsonArrayBuilder nucsBuilder = Json.createArrayBuilder();
        for (String nuc : nucs) {
          nucsBuilder.add(nuc);
        }
        
        builder.add("nucs", nucsBuilder);
        
        JsonObject json = builder.build();
        
        String jsonString = json.toString();  
        
        return jsonString;
    }


    public void iterativeTestHarness(String fileId, Boolean convertInputFileFromFlatFileToJson) {
        // String rt = "/src/test/resources/";
        String rt = "/";
        fileBasedTest(rt.concat("input/sample_" + fileId + ".txt"), rt.concat("output/sample_" + fileId + ".txt"), convertInputFileFromFlatFileToJson);
    }

    @Test
    public void testSample1() {
        iterativeTestHarness("1", true);
    }

    @Test
    public void testSample2() {
        iterativeTestHarness("2", true);
    }

    @Test
    public void testSample3() {
        iterativeTestHarness("3", true);
    }

    @Test
    public void testSample4() {
        iterativeTestHarness("4", true);
    }

    @Test
    public void testSample5() {
        iterativeTestHarness("5", true);
    }

    @Test
    public void testSample6() {
        iterativeTestHarness("6", true);
    }

    @Test
    public void testSample9() {
        iterativeTestHarness("9", true);
    }

    @Test
    public void testSample10() {
        iterativeTestHarness("10", true);
    }

    @Test
    public void testSample11() {
        iterativeTestHarness("11", true);
    }

    @Test
    public void testSample12() {
        iterativeTestHarness("12", true);
    }

    @Test
    public void testSample13() {
        iterativeTestHarness("13", true);
    }

    @Test
    public void testSample14() {
        iterativeTestHarness("14", false);
    }
    

}
