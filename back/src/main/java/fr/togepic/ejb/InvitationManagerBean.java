package fr.togepic.ejb;

import fr.togepic.entities.*;
import fr.togepic.interfaces.InvitationManager;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class InvitationManagerBean extends ObjectManagerBean<Invitation> implements InvitationManager {

    @Override
    public Invitation getInvitation(final long id) {
        return super.read(Invitation.class, id);
    }

    @Override
    public List<Invitation> getInvitationsByUser(final User user) {
        Query query = em.createNamedQuery("fr.togepic.entities.Invitation.findByUser");
        query.setParameter("uid", user.getId());
        return query.getResultList();
    }

    @Override
    public List<Invitation> getInvitationsByGroupe(final Groupe groupe) {
        Query query = em.createNamedQuery("fr.togepic.entities.Invitation.findByGroupe");
        query.setParameter("gid", groupe.getId());
        return query.getResultList();
    }

    @Override
    public Invitation getInvitationByUserGroupe(final User user, final Groupe groupe) {
        Query query = em.createNamedQuery("fr.togepic.entities.Invitation.findByUserGroupe");
        query.setParameter("uid", user.getId()).setParameter("gid", groupe.getId());
        try {
            Invitation invitation = (Invitation) query.getSingleResult();
            return invitation;
        } catch (NoResultException ignored) {
        }
        return null;
    }

    @Override
    public Invitation createInvitation(final User user, final Groupe groupe, boolean isRequest) {
        Invitation invitation = getInvitationByUserGroupe(user, groupe);
        if (invitation == null)
            return super.create(new Invitation(user, groupe, isRequest));
        else if (invitation.getIsRequest() == !isRequest) {
            deleteInvitation(invitation.getId());
            return null;
        } else
            return invitation;
    }

    @Override
    public void deleteInvitation(final long id) {
        super.delete(getInvitation(id));
    }
}
