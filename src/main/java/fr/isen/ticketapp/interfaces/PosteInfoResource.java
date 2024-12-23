package fr.isen.ticketapp.interfaces;

import fr.isen.ticketapp.impl.PosteInfoServiceImpl;
import fr.isen.ticketapp.interfaces.models.PosteInformatique;

import fr.isen.ticketapp.interfaces.models.TicketModel;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/postes")
public class PosteInfoResource {

    private final PosteInfoServiceImpl posteInfoService = new PosteInfoServiceImpl();
    @POST
    @Path("/creer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPoste(PosteInformatique poste) {
        PosteInformatique createdPoste = posteInfoService.creerPoste(poste);

        if (createdPoste != null) {
            return Response.ok(createdPoste).build(); // Retourne le poste créé avec HTTP 200
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Erreur lors de la création du poste. Vérifiez les données envoyées.")
                    .build(); // Retourne une erreur 400
        }
    }

    // GET: Récupérer tous les postes informatiques
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PosteInformatique> getPostes() {
        List<PosteInformatique> postes = posteInfoService.AfficherPostes();
        System.out.println("Postes retournés : " + postes);
        return postes;
    }

    // GET: Récupérer un poste informatique par ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPosteById(@PathParam("id") Integer id) {
        PosteInformatique posteRecherche = new PosteInformatique();
        posteRecherche.setId(id);

        PosteInformatique poste = posteInfoService.AfficherUnPoste(posteRecherche);

        if (poste != null) {
            System.out.println("Poste trouvé : " + poste);
            return Response.ok(poste).build(); // Retourne le poste avec un statut HTTP 200
        } else {
            System.err.println("Poste non trouvé pour l'ID : " + id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Poste informatique non trouvé pour l'ID : " + id)
                    .build(); // Retourne un statut HTTP 404
        }
    }
    @PUT
    @Path("/modifier")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifierPoste(PosteInformatique poste) {
        PosteInformatique posteRecherche = new PosteInformatique();
        posteRecherche = posteInfoService.ModifierPoste(poste);
        if (posteRecherche != null) {
            return Response.ok(posteRecherche).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Ticket non trouvé pour l' ID : " + poste.getId() + ", pas de modification possible")
                    .build();
        }
    }
    @DELETE
    @Path("/supprimer")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deletePoste(PosteInformatique posteAsuppr){
        boolean supprime = posteInfoService.SupprimerPoste(posteAsuppr);
        if(supprime){
            return Response.ok(true + " => Le poste a été supprimé avec succès").build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("poste non trouvé pour l' ID : " + posteAsuppr.getId() + ", pas de suppression possible")
                    .build();
        }
    }

}
