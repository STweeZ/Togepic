package fr.togepic.entities;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@NamedQueries(value = {
        @NamedQuery(name = "fr.togepic.entities.Groupe.findAll", query = "select g from Groupe g"),
        @NamedQuery(name = "fr.togepic.entities.Groupe.findByName", query = "SELECT g FROM Groupe g where lower(g.name) like lower(:name)")
})
public class Groupe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id = 0L;

    @Version
    private long version;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "groupe")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "groupe")
    private List<Member> users = new ArrayList<>();

    private String name;
    private String description;

    @Lob
    private byte[] picture;

    private Timestamp creation;
    private boolean isPrivate;

    public Groupe() {
    }

    public Groupe(String name, String description, byte[] picture, boolean isPrivate) {
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.isPrivate = isPrivate;
        this.creation = new Timestamp(new Date(System.currentTimeMillis()).getTime());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getPicture() {
        return picture;
    }

    public Timestamp getCreation() {
        return creation;
    }

    public List<Member> getUsers() {
        return users;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public void setUsers(List<Member> users) {
        this.users = users;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public void addUser(User user, Role role) {
        Member member = new Member(user, this, role);
        users.add(member);
        user.addGroupe(member);
    }

    public void addInverse(Member member) {
        users.add(member);
    }

    public void removeUser(Member member) {
        users.remove(member);
    }

    public void removeInverse(User user) {
        for (Member member : users) {
            if (member.getUser().getId() == user.getId()) {
                user.removeGroupeInverse(member);
                users.remove(member);
                break;
            }
        }
    }

    public void addPost(Post post) {
        posts.add(post);
        post.setGroupe(this);
    }
}
