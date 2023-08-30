package fr.togepic.entities;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "groupe_id" }) })
@NamedQueries(value = {
        @NamedQuery(name = "fr.togepic.entities.Invitation.findByUserGroupe", query = "SELECT i FROM Invitation i where i.user.id = :uid and i.groupe.id = :gid"),
        @NamedQuery(name = "fr.togepic.entities.Invitation.findByUser", query = "SELECT i FROM Invitation i where i.user.id = :uid and i.isRequest is true"),
        @NamedQuery(name = "fr.togepic.entities.Invitation.findByGroupe", query = "SELECT i FROM Invitation i where i.groupe.id = :gid and i.isRequest is false"),
})
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id = 0L;

    @ManyToOne
    private User user;

    @ManyToOne
    private Groupe groupe;

    private boolean isRequest;

    public Invitation() {
    }

    public Invitation(User user, Groupe groupe, boolean isRequest) {
        this.user = user;
        this.groupe = groupe;
        this.isRequest = isRequest;
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

    public boolean getIsRequest() {
        return isRequest;
    }

    public void setIsRequest(boolean isRequest) {
        this.isRequest = isRequest;
    }
}
