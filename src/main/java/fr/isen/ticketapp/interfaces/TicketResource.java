package fr.isen.ticketapp.interfaces;

import fr.isen.ticketapp.impl.TicketServiceImpl;
import fr.isen.ticketapp.interfaces.models.Services;
import fr.isen.ticketapp.interfaces.models.TicketModel;
import fr.isen.ticketapp.interfaces.models.enums.IMPACT;
import fr.isen.ticketapp.interfaces.models.enums.etatTicket;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.Ticket;

import java.util.List;

@Path("/tickets")
public class TicketResource {

    private final TicketServiceImpl ticketService = new TicketServiceImpl();

    @POST
    @Path("/creer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTicket(TicketModel ticket) {
        TicketModel createdTicket = ticketService.creerTicket(ticket);

        if (createdTicket != null) {
            /*createdTicket = ticketService.AfficherUnTicket(createdTicket); //finir afficher le retour dans postman*/
            return Response.ok(createdTicket).build(); // Retourne le ticket créé avec HTTP 200
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Erreur lors de la création du ticket. Vérifiez les données envoyées.")
                    .build(); // Retourne une erreur 400
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TicketModel> getTickets() {
        List<TicketModel> tickets = ticketService.AfficherTickets();
        System.out.println("Tickets retournés : " + tickets);
        return tickets;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTicketById(@PathParam("id") Integer id) {
        TicketModel ticketRecherche = new TicketModel();
        ticketRecherche.setId(id);

        TicketModel ticket = ticketService.AfficherUnTicket(ticketRecherche);

        if (ticket != null) {
            return Response.ok(ticket).build(); // Retourne le ticket avec un statut HTTP 200
        }
        else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Ticket non trouvé pour l'ID : " + id)
                    .build(); // Retourne un statut HTTP 404
        }
    }

    @PUT
    @Path("/modifier")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifierTicket(TicketModel ticket) {
        TicketModel ticketRecherche = new TicketModel();
        ticketRecherche = ticketService.ModifierTicket(ticket);
        if (ticketRecherche != null) {
            return Response.ok(ticketRecherche).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Ticket non trouvé pour l' ID : " + ticket.getId() + ", pas de modification possible")
                    .build();
        }
    }

    @DELETE
    @Path("/supprimer")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteTicket(TicketModel ticketASuppr){
        boolean supprime = ticketService.SupprimerUnTicket(ticketASuppr);
        if(supprime){
            return Response.ok(true + " => Le ticket a été supprimé avec succès").build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Ticket non trouvé pour l' ID : " + ticketASuppr.getId() + ", pas de suppression possible")
                    .build();
        }
    }
}
