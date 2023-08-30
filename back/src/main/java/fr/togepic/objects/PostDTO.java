package fr.togepic.objects;

import fr.togepic.entities.Picture;
import fr.togepic.entities.Post;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PostDTO implements Serializable {

    long id;
    PostLightDTO parent;
    List<PostLightDTO> childrens = new ArrayList<>();
    List<PictureLightDTO> pictures = new ArrayList<>();
    UserMediumDTO user;
    String text;
    String creation;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PostLightDTO getParent() {
        return parent;
    }

    public void setParent(PostLightDTO parent) {
        this.parent = parent;
    }

    public List<PostLightDTO> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<PostLightDTO> childrens) {
        this.childrens = childrens;
    }

    public List<PictureLightDTO> getPictures() {
        return pictures;
    }

    public void setPictures(List<PictureLightDTO> pictures) {
        this.pictures = pictures;
    }

    public UserMediumDTO getUser() {
        return user;
    }

    public void setUser(UserMediumDTO user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreation() {
        return creation;
    }

    public void setCreation(String creation) {
        this.creation = creation;
    }

    public static PostDTO toDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.id = post.getId();
        dto.parent = post.getParent() != null ? PostLightDTO.toDTO(post.getParent()) : null;
        for (Post childPost : post.getChildrens())
            dto.childrens.add(PostLightDTO.toDTO(childPost));
        for (Picture picture : post.getPictures())
            dto.pictures.add(PictureLightDTO.toDTO(picture));
        dto.user = UserMediumDTO.toDTO(post.getUser());
        dto.text = post.getText();
        dto.creation = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(post.getCreation());
        return dto;
    }
}
