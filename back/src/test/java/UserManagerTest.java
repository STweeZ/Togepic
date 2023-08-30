import fr.togepic.entities.Groupe;
import fr.togepic.entities.User;
import fr.togepic.interfaces.GroupeManager;
import fr.togepic.interfaces.UserManager;
import fr.togepic.objects.UserUpdateDTO;
import junit.framework.TestCase;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import java.util.List;
import java.util.Properties;

public class UserManagerTest extends TestCase {
    private EJBContainer ejbContainer;
    @EJB
    private UserManager userManager;
    @EJB
    private GroupeManager groupeManager;

    private static final String TEST_USER_NAME = "testUser";
    private static final String TEST_USER_EMAIL = "testuser@example.com";
    private static final String TEST_USER_PASSWORD = "testPassword";

    public void setUp() throws NamingException {
        Properties p = new Properties();
        p.put("togepicDB", "new://Resource?type=DataSource");
        ejbContainer = EJBContainer.createEJBContainer(p);
        ejbContainer.getContext().bind("inject", this);

        User testUser = new User();
        testUser.setName(TEST_USER_NAME);
        testUser.setEmail(TEST_USER_EMAIL);
        testUser.setPassword(TEST_USER_PASSWORD);
        userManager.createUser(testUser);
    }

    public void tearDown() {
        if (ejbContainer != null) {
            ejbContainer.close();
        }
    }

    public void testGetAll() throws Exception {
        List<User> userList = userManager.getAll();
        assertEquals(1, userList.size());
        assertEquals(TEST_USER_NAME, userList.get(0).getName());
    }

    public void testGetUserByName() throws Exception {
        List<User> userList = userManager.getUserByName(TEST_USER_NAME);
        assertEquals(1, userList.size());
        assertEquals(TEST_USER_NAME, userList.get(0).getName());
    }

    public void testUpdateUser() {
        User testUser = userManager.getUserByEmail(TEST_USER_EMAIL);
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        String updatedEmail = "updatedEmail@example.com";
        updateDTO.setEmail(updatedEmail);
        userManager.updateUser(testUser.getId(), updateDTO);
        User updatedUser = userManager.getUserByEmail(updatedEmail);
        assertNotNull(updatedUser);
        assertEquals(updatedEmail, updatedUser.getEmail());
    }

    public void testUpdateUserFCMToken() {
        User testUser = userManager.getUserByEmail(TEST_USER_EMAIL);
        String testToken = "testToken";
        testUser.setFCMToken(testToken);
        userManager.updateUserFCMToken(testUser);
        User updatedUser = userManager.getUserByEmail(TEST_USER_EMAIL);
        assertNotNull(updatedUser);
        assertEquals(testToken, updatedUser.getFCMToken());
    }

    public void testDeleteUser() {
        User testUser = userManager.getUserByEmail(TEST_USER_EMAIL);
        userManager.deleteUser(testUser.getId());
        assertNull(userManager.getUserByEmail(TEST_USER_EMAIL));
    }

    public void testGetUserByLogs() {
        User testUser = userManager.getUserByLogs(TEST_USER_EMAIL, TEST_USER_PASSWORD);
        assertNotNull(testUser);
        assertEquals(TEST_USER_EMAIL, testUser.getEmail());
    }

    public void testGetUserByEmail() {
        User testUser = userManager.getUserByEmail(TEST_USER_EMAIL);
        assertNotNull(testUser);
        assertEquals(TEST_USER_EMAIL, testUser.getEmail());
    }

    public void testGetUserByToken() {
        User testUser = userManager.getUserByEmail(TEST_USER_EMAIL);
        String testToken = "testToken";
        testUser.setToken(testToken);
        userManager.updateUserFCMToken(testUser);
        User userByToken = userManager.getUserByToken("Bearer " + testToken);
        assertNotNull(userByToken);
        assertEquals(TEST_USER_EMAIL, userByToken.getEmail());
    }

    public void testVerifyUser() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setEmailVerified(false);
        user = userManager.createUser(user);

        assertFalse(user.getEmailVerified());
        userManager.verifyUser(user.getId());
        assertTrue(user.getEmailVerified());
    }

    public void testConnectUser() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setEmailVerified(true);
        user = userManager.createUser(user);

        assertNull(user.getLastConnexion());
        userManager.connectUser(user.getId());
        assertNotNull(user.getLastConnexion());
    }

    public void testPushToken() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setEmailVerified(true);
        user = userManager.createUser(user);

        assertNull(user.getToken());
        userManager.pushToken(user.getId());
        assertNotNull(user.getToken());
    }

    public void testJoinGroupe() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setEmailVerified(true);
        user = userManager.createUser(user);

        Groupe groupe = new Groupe();
        groupe.setName("testGroupe");
        groupe = groupeManager.createGroupe(groupe);

        userManager.joinGroupe(user.getId(), groupe);

        Groupe finalGroupe = groupe;
        assertTrue(userManager.getUser(user.getId()).getGroupes().stream().anyMatch(gu -> gu.getUser().getId() == finalGroupe.getId()));
        User finalUser = user;
        assertTrue(groupeManager.getGroupe(groupe.getId()).getUsers().stream().anyMatch(u -> u.getUser().getId() == finalUser.getId()));
    }

    public void testLeaveGroupe() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setEmailVerified(true);
        user = userManager.createUser(user);

        Groupe groupe = new Groupe();
        groupe.setName("testGroupe");
        groupe = groupeManager.createGroupe(groupe);

        userManager.joinGroupe(user.getId(), groupe);
        Groupe finalGroupe = groupe;
        assertTrue(userManager.getUser(user.getId()).getGroupes().stream().anyMatch(gu -> gu.getGroupe().getId() == finalGroupe.getId()));

        userManager.leaveGroupe(user.getId(), groupe);
        User finalUser = user;
        assertFalse(userManager.getUser(user.getId()).getGroupes().stream().anyMatch(u -> u.getId() == finalUser.getId()));
    }
}
