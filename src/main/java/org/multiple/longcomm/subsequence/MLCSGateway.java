package org.multiple.longcomm.subsequence;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue.ValueType;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonArrayBuilder;

// import java.io.StringReader;


@Path("/nucleotides")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MLCSGateway {
    @POST
    public Response processNucleotides(JsonObject input) {
        MultipleLongCommonSubsequenceInput inputObj = new MultipleLongCommonSubsequenceInput();

        try {

            if (!input.containsKey("matchReward") || !input.containsKey("mismatchPenalty") || !input.containsKey("indelPenalty")) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Required keys missing in the JSON payload").build();
            }

            int matchReward = input.getInt("matchReward");
            int mismatchPenalty = input.getInt("mismatchPenalty");
            int indelPenalty = input.getInt("indelPenalty");

            JsonArray nucsArray = input.getJsonArray("nucs");
            List<String[]> nucs = new ArrayList<String[]>();
            for (int i = 0; i < nucsArray.size(); i++) {
                if (!nucsArray.get(i).getValueType().equals(ValueType.STRING)) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Invalid type for nucs array element. Expecting string.").build();
                    // throw new BadRequestException("Invalid type for nucs array element. Expecting string.");
                }
                String nuc = nucsArray.getString(i);
                nucs.add(nuc.split(""));
            }

            inputObj = new MultipleLongCommonSubsequenceInput(matchReward, mismatchPenalty, indelPenalty, nucs);

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

        MultipleLongCommSubseq mlcs = new MultipleLongCommSubseq();
        MultipleLongCommonSubseqenceOutput output = mlcs.doNucleotideCompare(inputObj);

        System.out.println("Response entity: " + output);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("score", output.getScore());
        builder.add("timeElapsed", output.getTimeElapsed());
        
        JsonArrayBuilder matchSetsBuilder = Json.createArrayBuilder();
        for (List<String> matchSet : output.getMatchSets()) {
          JsonArrayBuilder innerBuilder = Json.createArrayBuilder();
          for (String match : matchSet) {
            innerBuilder.add(match);
          }
          matchSetsBuilder.add(innerBuilder);
        }
        
        builder.add("matchSets", matchSetsBuilder);
        
        JsonObject jsonObject = builder.build();


        return Response.status(Response.Status.OK).entity(jsonObject).build();

        
    }
}