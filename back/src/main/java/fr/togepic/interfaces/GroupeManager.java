package fr.togepic.interfaces;

import fr.togepic.entities.Groupe;
import fr.togepic.entities.Role;
import fr.togepic.entities.User;
import fr.togepic.objects.GroupeUpdateDTO;

import java.util.List;

public interface GroupeManager {
    Groupe getGroupe(long id);

    List<Groupe> getAll();
    List<Groupe> getGroupeByName(String name);

    Groupe createGroupe(Groupe groupe);

    Groupe updateGroupe(long id, GroupeUpdateDTO dto);

    void deleteGroupe(long id);

    void addUser(long id, User user);

    Role getRole(long id, User user);

    void setRole(long id, User user, Role role);

    void removeUser(long id, User user);

    void synchronizeGroupeState(Groupe groupe);
}
