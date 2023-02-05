package org.multiple.longcomm.subsequence;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class QuarkusTesting {
    @Test
    public void testProcessNucleotides() throws IOException {
        URL resource = getClass().getClassLoader().getResource("input.json");
        File inputFile = new File(resource.getFile());
        String inputJson = new String(Files.readAllBytes(inputFile.toPath()), StandardCharsets.UTF_8);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(inputJson)
        .when()
            .post("/nucleotides")
        .then()
            .statusCode(200);

        // Additional assertions can be added here to verify the processing was successful
    }
}
