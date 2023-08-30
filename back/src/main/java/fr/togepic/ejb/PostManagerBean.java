package fr.togepic.ejb;

import fr.togepic.entities.Post;
import fr.togepic.interfaces.PostManager;
import fr.togepic.objects.PostUpdateDTO;

import javax.ejb.Stateless;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class PostManagerBean extends ObjectManagerBean<Post> implements PostManager {

    @Override
    public Post getPost(final long id) {
        return super.read(Post.class, id);
    }

    @Override
    public Post createPost(final Post post) {
        return super.create(post);
    }

    @Override
    public Post updatePost(final long id, final PostUpdateDTO dto) {
        return super.update(PostUpdateDTO.toEntity(getPost(id), dto));
    }

    @Override
    public void deletePost(final long id) {
        super.delete(getPost(id));
    }

    @Override
    public List<Post> getPostsByUser(final long id) {
        return getPostByField(em.createNamedQuery("fr.togepic.entities.Post.findByUserId"), id);
    }

    @Override
    public List<Post> getPostsByGroupe(final long id) {
        return getPostByField(em.createNamedQuery("fr.togepic.entities.Post.findByGroupeId"), id);
    }

    private List getPostByField(final Query query, final long id) {
        query.setParameter("id", id);
        return query.getResultList();
    }
}
