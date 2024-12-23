package org.acme;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/json")
public class JsonPrinter {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Ticket> jsonPrint() {
        JsonReader jsonReader = new JsonReader();
        String filePath = "rempleizomouk.json";

        // Charger les tickets à partir du fichier JSON
        List<Ticket> tickets = jsonReader.getTicketFromJsonFile(Ticket.class, filePath);

        // Log des données lues
        System.out.println("Tickets lus : " + tickets);
        return tickets;
    }
}


