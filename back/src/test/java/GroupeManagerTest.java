import fr.togepic.entities.Groupe;
import fr.togepic.entities.Member;
import fr.togepic.entities.Role;
import fr.togepic.entities.User;
import fr.togepic.interfaces.GroupeManager;
import fr.togepic.interfaces.UserManager;
import fr.togepic.objects.GroupeUpdateDTO;
import junit.framework.TestCase;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import java.util.List;
import java.util.Properties;

public class GroupeManagerTest extends TestCase {
    private EJBContainer ejbContainer;
    @EJB
    private UserManager userManager;
    @EJB
    private GroupeManager groupeManager;

    private static final String GROUPE_NAME_1 = "groupe1";
    private static final String GROUPE_NAME_2 = "groupe2";
    private static final String USER_NAME_1 = "user1";
    private static final String USER_NAME_2 = "user2";

    private Groupe groupe1;
    private Groupe groupe2;
    private User user1;
    private User user2;

    public void setUp() throws NamingException {
        Properties p = new Properties();
        p.put("togepicDB", "new://Resource?type=DataSource");
        ejbContainer = EJBContainer.createEJBContainer(p);
        ejbContainer.getContext().bind("inject", this);

        groupe1 = new Groupe();
        groupe1.setName(GROUPE_NAME_1);
        groupe1 = groupeManager.createGroupe(groupe1);

        groupe2 = new Groupe();
        groupe2.setName(GROUPE_NAME_2);
        groupe2 = groupeManager.createGroupe(groupe2);

        user1 = new User();
        user1.setName(USER_NAME_1);
        user1 = userManager.createUser(user1);

        user2 = new User();
        user2.setName(USER_NAME_2);
        user2 = userManager.createUser(user2);
    }

    public void tearDown() {
        if (ejbContainer != null) {
            ejbContainer.close();
        }
    }

    public void testGetGroupe() {
        Groupe groupe = groupeManager.getGroupe(groupe1.getId());
        assertEquals(GROUPE_NAME_1, groupe.getName());
    }

    public void testGetAll() {
        List<Groupe> groupes = groupeManager.getAll();
        assertEquals(2, groupes.size());
    }

    public void testGetGroupeByName() {
        List<Groupe> groupes = groupeManager.getGroupeByName("groupe");
        assertEquals(2, groupes.size());
    }

    public void testCreateGroupe() {
        Groupe groupe = new Groupe();
        groupe.setName("groupe3");
        groupe = groupeManager.createGroupe(groupe);

        assertNotNull(groupe.getId());
        assertEquals("groupe3", groupe.getName());
    }

    public void testUpdateGroupe() {
        GroupeUpdateDTO dto = new GroupeUpdateDTO();
        dto.setName("newName");

        Groupe updatedGroupe = groupeManager.updateGroupe(groupe1.getId(), dto);

        assertEquals("newName", updatedGroupe.getName());
    }

    public void testDeleteGroupe() {
        groupeManager.deleteGroupe(groupe1.getId());

        assertNull(groupeManager.getGroupe(groupe1.getId()));
    }

    public void testAddUser() {
        groupeManager.addUser(groupe1.getId(), user1);

        Role role = groupeManager.getRole(groupe1.getId(), user1);
        assertEquals(Role.READER, role);
    }

    public void testGetRole() {
        groupeManager.addUser(groupe1.getId(), user1);

        Role role = groupeManager.getRole(groupe1.getId(), user1);
        assertEquals(Role.READER, role);
    }

    public void testSetRole() {
        groupeManager.addUser(groupe1.getId(), user1);
        groupeManager.setRole(groupe1.getId(), user1, Role.EDITOR);

        Role role = groupeManager.getRole(groupe1.getId(), user1);
        assertEquals(Role.EDITOR, role);
    }

    public void testRemoveUser() {
        groupeManager.addUser(groupe1.getId(), user1);
        assertTrue(groupeManager.getGroupe(groupe1.getId()).getUsers().stream().map(Member::getUser).anyMatch(u -> u.getId() == user1.getId()));
        groupeManager.removeUser(groupe1.getId(), user1);
        assertFalse(groupeManager.getGroupe(groupe1.getId()).getUsers().stream().map(Member::getUser).anyMatch(u -> u.getId() == user1.getId()));
    }

    public void testSynchronizeGroupeState() {
        String newGroupeName = "Nouveau nom de groupe";
        groupe1.setName(newGroupeName);
        groupeManager.synchronizeGroupeState(groupe1);
        assertEquals(newGroupeName, groupeManager.getGroupe(groupe1.getId()).getName());
    }
}
