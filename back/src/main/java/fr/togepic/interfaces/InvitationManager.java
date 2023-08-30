package fr.togepic.interfaces;

import fr.togepic.entities.Groupe;
import fr.togepic.entities.Invitation;
import fr.togepic.entities.User;

import java.util.List;

public interface InvitationManager {
    Invitation getInvitation(long id);

    List<Invitation> getInvitationsByUser(final User user);

    List<Invitation> getInvitationsByGroupe(final Groupe groupe);

    Invitation getInvitationByUserGroupe(final User user, final Groupe groupe);

    Invitation createInvitation(User user, Groupe groupe, boolean isRequest);

    void deleteInvitation(long id);
}
