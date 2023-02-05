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

// import java.io.StringReader;

@Path("/nucleotides")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MLCSGateway {
    @POST
    public Response processNucleotides(JsonObject input) {
        List<String[]> nucs = new ArrayList<String[]>();

        try {
            // JsonObject input = Json.createReader(new StringReader(json)).readObject();

            if (!input.containsKey("matchReward") || !input.containsKey("mismatchPenalty") || !input.containsKey("indelPenalty")) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Required keys missing in the JSON payload").build();
            }

            int matchReward = input.getInt("matchReward");
            int mismatchPenalty = input.getInt("mismatchPenalty");
            int indelPenalty = input.getInt("indelPenalty");

            JsonArray nucsArray = input.getJsonArray("nucs");
            for (int i = 0; i < nucsArray.size(); i++) {
                if (!nucsArray.get(i).getValueType().equals(ValueType.STRING)) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Invalid type for nucs array element. Expecting string.").build();
                    // throw new BadRequestException("Invalid type for nucs array element. Expecting string.");
                }
                String nuc = nucsArray.getString(i);
                nucs.add(nuc.split(""));
            }


            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

        // Do processing on nucs, matchReward, mismatchPenalty and indelPenalty here

        return Response.ok().build();
    }
}