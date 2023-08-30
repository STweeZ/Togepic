package fr.togepic.entities;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Properties;

import javax.persistence.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = "user_id") })
@NamedQueries(value = {
        @NamedQuery(name = "fr.togepic.entities.Verification.findByToken", query = "SELECT v FROM Verification v where v.token = :token"),
        @NamedQuery(name = "fr.togepic.entities.Verification.findByUserId", query = "SELECT v FROM Verification v where v.user.id = :id")
})
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id = 0L;

    @OneToOne
    private User user;

    private String token;
    private Timestamp expiration;

    public Verification() {
    }

    public Verification(User user) {
        this.user = user;
        user.setVerification(this);
        pushExpiration();
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public Timestamp getExpiration() {
        return expiration;
    }

    public void pushExpiration() {
        Properties props = new Properties();
        try {
            props.load(Verification.class.getResourceAsStream("/resources.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.expiration = new Timestamp(
                new Date(System.currentTimeMillis() + Long.parseLong(props.getProperty("verification.expiration")))
                        .getTime());
        Algorithm algorithm = Algorithm.HMAC256(props.getProperty("secret.key"));
        this.token = JWT.create().withClaim("userId", user.getId()).withClaim("email", user.getEmail())
                .withExpiresAt(getExpiration()).sign(algorithm);
    }
}
