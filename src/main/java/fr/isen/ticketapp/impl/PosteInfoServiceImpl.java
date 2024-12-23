package fr.isen.ticketapp.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.isen.ticketapp.interfaces.models.PosteInformatique;
import fr.isen.ticketapp.interfaces.models.Services.PosteInfoService;
import fr.isen.ticketapp.interfaces.models.TicketModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PosteInfoServiceImpl implements PosteInfoService {

    @Override
    public PosteInformatique creerPoste(PosteInformatique posteInfo) {
        if (posteInfo == null) {
            System.err.println("Erreur : Le poste reçu est null.");
            return null; // Retourne null en cas de paramètre invalide
        }

        // Afficher les informations du poste pour confirmation
        System.out.println("Poste créé :");
        System.out.println("ID : " + posteInfo.getId());
        System.out.println("Configuration : " + posteInfo.getConfiguration());
        System.out.println("Utilisateur affecté : " + posteInfo.getUtilisateurAffecte());
        System.out.println("État : " + posteInfo.getEtat());

        // Retourne le poste tel qu'il est reçu (simule un retour)
        return posteInfo;
    }


    @Override
    public List<PosteInformatique> AfficherPostes() {
        List<PosteInformatique> postes = new ArrayList<>();
        String filePath = "src/main/resources/poste.json"; // Chemin du fichier JSON des postes

        try {
            // Utilisation de l'ObjectMapper pour mapper le JSON
            ObjectMapper mapper = new ObjectMapper();

            // Vérifier si le fichier JSON existe
            File jsonFile = new File(filePath);
            if (!jsonFile.exists()) {
                System.err.println("Erreur : Le fichier JSON n'existe pas à l'emplacement : " + filePath);
                return postes; // Retourne une liste vide si le fichier n'existe pas
            }

            // Lire le fichier JSON en tant que noeud racine
            JsonNode rootNode = mapper.readTree(jsonFile);

            // Accéder au tableau "postes" dans le fichier JSON
            JsonNode postesArray = rootNode.get("postes");

            if (postesArray != null && postesArray.isArray()) {
                // Mapper chaque élément du tableau en objet PosteInformatique
                for (JsonNode posteNode : postesArray) {
                    PosteInformatique poste = mapper.treeToValue(posteNode, PosteInformatique.class);
                    postes.add(poste);
                }
            } else {
                System.err.println("Erreur : Le fichier JSON ne contient pas de tableau 'postes'.");
            }

            // Afficher les postes pour vérification
            if (postes.isEmpty()) {
                System.out.println("Aucun poste trouvé dans le fichier JSON.");
            } else {
                postes.forEach(poste -> System.out.println("Poste : " + poste.getConfiguration()));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture des postes depuis le fichier JSON : " + e.getMessage());
            e.printStackTrace();
        }

        return postes;
    }

    @Override
    public PosteInformatique AfficherUnPoste(PosteInformatique posteParam) {
        if (posteParam == null || posteParam.getId() == 0) {
            System.err.println("Erreur : Le poste passé en paramètre est null ou ne contient pas d'ID.");
            return null; // Retourne null si le paramètre est invalide
        }

        List<PosteInformatique> postes = AfficherPostes(); // Charge les postes depuis le JSON

        // Parcours des postes pour trouver une correspondance
        for (PosteInformatique poste : postes) {
            if (poste.getId() == posteParam.getId()) {
                return poste; // Retourne le poste correspondant
            }
        }

        // Si aucun poste ne correspond, affiche un message d'erreur et retourne null
        System.err.println("Poste non trouvé pour l'ID : " + posteParam.getId());
        return null;
    }

    @Override
    public PosteInformatique ModifierPoste(PosteInformatique posteParam) {
        // Vérification des paramètres
        if (posteParam == null || posteParam.getId() == 0) {
            System.err.println("Erreur : Le poste passé en paramètre est null ou ne contient pas d'ID.");
            return null;
        }

        List<PosteInformatique> postes = AfficherPostes(); // Récupère tous les postes
        for (PosteInformatique poste : postes) {
            if (poste.getId() == posteParam.getId()) {
                // Simule la modification du poste
                poste = posteParam;
                System.out.println("Poste modifié avec succès : " + poste);
                return poste;
            }
        }

        System.err.println("Poste non trouvé pour l'ID : " + posteParam.getId());
        return null;
    }


    @Override
    public boolean SupprimerPoste(PosteInformatique posteParam) {
        // Vérification des paramètres
        if (posteParam == null || posteParam.getId() == 0) {
            System.err.println("Erreur : Le poste passé en paramètre est null ou ne contient pas d'ID.");
            return false;
        }

        List<PosteInformatique> postes = AfficherPostes(); // Récupère tous les postes
        for (PosteInformatique poste : postes) {
            if (poste.getId() == posteParam.getId()) {
                // Simule la modification du poste
                poste = posteParam;
                System.out.println("Poste modifié avec succès : " + poste);
                return true;
            }
        }

        System.err.println("Poste non trouvé pour l'ID : " + posteParam.getId());
        return false;
    }
}
