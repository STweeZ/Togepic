package fr.togepic.objects;

import fr.togepic.entities.Post;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;

import java.io.Serializable;

public class PostUpdateDTO implements Serializable {

    String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isValid() {
        return text != null;
    }

    private static Post toPost(PostUpdateDTO dto) {
        Post post = new Post();
        post.setText(dto.getText());
        return post;
    }

    public static Post toEntity(Post post, PostUpdateDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull()).setSkipNullEnabled(true);
        Post temp = toPost(dto);
        temp.setId(post.getId());
        temp.setChildrens(post.getChildrens());
        temp.setPictures(post.getPictures());
        modelMapper.map(temp, post);
        return post;
    }
}
