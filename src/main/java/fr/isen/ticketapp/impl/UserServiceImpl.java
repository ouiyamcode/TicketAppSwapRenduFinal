package fr.isen.ticketapp.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.isen.ticketapp.interfaces.models.Services.UtilisateurService;
import fr.isen.ticketapp.interfaces.models.Utilisateur;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.inject.spi.CDI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UtilisateurService {
    String datasourceType = "bdd"; // ou mettre "json" ici pour changer ("json" ou "bdd")

    AgroalDataSource dataSource = CDI.current().select(AgroalDataSource.class).get();
    private static final String FILE_PATH = "src/main/resources/user.json";

    @Override
    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        if (datasourceType.equals("json")) {
            if (utilisateur == null) {
                System.err.println("Erreur : L'utilisateur reçu est null.");
                return null;
            }
            System.out.println("Utilisateur créé :");
            System.out.println("ID : " + utilisateur.getId());
            System.out.println("Nom : " + utilisateur.getNom());
            System.out.println("Email : " + utilisateur.getEmail());
            return utilisateur;
        }
    }

    @Override
    public Utilisateur AfficherUnUtilisateur(Utilisateur utilisateurParam) {
        if (utilisateurParam == null || utilisateurParam.getId() == 0) {
            System.err.println("Erreur : L'utilisateur passé en paramètre est null ou ne contient pas d'ID.");
            return null;
        }

        List<Utilisateur> utilisateurs = AfficherUtilisateur();
        for (Utilisateur utilisateur : utilisateurs) {
            if (utilisateur.getId().equals(utilisateurParam.getId())) {
                return utilisateur;
            }
        }

        System.err.println("Utilisateur non trouvé pour l'ID : " + utilisateurParam.getId());
        return null;
    }

    @Override
    public List<Utilisateur> AfficherUtilisateur() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        try {
            File jsonFile = new File(FILE_PATH);
            if (!jsonFile.exists()) {
                System.err.println("Erreur : Le fichier JSON n'existe pas à l'emplacement : " + FILE_PATH);
                return utilisateurs;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonFile);

            JsonNode userArray = rootNode.get("users");
            if (userArray != null && userArray.isArray()) {
                for (JsonNode userNode : userArray) {
                    Utilisateur utilisateur = mapper.treeToValue(userNode, Utilisateur.class);
                    utilisateurs.add(utilisateur);
                }
            } else {
                System.err.println("Erreur : Le fichier JSON ne contient pas de tableau 'users'.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture des utilisateurs depuis le fichier JSON : " + e.getMessage());
            e.printStackTrace();
        }
        return utilisateurs;
    }

    @Override
    public boolean SupprimerUtilisateur(Utilisateur utilisateurParam) {
        if (utilisateurParam == null || utilisateurParam.getId() == 0) {
            System.err.println("Erreur : L'utilisateur passé en paramètre est null ou ne contient pas d'ID.");
            return false;
        }

        List<Utilisateur> utilisateurs = AfficherUtilisateur();
        for (Utilisateur utilisateur : utilisateurs) {
            if (utilisateur.getId() == utilisateurParam.getId()) {
                utilisateurs.remove(utilisateur);
                System.out.println("Utilisateur supprimé : " + utilisateur.getNom());
                return true;
            }
        }

        System.err.println("Utilisateur non trouvé pour l'ID : " + utilisateurParam.getId());
        return false;
    }

    @Override
    public Utilisateur ModifierUtilisateur(Utilisateur utilisateurParam) {
        if (utilisateurParam == null || utilisateurParam.getId() == 0) {
            System.err.println("Erreur : L'utilisateur passé en paramètre est null ou ne contient pas d'ID.");
            return null;
        }

        List<Utilisateur> utilisateurs = AfficherUtilisateur();
        for (Utilisateur utilisateur : utilisateurs) {
            if (utilisateur.getId().equals(utilisateurParam.getId())) {
                utilisateur = utilisateurParam;
                System.out.println("Utilisateur modifié : " + utilisateur.getNom());
                return utilisateur;
            }
        }

        System.err.println("Utilisateur non trouvé pour l'ID : " + utilisateurParam.getId());
        return null;
    }
}
