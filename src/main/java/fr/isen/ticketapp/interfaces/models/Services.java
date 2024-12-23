package fr.isen.ticketapp.interfaces.models;

import jakarta.resource.spi.ConfigProperty;

import java.util.List;

public class Services {
    public interface TicketService {
        TicketModel creerTicket(final TicketModel Ticket);

        List<TicketModel> AfficherTickets();

        TicketModel AfficherUnTicket(final TicketModel Ticket);

        TicketModel ModifierTicket(final TicketModel Ticket);

        boolean SupprimerUnTicket(final TicketModel Ticket);

    }

    public interface PosteInfoService {
        PosteInformatique creerPoste(final PosteInformatique PosteInfo);
        
        List<PosteInformatique> AfficherPostes();

        PosteInformatique AfficherUnPoste(final PosteInformatique PosteInfo);

        PosteInformatique ModifierPoste(final PosteInformatique PosteInfo);

        boolean SupprimerPoste(final PosteInformatique PosteInfo);

    }

    public interface UtilisateurService {
        Utilisateur creerUtilisateur(final Utilisateur Utilisateur);

        Utilisateur AfficherUnUtilisateur(final Utilisateur Utilisateur);

        List<Utilisateur> AfficherUtilisateur();

        boolean SupprimerUtilisateur(final Utilisateur Utilisateur);

        Utilisateur ModifierUtilisateur(final Utilisateur Utilisateur);

    }

}
