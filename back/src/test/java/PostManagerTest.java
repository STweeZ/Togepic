import fr.togepic.entities.*;
import fr.togepic.interfaces.GroupeManager;
import fr.togepic.interfaces.PostManager;
import fr.togepic.interfaces.UserManager;
import fr.togepic.objects.PostUpdateDTO;
import junit.framework.TestCase;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import java.util.List;
import java.util.Properties;

public class PostManagerTest extends TestCase {
    private EJBContainer ejbContainer;
    @EJB
    private UserManager userManager;
    @EJB
    private GroupeManager groupeManager;
    @EJB
    private PostManager postManager;

    public void setUp() throws NamingException {
        Properties p = new Properties();
        p.put("togepicDB", "new://Resource?type=DataSource");
        ejbContainer = EJBContainer.createEJBContainer(p);
        ejbContainer.getContext().bind("inject", this);
    }

    public void tearDown() {
        if (ejbContainer != null) {
            ejbContainer.close();
        }
    }

    public void testCreateAndGetPost() {
        User user = new User("john.doe@example.com", "password", "John Doe", null, null);
        Groupe groupe = new Groupe("Groupe test", "", null, false);
        Post post = new Post(null, user, groupe, "test post");

        Post createdPost = postManager.createPost(post);
        assertNotNull(createdPost.getId());

        Post retrievedPost = postManager.getPost(createdPost.getId());
        assertNotNull(retrievedPost);
        assertEquals(createdPost.getId(), retrievedPost.getId());
        assertEquals(post.getText(), retrievedPost.getText());
    }

    public void testUpdatePost() {
        User user = new User("john.doe@example.com", "password", "John Doe", null, null);
        Groupe groupe = new Groupe("Groupe test", "", null, false);
        Post post = new Post(null, user, groupe, "test post");

        Post createdPost = postManager.createPost(post);
        PostUpdateDTO postUpdateDTO = new PostUpdateDTO();
        postUpdateDTO.setText("updated test post");

        Post updatedPost = postManager.updatePost(createdPost.getId(), postUpdateDTO);
        assertEquals(createdPost.getId(), updatedPost.getId());
        assertEquals(postUpdateDTO.getText(), updatedPost.getText());
    }

    public void testDeletePost() {
        User user = new User("john.doe@example.com", "password", "John Doe", null, null);
        Groupe groupe = new Groupe("Groupe test", "", null, false);
        Post post = new Post(null, user, groupe, "test post");

        Post createdPost = postManager.createPost(post);
        assertNotNull(createdPost.getId());

        postManager.deletePost(createdPost.getId());
        assertNull(postManager.getPost(createdPost.getId()));
    }

    public void testGetPostsByUser() {
        User user = new User("john.doe@example.com", "password", "John Doe", null, null);
        Groupe groupe = new Groupe("Groupe test", "", null, false);
        Post post1 = new Post(null, user, groupe, "test post 1");
        Post post2 = new Post(null, user, groupe, "test post 2");

        postManager.createPost(post1);
        postManager.createPost(post2);

        List<Post> posts = postManager.getPostsByUser(user.getId());
        assertNotNull(posts);
        assertEquals(2, posts.size());
    }

    public void testGetPostsByGroupe() {
        User user = new User("john.doe@example.com", "password", "John Doe", null, null);
        Groupe groupe = new Groupe("Groupe test", "", null, false);
        Post post1 = new Post(null, user, groupe, "test post 1");
        Post post2 = new Post(null, user, groupe, "test post 2");

        postManager.createPost(post1);
        postManager.createPost(post2);

        List<Post> posts = postManager.getPostsByGroupe(groupe.getId());
        assertNotNull(posts);
        assertEquals(2, posts.size());
    }

    public void testGetPostByField() {
        User user = new User("john.doe@example.com", "password", "John Doe", null, null);
        Groupe groupe = new Groupe("Groupe test", "", null, false);
        Post post1 = new Post(null, user, groupe, "test post 1");
        Post post2 = new Post(null, user, groupe, "test post 2");

        postManager.createPost(post1);
        postManager.createPost(post2);

        List<Post> posts = postManager.getPostsByGroupe(groupe.getId());
        assertNotNull(posts);
        assertEquals(2, posts.size());
    }
}
