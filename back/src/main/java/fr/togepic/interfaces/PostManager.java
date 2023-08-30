package fr.togepic.interfaces;

import fr.togepic.entities.Post;
import fr.togepic.objects.PostUpdateDTO;

import java.util.List;

public interface PostManager {
    Post getPost(long id);

    Post createPost(Post post);

    Post updatePost(long id, PostUpdateDTO dto);

    void deletePost(long id);

    List<Post> getPostsByUser(long id);

    List<Post> getPostsByGroupe(long id);

}
