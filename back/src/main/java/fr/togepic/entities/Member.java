package fr.togepic.entities;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "groupe_id" }) })
@NamedQueries(value = {
        @NamedQuery(name = "fr.togepic.entities.Member.findByUserGroupe", query = "SELECT m FROM Member m where m.user.id = :uid and m.groupe.id = :gid"),
})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id = 0L;

    @ManyToOne
    private User user;

    @ManyToOne
    private Groupe groupe;

    private Role role;

    public Member() {
    }

    public Member(User user, Groupe groupe, Role role) {
        this.user = user;
        this.groupe = groupe;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Groupe getGroupe() {
        return groupe;
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
