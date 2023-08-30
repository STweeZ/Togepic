package fr.togepic.objects;

import fr.togepic.entities.Post;

import java.io.Serializable;

public class PostLightDTO implements Serializable {

    long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static PostLightDTO toDTO(Post post) {
        PostLightDTO dto = new PostLightDTO();
        dto.id = post.getId();
        return dto;
    }
}
