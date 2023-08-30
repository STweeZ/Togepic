package fr.togepic.ejb;

import fr.togepic.entities.*;
import fr.togepic.interfaces.MemberManager;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Stateless
public class MemberManagerBean extends ObjectManagerBean<Member> implements MemberManager {

    @Override
    public Member getMember(final long id) {
        return super.read(Member.class, id);
    }

    @Override
    public Member getMemberByUserGroupe(final User user, final Groupe groupe) {
        Query query = em.createNamedQuery("fr.togepic.entities.Member.findByUserGroupe");
        query.setParameter("uid", user.getId()).setParameter("gid", groupe.getId());
        try {
            Member member = (Member) query.getSingleResult();
            return member;
        } catch (NoResultException ignored) {
        }
        return null;
    }

    @Override
    public Member createMember(final User user, final Groupe groupe, Role role) {
        Member member = getMemberByUserGroupe(user, groupe);
        if (member == null)
            member = super.create(new Member(user, groupe, role));
        return member;
    }

    @Override
    public void deleteMember(final long id) {
        super.delete(getMember(id));
    }
}
