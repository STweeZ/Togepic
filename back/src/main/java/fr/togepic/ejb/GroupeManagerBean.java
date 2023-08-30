package fr.togepic.ejb;

import fr.togepic.entities.Groupe;
import fr.togepic.entities.Member;
import fr.togepic.entities.Role;
import fr.togepic.entities.User;
import fr.togepic.interfaces.GroupeManager;
import fr.togepic.objects.GroupeUpdateDTO;

import javax.ejb.Stateless;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class GroupeManagerBean extends ObjectManagerBean<Groupe> implements GroupeManager {

    @Override
    public Groupe getGroupe(final long id) {
        return super.read(Groupe.class, id);
    }

    @Override
    public List<Groupe> getAll() {
        Query query = em.createNamedQuery("fr.togepic.entities.Groupe.findAll");
        return query.getResultList();
    }

    @Override
    public List<Groupe> getGroupeByName(String name) {
        Query query = em.createNamedQuery("fr.togepic.entities.Groupe.findByName");
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }

    @Override
    public Groupe createGroupe(final Groupe groupe) {
        return super.create(groupe);
    }

    @Override
    public Groupe updateGroupe(final long id, final GroupeUpdateDTO dto) {
        return super.update(GroupeUpdateDTO.toEntity(getGroupe(id), dto));
    }

    @Override
    public void deleteGroupe(final long id) {
        super.delete(getGroupe(id));
    }

    @Override
    public void addUser(final long id, User user) {
        Groupe managedGroupe = getGroupe(id);
        managedGroupe.addUser(user, Role.READER);
    }

    @Override
    public Role getRole(final long id, User user) {
        Groupe managedGroupe = getGroupe(id);
        for (Member member : managedGroupe.getUsers()) {
            if (member.getUser().getId() == user.getId())
                return member.getRole();
        }
        return null;
    }

    @Override
    public void setRole(long id, User user, Role role) {
        Groupe managedGroupe = getGroupe(id);
        for (Member member : managedGroupe.getUsers()) {
            if (member.getUser().getId() == user.getId()) {
                member.setRole(role);
                break;
            }
        }
    }

    @Override
    public void removeUser(final long id, User user) {
        Groupe managedGroupe = getGroupe(id);
        managedGroupe.removeInverse(user);
    }

    @Override
    public void synchronizeGroupeState(Groupe groupe) {
        em.merge(groupe);
    }
}
