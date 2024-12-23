package fr.isen.ticketapp.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.isen.ticketapp.interfaces.models.PosteInformatique;
import fr.isen.ticketapp.interfaces.models.Services.PosteInfoService;
import fr.isen.ticketapp.interfaces.models.TicketModel;
import fr.isen.ticketapp.interfaces.models.Utilisateur;
import fr.isen.ticketapp.interfaces.models.enums.IMPACT;
import fr.isen.ticketapp.interfaces.models.enums.Role;
import fr.isen.ticketapp.interfaces.models.enums.etatPoste;
import fr.isen.ticketapp.interfaces.models.enums.etatTicket;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.inject.spi.CDI;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PosteInfoServiceImpl implements PosteInfoService {
    String datasourceType = "json"; // ou mettre "json" ici pour changer ("json" ou "bdd")

    AgroalDataSource dataSource = CDI.current().select(AgroalDataSource.class).get();

    @Override
    public PosteInformatique creerPoste(PosteInformatique posteInfo) {
        if (datasourceType.equals("json")) {
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
        else {
            if (posteInfo == null) {
                System.err.println("Erreur : Le poste reçu est null.");
                return null; // Retourne null en cas de paramètre invalide
            }

            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                conn.setAutoCommit(false); // Démarre une transaction

                // Insertion ou mise à jour de l'utilisateur affecté
                if (posteInfo.getUtilisateurAffecte() != null) {
                    Utilisateur utilisateurAffecte = posteInfo.getUtilisateurAffecte();
                    PreparedStatement userStmt = conn.prepareStatement(
                            "INSERT INTO utilisateur (id, nom, email, mdp, date_derniere_cnx, statut_actif, role) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                                    "ON DUPLICATE KEY UPDATE nom = VALUES(nom), email = VALUES(email), " +
                                    "mdp = VALUES(mdp), date_derniere_cnx = VALUES(date_derniere_cnx), " +
                                    "statut_actif = VALUES(statut_actif), role = VALUES(role)"
                    );
                    userStmt.setInt(1, utilisateurAffecte.getId());
                    userStmt.setString(2, utilisateurAffecte.getNom());
                    userStmt.setString(3, utilisateurAffecte.getEmail());
                    userStmt.setString(4, utilisateurAffecte.getMdp());
                    userStmt.setString(5, utilisateurAffecte.getDateDerniereCnx());
                    userStmt.setBoolean(6, utilisateurAffecte.isStatutActif());
                    userStmt.setString(7, utilisateurAffecte.getRole().name());
                    userStmt.executeUpdate();
                    userStmt.close();
                }

                // Insertion ou mise à jour du poste informatique
                PreparedStatement posteStmt = conn.prepareStatement(
                        "INSERT INTO poste_info (id, configuration, etat, utilisateur_affecte) " +
                                "VALUES (?, ?, ?, ?) " +
                                "ON DUPLICATE KEY UPDATE configuration = VALUES(configuration), " +
                                "etat = VALUES(etat), utilisateur_affecte = VALUES(utilisateur_affecte)"
                );
                posteStmt.setInt(1, posteInfo.getId());
                posteStmt.setString(2, posteInfo.getConfiguration());
                posteStmt.setString(3, posteInfo.getEtat().name());
                if (posteInfo.getUtilisateurAffecte() != null) {
                    posteStmt.setInt(4, posteInfo.getUtilisateurAffecte().getId());
                } else {
                    posteStmt.setNull(4, java.sql.Types.INTEGER);
                }
                posteStmt.executeUpdate();
                posteStmt.close();

                conn.commit(); // Valider la transaction
                conn.close();

            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de l'insertion ou de la mise à jour du poste", e);
            }

            return posteInfo;
        }

    }


    @Override
    public List<PosteInformatique> AfficherPostes() {
        if(datasourceType.equals("json")) {
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
        else {
            List<PosteInformatique> postes = new ArrayList<>();
            Connection conn = null;

            try {
                conn = dataSource.getConnection();

                // Requête pour récupérer les informations des postes informatiques avec leurs utilisateurs affectés
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT p.id AS poste_id, p.configuration AS poste_config, p.etat AS poste_etat, " +
                                "ua.id AS utilisateur_affecte_id, ua.nom AS utilisateur_affecte_nom, ua.email AS utilisateur_affecte_email, " +
                                "ua.mdp AS utilisateur_affecte_mdp, ua.date_derniere_cnx AS utilisateur_affecte_date_derniere_cnx, " +
                                "ua.statut_actif AS utilisateur_affecte_statut_actif, ua.role AS utilisateur_affecte_role " +
                                "FROM poste_info p " +
                                "LEFT JOIN utilisateur ua ON p.utilisateur_affecte = ua.id"
                );

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    // Construire le PosteInformatique
                    PosteInformatique poste = new PosteInformatique();
                    poste.setId(rs.getInt("poste_id"));
                    poste.setConfiguration(rs.getString("poste_config"));
                    poste.setEtat(etatPoste.valueOf(rs.getString("poste_etat")));

                    // Construire l'utilisateur affecté si disponible
                    int utilisateurAffecteId = rs.getInt("utilisateur_affecte_id");
                    if (!rs.wasNull()) {
                        Utilisateur utilisateurAffecte = new Utilisateur();
                        utilisateurAffecte.setId(utilisateurAffecteId);
                        utilisateurAffecte.setNom(rs.getString("utilisateur_affecte_nom"));
                        utilisateurAffecte.setEmail(rs.getString("utilisateur_affecte_email"));
                        utilisateurAffecte.setMdp(rs.getString("utilisateur_affecte_mdp"));
                        utilisateurAffecte.setDateDerniereCnx(rs.getString("utilisateur_affecte_date_derniere_cnx"));
                        utilisateurAffecte.setStatutActif(rs.getBoolean("utilisateur_affecte_statut_actif"));
                        utilisateurAffecte.setRole(Role.valueOf(rs.getString("utilisateur_affecte_role")));

                        poste.setUtilisateurAffecte(utilisateurAffecte);
                    }

                    // Ajouter le poste à la liste
                    postes.add(poste);
                }

                stmt.close();
                conn.close();

            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la récupération des postes informatiques", e);
            }

            return postes;
        }

    }


    @Override
    public PosteInformatique AfficherUnPoste(PosteInformatique posteParam) {
        if(datasourceType.equals("json")) {
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
        else {
            PosteInformatique poste = null;
            Connection conn = null;
            try {
                conn = dataSource.getConnection();

                // Requête pour récupérer les informations d'un poste informatique avec l'utilisateur affecté
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT p.id AS poste_id, p.configuration AS poste_config, p.etat AS poste_etat, " +
                                "ua.id AS utilisateur_affecte_id, ua.nom AS utilisateur_affecte_nom, ua.email AS utilisateur_affecte_email, " +
                                "ua.mdp AS utilisateur_affecte_mdp, ua.date_derniere_cnx AS utilisateur_affecte_date_derniere_cnx, " +
                                "ua.statut_actif AS utilisateur_affecte_statut_actif, ua.role AS utilisateur_affecte_role " +
                                "FROM poste_info p " +
                                "LEFT JOIN utilisateur ua ON p.utilisateur_affecte = ua.id " +
                                "WHERE p.id = ?"
                );

                stmt.setInt(1, posteParam.getId());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Construire le PosteInformatique
                    poste = new PosteInformatique();
                    poste.setId(rs.getInt("poste_id"));
                    poste.setConfiguration(rs.getString("poste_config"));
                    poste.setEtat(etatPoste.valueOf(rs.getString("poste_etat")));

                    // Construire l'utilisateur affecté si disponible
                    if (rs.getObject("utilisateur_affecte_id") != null) {
                        Utilisateur utilisateurAffecte = new Utilisateur();
                        utilisateurAffecte.setId(rs.getInt("utilisateur_affecte_id"));
                        utilisateurAffecte.setNom(rs.getString("utilisateur_affecte_nom"));
                        utilisateurAffecte.setEmail(rs.getString("utilisateur_affecte_email"));
                        utilisateurAffecte.setMdp(rs.getString("utilisateur_affecte_mdp"));
                        utilisateurAffecte.setDateDerniereCnx(rs.getString("utilisateur_affecte_date_derniere_cnx"));
                        utilisateurAffecte.setStatutActif(rs.getBoolean("utilisateur_affecte_statut_actif"));
                        utilisateurAffecte.setRole(Role.valueOf(rs.getString("utilisateur_affecte_role")));

                        poste.setUtilisateurAffecte(utilisateurAffecte);
                    }
                }

                stmt.close();
                conn.close();

            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la récupération des informations du poste informatique", e);
            }

            return poste;
        }

    }

    @Override
    public PosteInformatique ModifierPoste(PosteInformatique posteParam) {
        if (datasourceType.equals("json")) {
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
        else {
            if (posteParam == null || posteParam.getId() == 0) {
                System.err.println("Erreur : Le poste passé en paramètre est null ou ne contient pas d'ID valide.");
                return null;
            }
            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                conn.setAutoCommit(false); // Démarre une transaction

                // Mise à jour de l'utilisateur affecté s'il existe
                if (posteParam.getUtilisateurAffecte() != null) {
                    Utilisateur utilisateurAffecte = posteParam.getUtilisateurAffecte();
                    PreparedStatement userAffecteStmt = conn.prepareStatement(
                            "UPDATE utilisateur SET nom = ?, email = ?, mdp = ?, date_derniere_cnx = ?, statut_actif = ?, role = ? WHERE id = ?"
                    );
                    userAffecteStmt.setString(1, utilisateurAffecte.getNom());
                    userAffecteStmt.setString(2, utilisateurAffecte.getEmail());
                    userAffecteStmt.setString(3, utilisateurAffecte.getMdp());
                    userAffecteStmt.setString(4, utilisateurAffecte.getDateDerniereCnx());
                    userAffecteStmt.setBoolean(5, utilisateurAffecte.isStatutActif());
                    userAffecteStmt.setString(6, utilisateurAffecte.getRole().name());
                    userAffecteStmt.setInt(7, utilisateurAffecte.getId());
                    userAffecteStmt.executeUpdate();
                    userAffecteStmt.close();
                }

                // Mise à jour du poste informatique
                PreparedStatement posteStmt = conn.prepareStatement(
                        "UPDATE poste_info SET configuration = ?, etat = ?, utilisateur_affecte = ? WHERE id = ?"
                );
                posteStmt.setString(1, posteParam.getConfiguration());
                posteStmt.setString(2, posteParam.getEtat().name());
                if (posteParam.getUtilisateurAffecte() != null) {
                    posteStmt.setInt(3, posteParam.getUtilisateurAffecte().getId());
                } else {
                    posteStmt.setNull(3, java.sql.Types.INTEGER);
                }
                posteStmt.setInt(4, posteParam.getId());

                int lignesModifiees = posteStmt.executeUpdate();

                if (lignesModifiees > 0) {
                    System.out.println("Poste mis à jour avec succès dans la base de données.");
                } else {
                    System.err.println("Erreur : Aucun poste mis à jour. ID non trouvé.");
                    conn.rollback();
                    return null;
                }

                posteStmt.close();
                conn.commit(); // Valider la transaction
                conn.close();

            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la mise à jour du poste", e);
            }

            return posteParam; // Retourne le poste mis à jour
        }

    }


    @Override
    public boolean SupprimerPoste(PosteInformatique posteParam) {
        if (datasourceType.equals("json")) {
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
        else {
            if (posteParam == null || posteParam.getId() == 0) {
                System.err.println("Erreur : Le poste passé en paramètre est null ou ne contient pas d'ID.");
                return false;
            }

            Connection conn = null;
            try {
                conn = dataSource.getConnection();

                // Supprimer uniquement le poste
                PreparedStatement posteStmt = conn.prepareStatement(
                        "DELETE FROM poste_info WHERE id = ?"
                );
                posteStmt.setInt(1, posteParam.getId());

                int lignesSupprimees = posteStmt.executeUpdate();

                posteStmt.close();
                conn.close();

                if (lignesSupprimees > 0) {
                    System.out.println("Poste supprimé avec succès de la base de données.");
                    return true;
                } else {
                    System.err.println("Erreur : Aucun poste trouvé pour l'ID : " + posteParam.getId());
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la suppression du poste dans la base de données", e);
            }
        }


    }

}