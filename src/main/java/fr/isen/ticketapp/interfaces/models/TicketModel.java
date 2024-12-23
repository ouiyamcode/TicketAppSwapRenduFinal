package fr.isen.ticketapp.interfaces.models;

import fr.isen.ticketapp.interfaces.models.enums.IMPACT;
import fr.isen.ticketapp.interfaces.models.enums.etatTicket;

public class TicketModel {
    private Integer id;
    private String titre;
    private String description;
    private String dateHeureCreation;
    private String dateHeureMiseAJour;
    private String typeDemande;
    private Utilisateur utilisateur_createur;
    private PosteInformatique poste_info;
    private IMPACT impact;
    private etatTicket etatTicket;

    // Constructeur par défaut
    public TicketModel() {}

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateHeureCreation() {
        return dateHeureCreation;
    }

    public void setDateHeureCreation(String dateHeureCreation) {
        this.dateHeureCreation = dateHeureCreation;
    }

    public String getDateHeureMiseAJour() {
        return dateHeureMiseAJour;
    }

    public void setDateHeureMiseAJour(String dateHeureMiseAJour) {
        this.dateHeureMiseAJour = dateHeureMiseAJour;
    }

    public String getTypeDemande() {
        return this.typeDemande;
    }

    public void setTypeDemande(String typeDemande) {
        this.typeDemande = typeDemande;
    }

    public Utilisateur getUtilisateur_createur() {
        return utilisateur_createur;
    }

    public void setUtilisateur_createur(Utilisateur utilisateur_createur) {
        this.utilisateur_createur = utilisateur_createur;
    }

    public PosteInformatique getPoste_info() {
        return poste_info;
    }

    public void setPoste_info(PosteInformatique poste_info) {
        this.poste_info = poste_info;
    }

    public IMPACT getImpact() {
        return impact;
    }

    public void setImpact(IMPACT impact) {
        this.impact = impact;
    }

    public etatTicket getEtatTicket() {
        return etatTicket;
    }

    public void setEtatTicket(etatTicket etatTicket) {
        this.etatTicket = etatTicket;
    }
}
