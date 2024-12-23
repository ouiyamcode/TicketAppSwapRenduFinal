package fr.isen.ticketapp.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.isen.ticketapp.interfaces.models.PosteInformatique;
import fr.isen.ticketapp.interfaces.models.Services.TicketService;
import fr.isen.ticketapp.interfaces.models.TicketModel;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import fr.isen.ticketapp.interfaces.models.Utilisateur;
import fr.isen.ticketapp.interfaces.models.enums.IMPACT;
import fr.isen.ticketapp.interfaces.models.enums.Role;
import fr.isen.ticketapp.interfaces.models.enums.etatPoste;
import fr.isen.ticketapp.interfaces.models.enums.etatTicket;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.inject.spi.CDI;

import java.util.ArrayList;


public class TicketServiceImpl implements TicketService{
    String datasourceType = "bdd"; // ou mettre "json" ici pour changer ("json" ou "bdd")

    AgroalDataSource dataSource = CDI.current().select(AgroalDataSource.class).get();

    @Override
    public TicketModel creerTicket(TicketModel Ticket) {
        if (datasourceType.equals("json")) {
            if (Ticket == null) {
                System.err.println("Erreur : Le ticket reçu est null.");
                return null; // Retourne null en cas de paramètre invalide
            }
            // Afficher les informations du ticket pour confirmation
            System.out.println("Ticket créé :");
            System.out.println("ID : " + Ticket.getId());
            System.out.println("Titre : " + Ticket.getTitre());
            System.out.println("Description : " + Ticket.getDescription());
            System.out.println("Date de création : " + Ticket.getDateHeureCreation());
            System.out.println("Date de mise à jour : " + Ticket.getDateHeureMiseAJour());
            System.out.println("Type de demande : " + Ticket.getTypeDemande());
            System.out.println("Impact : " + Ticket.getImpact());
            System.out.println("État du ticket : " + Ticket.getEtatTicket());
            System.out.println("Utilisateur créateur : " + Ticket.getUtilisateur_createur());
            System.out.println("Poste info : " + Ticket.getPoste_info());

            // Retourne le ticket
            return Ticket;
        } else {
            if (Ticket == null) {
                System.err.println("Erreur : Le ticket reçu est null.");
                return null; // Retourne null en cas de paramètre invalide
            }

            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                conn.setAutoCommit(false); // Démarre une transaction

                // Insertion ou mise à jour de l'utilisateur créateur
                if (Ticket.getUtilisateur_createur() != null) {
                    Utilisateur utilisateur = Ticket.getUtilisateur_createur();
                    PreparedStatement userStmt = conn.prepareStatement(
                            "INSERT INTO utilisateur (id, nom, email, mdp, date_derniere_cnx, statut_actif, role) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                                    "ON DUPLICATE KEY UPDATE nom = VALUES(nom), email = VALUES(email), " +
                                    "mdp = VALUES(mdp), date_derniere_cnx = VALUES(date_derniere_cnx), " +
                                    "statut_actif = VALUES(statut_actif), role = VALUES(role)",
                            PreparedStatement.RETURN_GENERATED_KEYS
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
                }

                // Insertion ou mise à jour du poste informatique
                if (Ticket.getPoste_info() != null) {
                    PosteInformatique poste = Ticket.getPoste_info();
                    Utilisateur utilisateurAffecte = poste.getUtilisateurAffecte();

                    if (utilisateurAffecte != null) {
                        PreparedStatement userAffecteStmt = conn.prepareStatement(
                                "INSERT INTO utilisateur (id, nom, email, mdp, date_derniere_cnx, statut_actif, role) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                                        "ON DUPLICATE KEY UPDATE nom = VALUES(nom), email = VALUES(email), " +
                                        "mdp = VALUES(mdp), date_derniere_cnx = VALUES(date_derniere_cnx), " +
                                        "statut_actif = VALUES(statut_actif), role = VALUES(role)",
                                PreparedStatement.RETURN_GENERATED_KEYS
                        );
                        userAffecteStmt.setInt(1, utilisateurAffecte.getId());
                        userAffecteStmt.setString(2, utilisateurAffecte.getNom());
                        userAffecteStmt.setString(3, utilisateurAffecte.getEmail());
                        userAffecteStmt.setString(4, utilisateurAffecte.getMdp());
                        userAffecteStmt.setString(5, utilisateurAffecte.getDateDerniereCnx());
                        userAffecteStmt.setBoolean(6, utilisateurAffecte.isStatutActif());
                        userAffecteStmt.setString(7, utilisateurAffecte.getRole().name());
                        userAffecteStmt.executeUpdate();
                        userAffecteStmt.close();
                    }

                    PreparedStatement posteStmt = conn.prepareStatement(
                            "INSERT INTO poste_info (id, configuration, etat, utilisateur_affecte) " +
                                    "VALUES (?, ?, ?, ?) " +
                                    "ON DUPLICATE KEY UPDATE configuration = VALUES(configuration), " +
                                    "etat = VALUES(etat), utilisateur_affecte = VALUES(utilisateur_affecte)",
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );
                    posteStmt.setInt(1, poste.getId());
                    posteStmt.setString(2, poste.getConfiguration());
                    posteStmt.setString(3, poste.getEtat().name());
                    posteStmt.setInt(4, Ticket.getUtilisateur_createur().getId());
                    posteStmt.executeUpdate();
                    posteStmt.close();
                }

                // Insertion du ticket
                PreparedStatement ticketStmt = conn.prepareStatement(
                        "INSERT INTO ticket (titre, description, dateHeureCreation, dateHeureMiseAJour, typeDemande, impact, etatTicket, utilisateur_createur, poste_info) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        PreparedStatement.RETURN_GENERATED_KEYS
                );

                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateHeureCreation = outputFormat.format(inputFormat.parse(Ticket.getDateHeureCreation()));
                String dateHeureMiseAJour = outputFormat.format(inputFormat.parse(Ticket.getDateHeureMiseAJour()));

                ticketStmt.setString(1, Ticket.getTitre());
                ticketStmt.setString(2, Ticket.getDescription());
                ticketStmt.setString(3, dateHeureCreation);
                ticketStmt.setString(4, dateHeureMiseAJour);
                ticketStmt.setString(5, Ticket.getTypeDemande());
                ticketStmt.setInt(6, Ticket.getImpact().ordinal());
                ticketStmt.setString(7, Ticket.getEtatTicket().name());
                ticketStmt.setInt(8, Ticket.getUtilisateur_createur().getId());
                ticketStmt.setInt(9, Ticket.getPoste_info().getId());
                ticketStmt.executeUpdate();
                ticketStmt.close();

                conn.commit(); // Valider la transaction
                conn.close();

            } catch (SQLException | ParseException e) {
                throw new RuntimeException("Erreur lors de l'insertion du ticket ou des informations associées", e);
            }

            return Ticket;
        }
    }


    @Override
    public List<TicketModel> AfficherTickets() {
        if(datasourceType.equals("json")){
            List<TicketModel> tickets = new ArrayList<>();
            String filePath = "src/main/resources/ticket.json";

            try {
                // Utilisation de l'ObjectMapper pour mapper le JSON
                ObjectMapper mapper = new ObjectMapper();

                // Vérifier si le fichier JSON existe
                File jsonFile = new File(filePath);
                if (!jsonFile.exists()) {
                    System.err.println("Erreur : Le fichier JSON n'existe pas à l'emplacement : " + filePath);
                    return tickets; // Retourne une liste vide si le fichier n'existe pas
                }

                // Lire le fichier JSON en tant que noeud racine
                JsonNode rootNode = mapper.readTree(jsonFile);

                // Accéder au tableau "ticket" dans le fichier JSON
                JsonNode ticketArray = rootNode.get("ticket");

                if (ticketArray != null && ticketArray.isArray()) {
                    // Mapper chaque élément du tableau en objet TicketModel
                    for (JsonNode ticketNode : ticketArray) {
                        TicketModel ticket = mapper.treeToValue(ticketNode, TicketModel.class);
                        tickets.add(ticket);
                    }
                } else {
                    System.err.println("Erreur : Le fichier JSON ne contient pas de tableau 'ticket'.");
                }

                // Afficher les tickets pour vérification
                if (tickets.isEmpty()) {
                    System.out.println("Aucun ticket trouvé dans le fichier JSON.");
                } else {
                    tickets.forEach(ticket -> System.out.println("Ticket : " + ticket.getTitre()));
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la lecture des tickets depuis le fichier JSON : " + e.getMessage());
                e.printStackTrace();
            }

            return tickets;
        }
        else {
            List<TicketModel> tickets = new ArrayList<>();
            Connection conn = null;

            try {
                conn = dataSource.getConnection();

                // Requête avec INNER JOIN pour s'assurer que toutes les relations existent
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT t.id, t.titre, t.description, t.dateHeureCreation, t.dateHeureMiseAJour, t.typeDemande, t.impact, t.etatTicket, " +
                                "u.id AS utilisateur_id, u.nom AS utilisateur_nom, u.email AS utilisateur_email, u.mdp AS utilisateur_mdp, " +
                                "u.date_derniere_cnx, u.statut_actif, u.role, " +
                                "p.id AS poste_id, p.configuration AS poste_config, p.etat AS poste_etat, p.utilisateur_affecte AS poste_utilisateur_affecte, " +
                                "ua.id AS utilisateur_affecte_id, ua.nom AS utilisateur_affecte_nom, ua.email AS utilisateur_affecte_email, " +
                                "ua.mdp AS utilisateur_affecte_mdp, ua.date_derniere_cnx AS utilisateur_affecte_date_derniere_cnx, " +
                                "ua.statut_actif AS utilisateur_affecte_statut_actif, ua.role AS utilisateur_affecte_role " +
                                "FROM ticket t " +
                                "INNER JOIN utilisateur u ON t.utilisateur_createur = u.id " +
                                "INNER JOIN poste_info p ON t.poste_info = p.id " +
                                "INNER JOIN utilisateur ua ON p.utilisateur_affecte = ua.id"
                );

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    // Construire le TicketModel
                    TicketModel ticketModel = new TicketModel();
                    ticketModel.setId(rs.getInt("id"));
                    ticketModel.setTitre(rs.getString("titre"));
                    ticketModel.setDescription(rs.getString("description"));
                    ticketModel.setDateHeureCreation(rs.getString("dateHeureCreation"));
                    ticketModel.setDateHeureMiseAJour(rs.getString("dateHeureMiseAJour"));
                    ticketModel.setTypeDemande(rs.getString("typeDemande"));

                    int impactValue = rs.getInt("impact");
                    if (impactValue == 0) {
                        ticketModel.setImpact(IMPACT.BLOQUER);
                    } else if (impactValue == 1) {
                        ticketModel.setImpact(IMPACT.MAJEUR);
                    } else {
                        ticketModel.setImpact(IMPACT.MINEUR);
                    }

                    ticketModel.setEtatTicket(etatTicket.valueOf(rs.getString("etatTicket")));

                    // Construire l'Utilisateur créateur
                    Utilisateur utilisateurCreateur = new Utilisateur();
                    utilisateurCreateur.setId(rs.getInt("utilisateur_id"));
                    utilisateurCreateur.setNom(rs.getString("utilisateur_nom"));
                    utilisateurCreateur.setEmail(rs.getString("utilisateur_email"));
                    utilisateurCreateur.setMdp(rs.getString("utilisateur_mdp"));
                    utilisateurCreateur.setDateDerniereCnx(rs.getString("date_derniere_cnx"));
                    utilisateurCreateur.setStatutActif(rs.getBoolean("statut_actif"));
                    utilisateurCreateur.setRole(Role.valueOf(rs.getString("role")));

                    ticketModel.setUtilisateur_createur(utilisateurCreateur);

                    // Construire le PosteInformatique associé
                    PosteInformatique poste = new PosteInformatique();
                    poste.setId(rs.getInt("poste_id"));
                    poste.setConfiguration(rs.getString("poste_config"));
                    poste.setEtat(etatPoste.valueOf(rs.getString("poste_etat")));

                    // Construire l'utilisateur affecté au poste
                    Utilisateur utilisateurAffecte = new Utilisateur();
                    utilisateurAffecte.setId(rs.getInt("utilisateur_affecte_id"));
                    utilisateurAffecte.setNom(rs.getString("utilisateur_affecte_nom"));
                    utilisateurAffecte.setEmail(rs.getString("utilisateur_affecte_email"));
                    utilisateurAffecte.setMdp(rs.getString("utilisateur_affecte_mdp"));
                    utilisateurAffecte.setDateDerniereCnx(rs.getString("utilisateur_affecte_date_derniere_cnx"));
                    utilisateurAffecte.setStatutActif(rs.getBoolean("utilisateur_affecte_statut_actif"));
                    utilisateurAffecte.setRole(Role.valueOf(rs.getString("utilisateur_affecte_role")));

                    poste.setUtilisateurAffecte(utilisateurAffecte);

                    ticketModel.setPoste_info(poste);

                    // Ajouter le ticket à la liste
                    tickets.add(ticketModel);
                }

                stmt.close();
                conn.close();

            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la récupération des tickets avec leurs informations associées", e);
            }

            return tickets;
        }

    }

    @Override
    public TicketModel AfficherUnTicket(TicketModel ticketParam) {
        if(datasourceType.equals("json")){
            if (ticketParam == null || ticketParam.getId() == null) {
                System.err.println("Erreur : Le ticket passé en paramètre est null ou ne contient pas d'ID.");
                return null; // Retourne null si le paramètre est invalide
            }

            List<TicketModel> tickets = AfficherTickets(); // Charge les tickets depuis le JSON

            // Parcours des tickets pour trouver une correspondance
            for (TicketModel ticket : tickets) {
                if (ticket.getId().equals(ticketParam.getId())) {
                    return ticket; // Retourne le ticket correspondant
                }
            }

            // Si aucun ticket ne correspond, affiche un message d'erreur et retourne null
            System.err.println("Ticket non trouvé pour l'ID : " + ticketParam.getId());
            return null;
        }
        else {
            TicketModel ticketModel = null;
            Connection conn = null;
            try {
                conn = dataSource.getConnection();

                // Requête avec INNER JOIN pour récupérer les données liées
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT t.id, t.titre, t.description, t.dateHeureCreation, t.dateHeureMiseAJour, t.typeDemande, t.impact, t.etatTicket, " +
                                "u.id AS utilisateur_id, u.nom AS utilisateur_nom, u.email AS utilisateur_email, u.mdp AS utilisateur_mdp, " +
                                "u.date_derniere_cnx, u.statut_actif, u.role, " +
                                "p.id AS poste_id, p.configuration AS poste_config, p.etat AS poste_etat, p.utilisateur_affecte AS poste_utilisateur_affecte, " +
                                "ua.id AS utilisateur_affecte_id, ua.nom AS utilisateur_affecte_nom, ua.email AS utilisateur_affecte_email, " +
                                "ua.mdp AS utilisateur_affecte_mdp, ua.date_derniere_cnx AS utilisateur_affecte_date_derniere_cnx, " +
                                "ua.statut_actif AS utilisateur_affecte_statut_actif, ua.role AS utilisateur_affecte_role " +
                                "FROM ticket t " +
                                "INNER JOIN utilisateur u ON t.utilisateur_createur = u.id " +
                                "INNER JOIN poste_info p ON t.poste_info = p.id " +
                                "LEFT JOIN utilisateur ua ON p.utilisateur_affecte = ua.id " +
                                "WHERE t.id = ?"
                );

                stmt.setInt(1, ticketParam.getId());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    ticketModel = new TicketModel();

                    // Construire le TicketModel
                    ticketModel.setId(rs.getInt("id"));
                    ticketModel.setTitre(rs.getString("titre"));
                    ticketModel.setDescription(rs.getString("description"));
                    ticketModel.setDateHeureCreation(rs.getString("dateHeureCreation"));
                    ticketModel.setDateHeureMiseAJour(rs.getString("dateHeureMiseAJour"));
                    ticketModel.setTypeDemande(rs.getString("typeDemande"));

                    int impactValue = rs.getInt("impact");
                    if (impactValue == 0) {
                        ticketModel.setImpact(IMPACT.BLOQUER);
                    } else if (impactValue == 1) {
                        ticketModel.setImpact(IMPACT.MAJEUR);
                    } else {
                        ticketModel.setImpact(IMPACT.MINEUR);
                    }

                    ticketModel.setEtatTicket(etatTicket.valueOf(rs.getString("etatTicket")));

                    // Construire l'Utilisateur créateur
                    Utilisateur utilisateurCreateur = new Utilisateur();
                    utilisateurCreateur.setId(rs.getInt("utilisateur_id"));
                    utilisateurCreateur.setNom(rs.getString("utilisateur_nom"));
                    utilisateurCreateur.setEmail(rs.getString("utilisateur_email"));
                    utilisateurCreateur.setMdp(rs.getString("utilisateur_mdp"));
                    utilisateurCreateur.setDateDerniereCnx(rs.getString("date_derniere_cnx"));
                    utilisateurCreateur.setStatutActif(rs.getBoolean("statut_actif"));
                    utilisateurCreateur.setRole(Role.valueOf(rs.getString("role")));

                    ticketModel.setUtilisateur_createur(utilisateurCreateur);

                    // Construire le PosteInformatique associé
                    PosteInformatique poste = new PosteInformatique();
                    poste.setId(rs.getInt("poste_id"));
                    poste.setConfiguration(rs.getString("poste_config"));
                    poste.setEtat(etatPoste.valueOf(rs.getString("poste_etat")));

                    // Construire l'utilisateur affecté au poste
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

                    ticketModel.setPoste_info(poste);
                }

                stmt.close();
                conn.close();

            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la récupération du ticket avec ses informations associées", e);
            }

            return ticketModel;
        }
    }

    @Override
    public TicketModel ModifierTicket(TicketModel ticketParam) {
        if (datasourceType.equals("json")) {
            if (ticketParam == null || ticketParam.getId() == null) {
                System.err.println("Erreur : Le ticket passé en paramètre est null ou ne contient pas d'ID.");
                return null;
            }
            List<TicketModel> tickets = AfficherTickets();
            for (TicketModel ticket : tickets) {
                if (ticket.getId().equals(ticketParam.getId())) {
                    ticket = ticketParam;
                    return ticket;
                }
            }
            System.err.println("Ticket non trouvé pour l'ID : " + ticketParam.getId());
            return null;
        } else {
            if (ticketParam == null || ticketParam.getId() == null) {
                System.err.println("Erreur : Le ticket passé en paramètre est null ou ne contient pas d'ID.");
                return null;
            }

            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                conn.setAutoCommit(false); // Démarre une transaction

                // Mise à jour de l'utilisateur créateur
                if (ticketParam.getUtilisateur_createur() != null) {
                    Utilisateur utilisateur = ticketParam.getUtilisateur_createur();
                    PreparedStatement userStmt = conn.prepareStatement(
                            "UPDATE utilisateur SET nom = ?, email = ?, mdp = ?, date_derniere_cnx = ?, statut_actif = ?, role = ? WHERE id = ?"
                    );
                    userStmt.setString(1, utilisateur.getNom());
                    userStmt.setString(2, utilisateur.getEmail());
                    userStmt.setString(3, utilisateur.getMdp());
                    userStmt.setString(4, utilisateur.getDateDerniereCnx());
                    userStmt.setBoolean(5, utilisateur.isStatutActif());
                    userStmt.setString(6, utilisateur.getRole().name());
                    userStmt.setInt(7, utilisateur.getId());
                    userStmt.executeUpdate();
                    userStmt.close();
                }

                // Mise à jour ou ajout du poste informatique
                if (ticketParam.getPoste_info() != null) {
                    PosteInformatique poste = ticketParam.getPoste_info();
                    Utilisateur utilisateurAffecte = poste.getUtilisateurAffecte();

                    // Mise à jour de l'utilisateur affecté s'il existe
                    if (utilisateurAffecte != null) {
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
                    posteStmt.setString(1, poste.getConfiguration());
                    posteStmt.setString(2, poste.getEtat().name());
                    posteStmt.setInt(3, poste.getUtilisateurAffecte() != null ? poste.getUtilisateurAffecte().getId() : null);
                    posteStmt.setInt(4, poste.getId());
                    posteStmt.executeUpdate();
                    posteStmt.close();
                }

                // Mise à jour du ticket
                PreparedStatement ticketStmt = conn.prepareStatement(
                        "UPDATE ticket SET titre = ?, description = ?, dateHeureCreation = ?, dateHeureMiseAJour = ?, typeDemande = ?, impact = ?, etatTicket = ?, utilisateur_createur = ?, poste_info = ? WHERE id = ?"
                );

                String dateHeureCreation = ticketParam.getDateHeureCreation(); // Assurez-vous que c'est déjà au bon format
                String dateHeureMiseAJour = ticketParam.getDateHeureMiseAJour(); // Assurez-vous que c'est déjà au bon format

                ticketStmt.setString(1, ticketParam.getTitre());
                ticketStmt.setString(2, ticketParam.getDescription());
                ticketStmt.setString(3, dateHeureCreation);
                ticketStmt.setString(4, dateHeureMiseAJour);
                ticketStmt.setString(5, ticketParam.getTypeDemande());
                ticketStmt.setInt(6, ticketParam.getImpact().ordinal());
                ticketStmt.setString(7, ticketParam.getEtatTicket().name());
                ticketStmt.setInt(8, ticketParam.getUtilisateur_createur().getId());
                ticketStmt.setInt(9, ticketParam.getPoste_info().getId());
                ticketStmt.setInt(10, ticketParam.getId());

                int lignesModifiees = ticketStmt.executeUpdate();

                if (lignesModifiees > 0) {
                    System.out.println("Ticket mis à jour avec succès dans la base de données.");
                } else {
                    System.err.println("Erreur : Aucun ticket mis à jour. ID non trouvé.");
                    conn.rollback();
                    return null;
                }

                ticketStmt.close();
                conn.commit(); // Valider la transaction
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la mise à jour du ticket ou des informations associées", e);
            }

            return ticketParam; // Retourne le ticket mis à jour
        }
    }



    @Override
    public boolean SupprimerUnTicket(TicketModel ticketParam) {
        if (datasourceType.equals("json")) {
            if (ticketParam == null || ticketParam.getId() == null) {
                System.err.println("Erreur : Le ticket passé en paramètre est null ou ne contient pas d'ID.");
                return false;
            }
            List<TicketModel> tickets = AfficherTickets();
            for (TicketModel ticket : tickets) {
                if (ticket.getId().equals(ticketParam.getId())) {
                    tickets.remove(ticket);
                    return true;
                }
            }
            System.err.println("Ticket non trouvé pour l'ID : " + ticketParam.getId());
            return false;
        } else {
            if (ticketParam == null || ticketParam.getId() == null) {
                System.err.println("Erreur : Le ticket passé en paramètre est null ou ne contient pas d'ID.");
                return false;
            }

            Connection conn = null;
            try {
                conn = dataSource.getConnection();

                // Supprimer uniquement le ticket
                PreparedStatement ticketStmt = conn.prepareStatement(
                        "DELETE FROM ticket WHERE id = ?"
                );
                ticketStmt.setInt(1, ticketParam.getId());

                int lignesSupprimees = ticketStmt.executeUpdate();

                ticketStmt.close();
                conn.close();

                if (lignesSupprimees > 0) {
                    System.out.println("Ticket supprimé avec succès de la base de données.");
                    return true;
                } else {
                    System.err.println("Erreur : Aucun ticket trouvé pour l'ID : " + ticketParam.getId());
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la suppression du ticket dans la base de données", e);
            }
        }
    }

}
