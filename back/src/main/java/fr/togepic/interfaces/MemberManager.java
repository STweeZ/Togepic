package fr.togepic.interfaces;

import fr.togepic.entities.*;

public interface MemberManager {
    Member getMember(long id);

    Member getMemberByUserGroupe(final User user, final Groupe groupe);

    Member createMember(User user, Groupe groupe, Role Role);

    void deleteMember(long id);
}
