package fr.togepic.objects;

import fr.togepic.entities.Groupe;
import fr.togepic.entities.Member;
import fr.togepic.entities.Post;
import fr.togepic.entities.Role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GroupeDTO implements Serializable {
    private long id;
    private String name;
    private String description;
    private String picture;
    private boolean isPrivate;
    private List<PostDTO> posts;
    private int nbUser;
    private Role role;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public Role getRole() {
        return role;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public List<PostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getNbUser() {
        return nbUser;
    }

    public void setNbUser(int nbUser) {
        this.nbUser = nbUser;
    }

    public static GroupeDTO toDTO(Groupe groupe) {
        GroupeDTO dto = new GroupeDTO();
        dto.id = groupe.getId();
        dto.name = groupe.getName();
        if (groupe.getPicture() != null)
            dto.picture = Base64.getEncoder().encodeToString(groupe.getPicture());
        dto.description = groupe.getDescription();
        dto.isPrivate = groupe.getIsPrivate();
        List<PostDTO> newPosts = new ArrayList<>();
        for (Post post : groupe.getPosts())
            newPosts.add(PostDTO.toDTO(post));
        dto.posts = newPosts;
        return dto;
    }

    public static GroupeDTO toDTO(Member member) {
        GroupeDTO dto = new GroupeDTO();
        dto.id = member.getGroupe().getId();
        dto.name = member.getGroupe().getName();
        dto.nbUser = member.getGroupe().getUsers().size();
        dto.description = member.getGroupe().getDescription();
        dto.role = member.getRole();
        if (member.getGroupe().getPicture() != null)
            dto.picture = Base64.getEncoder().encodeToString(member.getGroupe().getPicture());
        dto.isPrivate = member.getGroupe().getIsPrivate();
        List<Post> allPosts = member.getGroupe().getPosts();
        List<Post> lastPosts = allPosts.stream().filter(post -> post.getParent() == null && !post.getText().isEmpty())
                .sorted(Comparator.comparing(Post::getCreation).reversed()).limit(4).collect(Collectors.toList());
        List<PostDTO> newPosts = new ArrayList<>();
        for (Post post : lastPosts) {
            newPosts.add(PostDTO.toDTO(post));
        }
        dto.posts = newPosts;
        return dto;
    }
}
