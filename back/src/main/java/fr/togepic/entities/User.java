package fr.togepic.entities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import fr.togepic.utils.Encryption;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.*;

@Entity
@Table(name = "Utilisateur", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@NamedQueries(value = {
        @NamedQuery(name = "fr.togepic.entities.User.findByLogs", query = "SELECT u FROM User u where u.email = :email and u.password = :password"),
        @NamedQuery(name = "fr.togepic.entities.User.findByEmail", query = "SELECT u FROM User u where u.email = :email"),
        @NamedQuery(name = "fr.togepic.entities.User.findByName", query = "SELECT u FROM User u where lower(u.name) like lower(:name)"),
        @NamedQuery(name = "fr.togepic.entities.User.findByToken", query = "SELECT u FROM User u where u.token = :token"),
        @NamedQuery(name = "fr.togepic.entities.User.findAll", query = "select u from User u")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id = 0L;

    @Version
    private long version;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Member> groupes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Post> posts = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private Verification verification;

    private String email;
    private String password;
    private String name;
    private Timestamp registration;
    private Timestamp lastConnexion;
    private boolean emailVerified;

    @Lob
    private byte[] picture;

    private String description;

    private String token;

    private String FCMToken;

    public User() {
    }

    public User(String email, String password, String name,String FCMToken, byte[] picture) {
        this.email = email;
        this.password = Encryption.encrypt(password);
        this.name = name;
        this.picture = picture;
        this.FCMToken = FCMToken;
        this.registration = this.lastConnexion = new Timestamp(new Date(System.currentTimeMillis()).getTime());
        setToken();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Timestamp getRegistration() {
        return registration;
    }

    public Timestamp getLastConnexion() {
        return lastConnexion;
    }

    public boolean getEmailVerified() {
        return emailVerified;
    }

    public String getToken() {
        return token;
    }

    public String getFCMToken(){return FCMToken;}

    public byte[] getPicture() {
        return picture;
    }

    public String getDescription() {
        return description;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public List<Member> getGroupes() {
        return groupes;
    }

    public Verification getVerification() {
        return verification;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastConnexion(Timestamp timestamp) {
        this.lastConnexion = timestamp;
    }

    public void setEmailVerified(boolean value) {
        this.emailVerified = value;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setFCMToken(String FCMToken){ this.FCMToken = FCMToken;}

    public void setToken() {
        Properties props = new Properties();
        try {
            props.load(User.class.getResourceAsStream("/resources.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Timestamp expiration = new Timestamp(
                new Date(System.currentTimeMillis() + Long.parseLong(props.getProperty("user.expiration"))).getTime());
        Algorithm algorithm = Algorithm.HMAC256(props.getProperty("secret.key"));
        this.token = JWT.create().withClaim("userId", getId()).withClaim("email", getEmail()).withExpiresAt(expiration)
                .sign(algorithm);
    }

    public boolean isTokenExpired() {
        DecodedJWT jwt = JWT.decode(this.token);
        java.util.Date expirationDate = jwt.getExpiresAt();
        return expirationDate.before(new java.util.Date());
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public void setGroupes(List<Member> groupes) {
        this.groupes = groupes;
    }

    public void setVerification(Verification verification) {
        this.verification = verification;
    }

    public void verifyEmail() {
        setEmailVerified(true);
    }

    public void addGroupe(Member member) {
        groupes.add(member);
    }

    public void addInverse(Groupe groupe, Role role) {
        Member member = new Member(this, groupe, role);
        groupes.add(member);
        groupe.addInverse(member);
    }

    public void removeGroupe(Groupe groupe) {
        for (Member member : groupes) {
            if (member.getGroupe().getId() == groupe.getId()) {
                groupe.removeUser(member);
                groupes.remove(member);
                break;
            }
        }
    }

    public void removeGroupeInverse(Member member) {
        groupes.remove(member);
    }

    public void addPost(Post post) {
        posts.add(post);
        post.setUser(this);
    }
}
