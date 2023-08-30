package fr.togepic.ejb;

import fr.togepic.entities.User;
import fr.togepic.entities.Verification;
import fr.togepic.interfaces.VerificationManager;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Stateless
public class VerificationManagerBean extends ObjectManagerBean<Verification> implements VerificationManager {

    @Override
    public Verification getVerification(final long id) {
        return super.read(Verification.class, id);
    }

    @Override
    public Verification createVerification(final User user) {
        Verification verification = null;
        if (!user.getEmailVerified()) {
            verification = user.getVerification();
            if (verification == null) {
                verification = new Verification(user);
                super.create(verification);
            } else
                verification.pushExpiration();
        }
        return verification;
    }

    @Override
    public Verification updateVerification(final long id, final Verification verification) {
        return super.update(verification);
    }

    @Override
    public void deleteVerification(final long id) {
        Verification verification = getVerification(id);
        if (verification != null && verification.getUser() != null)
            verification.getUser().setVerification(null);
        super.delete(verification);
    }

    @Override
    public Verification getVerificationByToken(final String token) {
        Query query = em.createNamedQuery("fr.togepic.entities.Verification.findByToken");
        query.setParameter("token", token);
        return getVerificationByField(query);
    }

    @Override
    public Verification getVerificationByUser(final long id) {
        Query query = em.createNamedQuery("fr.togepic.entities.Verification.findByUserId");
        query.setParameter("id", id);
        return getVerificationByField(query);
    }

    private Verification getVerificationByField(Query query) {
        try {
            Verification verification = (Verification) query.getSingleResult();
            if (verification != null)
                return verification;
        } catch (NoResultException e) {
        }
        return null;
    }

    public void pushVerification(final long id) {
        Verification verification = getVerification(id);
        verification.pushExpiration();
    }
}
