package fr.isen.ticketapp.interfaces.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.isen.ticketapp.interfaces.models.enums.Role;

public class Utilisateur {
    private Integer id;
    private String nom;
    private String email;
    private String mdp;

    @JsonProperty("date_derniere_cnx")
    private String dateDerniereCnx;

    @JsonProperty("statut_actif")
    private boolean statutActif;

    private Role role;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getDateDerniereCnx() {
        return dateDerniereCnx;
    }

    public void setDateDerniereCnx(String dateDerniereCnx) {
        this.dateDerniereCnx = dateDerniereCnx;
    }

    public boolean isStatutActif() {
        return statutActif;
    }

    public void setStatutActif(boolean statutActif) {
        this.statutActif = statutActif;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
