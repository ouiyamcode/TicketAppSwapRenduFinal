package fr.isen.ticketapp.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.isen.ticketapp.interfaces.models.Services.UtilisateurService;
import fr.isen.ticketapp.interfaces.models.Utilisateur;
import fr.isen.ticketapp.interfaces.models.enums.Role;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.inject.spi.CDI;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        else {
            if (utilisateur == null) {
                System.err.println("Erreur : L'utilisateur reçu est null.");
                return null; // Retourne null en cas de paramètre invalide
            }

            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                conn.setAutoCommit(false); // Démarre une transaction

                // Insertion ou mise à jour de l'utilisateur
                PreparedStatement userStmt = conn.prepareStatement(
                        "INSERT INTO utilisateur (id, nom, email, mdp, date_derniere_cnx, statut_actif, role) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                                "ON DUPLICATE KEY UPDATE nom = VALUES(nom), email = VALUES(email), " +
                                "mdp = VALUES(mdp), date_derniere_cnx = VALUES(date_derniere_cnx), " +
                                "statut_actif = VALUES(statut_actif), role = VALUES(role)"
                );
                userStmt.setInt(1, utilisateur.getId());
                userStmt.setString(2, utilisateur.getNom());
                userStmt.setString(3, utilisateur.getEmail());
                userStmt.setString(4, utilisateur.getMdp());
                userStmt.setString(5, utilisateur.getDateDerniereCnx());
                userStmt.setBoolean(6, utilisateur.isStatutActif());
                userStmt.setString(7, utilisateur.getRole().name());
                userStmt.executeUpdate();
                userStmt.close();

                conn.commit(); // Valider la transaction
                conn.close();

            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de l'insertion ou de la mise à jour de l'utilisateur", e);
            }

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
        if(datasourceType.equals("json")) {
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
        else {
            List<Utilisateur> utilisateurs = new ArrayList<>();
            Connection conn = null;

            try {
                conn = dataSource.getConnection();

                // Requête pour récupérer les informations des utilisateurs
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT u.id AS utilisateur_id, u.nom AS utilisateur_nom, u.email AS utilisateur_email, " +
                                "u.mdp AS utilisateur_mdp, u.date_derniere_cnx AS utilisateur_date_derniere_cnx, " +
                                "u.statut_actif AS utilisateur_statut_actif, u.role AS utilisateur_role " +
                                "FROM utilisateur u"
                );

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    // Construire l'objet Utilisateur
                    Utilisateur utilisateur = new Utilisateur();
                    utilisateur.setId(rs.getInt("utilisateur_id"));
                    utilisateur.setNom(rs.getString("utilisateur_nom"));
                    utilisateur.setEmail(rs.getString("utilisateur_email"));
                    utilisateur.setMdp(rs.getString("utilisateur_mdp"));
                    utilisateur.setDateDerniereCnx(rs.getString("utilisateur_date_derniere_cnx"));
                    utilisateur.setStatutActif(rs.getBoolean("utilisateur_statut_actif"));
                    utilisateur.setRole(Role.valueOf(rs.getString("utilisateur_role")));

                    // Ajouter l'utilisateur à la liste
                    utilisateurs.add(utilisateur);
                }

                stmt.close();
                conn.close();

            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la récupération des utilisateurs", e);
            }

            return utilisateurs;
        }

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
