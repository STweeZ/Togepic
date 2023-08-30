package fr.togepic.ejb;

import fr.togepic.entities.Groupe;
import fr.togepic.entities.Role;
import fr.togepic.entities.User;
import fr.togepic.interfaces.UserManager;
import fr.togepic.objects.UserUpdateDTO;
import fr.togepic.objects.UserUpdateFCMTokenDTO;
import fr.togepic.utils.Encryption;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Stateless
public class UserManagerBean extends ObjectManagerBean<User> implements UserManager {

    private static final String BEARER = "Bearer ";

    @Override
    public User getUser(final long id) {
        return super.read(User.class, id);
    }

    @Override
    public List<User> getAll() {
        Query query = em.createNamedQuery("fr.togepic.entities.User.findAll");
        return query.getResultList();
    }

    @Override
    public List<User> getUserByName(String name) {
        Query query = em.createNamedQuery("fr.togepic.entities.User.findByName");
        query.setParameter("name", name + "%");
        return query.getResultList();
    }

    @Override
    public User createUser(final User user) {
        return super.create(user);
    }

    @Override
    public User updateUser(final long id, final UserUpdateDTO dto) {
        User user = getUser(id);
        if (user != null) {
            user = super.update(UserUpdateDTO.toEntity(user, dto));
            if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail()))
                user.setEmailVerified(false);
        }
        return user;
    }

    @Override
    public User updateUserFCMToken(User user) {

        if (user != null) {
            user = super.update(user);
        }
        return user;
    }

    @Override
    public void deleteUser(final long id) {
        super.delete(getUser(id));
    }

    @Override
    public User getUserByLogs(final String email, final String password) {
        User user = getUserByEmail(email);
        if (user == null || !Encryption.verify(password, user.getPassword()))
            return null;
        return user;
    }

    @Override
    public User getUserByEmail(final String email) {
        Query query = em.createNamedQuery("fr.togepic.entities.User.findByEmail");
        query.setParameter("email", email);
        try {
            User user = (User) query.getSingleResult();
            if (user != null) {
                return user;
            }
        } catch (NoResultException ignored) {
        }
        return null;
    }

    @Override
    public User getUserByToken(final String token) {
        Query query = em.createNamedQuery("fr.togepic.entities.User.findByToken");
        if (token.startsWith(BEARER)) {
            query.setParameter("token", token.substring(BEARER.length()));
            try {
                User user = (User) query.getSingleResult();
                if (user != null && !user.isTokenExpired()) {
                    return user;
                }
            } catch (NoResultException ignored) {
            }
        }
        return null;
    }

    @Override
    public void verifyUser(final long id) {
        User user = getUser(id);
        user.verifyEmail();
    }

    @Override
    public void connectUser(final long id) {
        User user = getUser(id);
        user.setLastConnexion(new Timestamp(new Date(System.currentTimeMillis()).getTime()));
        pushToken(id);
    }

    @Override
    public void pushToken(final long id) {
        User user = getUser(id);
        user.setToken();
    }

    @Override
    public void joinGroupe(final long id, final Groupe groupe) {
        User managedUser = getUser(id);
        managedUser.addInverse(groupe, Role.READER);
    }

    @Override
    public void leaveGroupe(final long id, Groupe groupe) {
        User managedUser = getUser(id);
        managedUser.removeGroupe(groupe);
    }
}
