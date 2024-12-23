package fr.isen.ticketapp.interfaces.models;

import fr.isen.ticketapp.interfaces.models.enums.etatPoste;

public class PosteInformatique {
    private int id; // Assure-toi que l'attribut `id` est défini
    private String configuration;
    private Utilisateur utilisateurAffecte;
    private etatPoste etat;

    // Getter pour `id`
    public int getId() {
        return id;
    }

    // Setter pour `id`
    public void setId(int id) {
        this.id = id;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public Utilisateur getUtilisateurAffecte() {
        return utilisateurAffecte;
    }

    public void setUtilisateurAffecte(Utilisateur utilisateurAffecte) {
        this.utilisateurAffecte = utilisateurAffecte;
    }

    public etatPoste getEtat() {
        return etat;
    }

    public void setEtat(etatPoste etat) {
        this.etat = etat;
    }
}
