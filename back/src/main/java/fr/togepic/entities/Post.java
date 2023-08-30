package fr.togepic.entities;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries(value = {
        @NamedQuery(name = "fr.togepic.entities.Post.findByUserId", query = "SELECT p FROM Post p where p.user.id = :id"),
        @NamedQuery(name = "fr.togepic.entities.Post.findByGroupeId", query = "SELECT p FROM Post p where p.groupe.id = :id")
})
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id = 0L;

    @ManyToOne
    private Post parent;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    private List<Post> childrens = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    private List<Picture> pictures = new ArrayList<>();

    @ManyToOne
    private User user;

    @ManyToOne
    private Groupe groupe;

    private String text;

    private Timestamp creation;

    public Post() {
    }

    public Post(Post parent, User user, Groupe groupe, String text) {
        this.text = text;
        this.parent = parent;
        this.groupe = groupe;
        this.user = user;
        if (this.parent != null) {
            this.parent.addChildren(this);
        }
        this.creation = new Timestamp(new Date(System.currentTimeMillis()).getTime());
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public Timestamp getCreation() {
        return creation;
    }

    public Post getParent() {
        return parent;
    }

    public List<Post> getChildrens() {
        return childrens;
    }

    public User getUser() {
        return user;
    }

    public Groupe getGroupe() {
        return groupe;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setChildrens(List<Post> childrens) {
        this.childrens = childrens;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addChildren(Post post) {
        childrens.add(post);
    }

    public void addPicture(Picture picture) {
        pictures.add(picture);
        picture.setPost(this);
    }

    public JsonArray getPicturesAsJsonArray() {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for(Picture p : getPictures()){
            builder.add(p.toJsonObject());
        }
        return  builder.build();
    }
}
