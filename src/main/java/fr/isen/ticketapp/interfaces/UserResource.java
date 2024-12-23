package fr.isen.ticketapp.interfaces;

import fr.isen.ticketapp.impl.UserServiceImpl;
import fr.isen.ticketapp.interfaces.models.Utilisateur;
import org.acme.JsonReader;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/users")
public class UserResource {

    private final UserServiceImpl userService = new UserServiceImpl();
    private static final String FILE_PATH = "user.json";

    @POST
    @Path("/creer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(Utilisateur user) {
        Utilisateur createdUser = userService.creerUtilisateur(user);

        if (createdUser != null) {
            return Response.ok(createdUser).build(); // Retourne l'utilisateur créé avec HTTP 200
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Erreur lors de la création de l'utilisateur. Vérifiez les données envoyées.")
                    .build(); // Retourne une erreur 400
        }
    }

    // GET: Récupérer tous les utilisateurs depuis le fichier JSON
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Utilisateur> getUsers() {
        List<Utilisateur> users = userService.AfficherUtilisateur();
        System.out.println("Utilisateurs retournés : " + users);
        return users;
    }

    // GET: Récupérer un utilisateur par ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") Integer id) {
        Utilisateur utilisateurRecherche = new Utilisateur();
        utilisateurRecherche.setId(id);

        Utilisateur user = userService.AfficherUnUtilisateur(utilisateurRecherche);

        if (user != null) {
            System.out.println("Utilisateur trouvé : " + user);
            return Response.ok(user).build(); // Retourne l'utilisateur avec un statut HTTP 200
        } else {
            System.err.println("Utilisateur non trouvé pour l'ID : " + id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Utilisateur non trouvé pour l'ID : " + id)
                    .build(); // Retourne un statut HTTP 404
        }
    }

    @PUT
    @Path("/modifier")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifierUtilisateur(Utilisateur user) {
        Utilisateur updatedUser = new Utilisateur();
        updatedUser = userService.ModifierUtilisateur(user);

        if (updatedUser != null) {
            return Response.ok(updatedUser).build(); // Retourne l'utilisateur modifié
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Utilisateur non trouvé pour l'ID : " + user.getId() + ", modification impossible")
                    .build();
        }
    }

    @DELETE
    @Path("/supprimer")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUtilisateur(Utilisateur userToDelete) {
        boolean deleted = userService.SupprimerUtilisateur(userToDelete);

        if (deleted) {
            return Response.ok(true + " => L'utilisateur a été supprimé avec succès").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Utilisateur non trouvé pour l'ID : " + userToDelete.getId() + ", suppression impossible")
                    .build();
        }
    }
}
