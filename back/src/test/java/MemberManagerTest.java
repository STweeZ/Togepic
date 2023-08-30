import fr.togepic.entities.Groupe;
import fr.togepic.entities.Member;
import fr.togepic.entities.Role;
import fr.togepic.entities.User;
import fr.togepic.interfaces.GroupeManager;
import fr.togepic.interfaces.MemberManager;
import fr.togepic.interfaces.UserManager;
import junit.framework.TestCase;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import java.util.Properties;

public class MemberManagerTest extends TestCase {
    private EJBContainer ejbContainer;
    @EJB
    private UserManager userManager;
    @EJB
    private GroupeManager groupeManager;
    @EJB
    private MemberManager memberManager;

    private User user;
    private Groupe groupe;


    public void setUp() throws NamingException {
        Properties p = new Properties();
        p.put("togepicDB", "new://Resource?type=DataSource");
        ejbContainer = EJBContainer.createEJBContainer(p);
        ejbContainer.getContext().bind("inject", this);

        user = new User("john.doe@example.com", "password", "John Doe", null, null);
        userManager.createUser(user);

        groupe = new Groupe("Groupe test", "", null, false);
        groupeManager.createGroupe(groupe);
    }

    public void tearDown() {
        if (ejbContainer != null) {
            ejbContainer.close();
        }
    }

    public void testCreateMember() {
        Member member = memberManager.createMember(user, groupe, Role.READER);
        assertNotNull(member.getId());

        Member result = memberManager.getMember(member.getId());
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(groupe, result.getGroupe());
        assertEquals(Role.READER, result.getRole());
    }

    public void testCreateDuplicateMember() {
        memberManager.createMember(user, groupe, Role.READER);

        Member duplicate = memberManager.createMember(user, groupe, Role.ADMIN);
        assertNull(duplicate);
    }

    public void testGetMember() {
        Member member = memberManager.createMember(user, groupe, Role.READER);

        Member result = memberManager.getMember(member.getId());
        assertNotNull(result);
        assertEquals(member.getId(), result.getId());
    }

    public void testGetMemberByUserGroupe() {
        Member member = memberManager.createMember(user, groupe, Role.READER);

        Member result = memberManager.getMemberByUserGroupe(user, groupe);
        assertNotNull(result);
        assertEquals(member.getId(), result.getId());
    }

    public void testDeleteMember() {
        Member member = memberManager.createMember(user, groupe, Role.READER);

        memberManager.deleteMember(member.getId());
        Member result = memberManager.getMember(member.getId());
        assertNull(result);
    }
}
