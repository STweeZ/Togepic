import fr.togepic.entities.*;
import fr.togepic.interfaces.GroupeManager;
import fr.togepic.interfaces.InvitationManager;
import fr.togepic.interfaces.UserManager;
import junit.framework.TestCase;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import java.util.List;
import java.util.Properties;

public class InvitationManagerTest extends TestCase {
    private EJBContainer ejbContainer;
    @EJB
    private UserManager userManager;
    @EJB
    private GroupeManager groupeManager;
    @EJB
    private InvitationManager invitationManager;

    private User user1;
    private User user2;
    private Groupe groupe;
    private Invitation invitation1;

    public void setUp() throws NamingException {
        Properties p = new Properties();
        p.put("togepicDB", "new://Resource?type=DataSource");
        ejbContainer = EJBContainer.createEJBContainer(p);
        ejbContainer.getContext().bind("inject", this);

        user1 = new User("john.doe@example.com", "password", "John Doe", null, null);
        user2 = new User("michel.admin@example.com", "password", "Michel Admin", null, null);
        groupe = new Groupe("Groupe 1", "", null, false);

        userManager.createUser(user1);
        userManager.createUser(user2);
        groupeManager.createGroupe(groupe);

        invitation1 = invitationManager.createInvitation(user1, groupe, true);
    }

    public void tearDown() {
        if (ejbContainer != null) {
            ejbContainer.close();
        }
    }

    public void testGetInvitation() {
        Invitation invitation = invitationManager.getInvitation(invitation1.getId());
        assertNotNull(invitation);
        assertEquals(invitation1.getId(), invitation.getId());
    }

    public void testGetInvitationsByUser() {
        List<Invitation> invitations = invitationManager.getInvitationsByUser(user1);
        assertNotNull(invitations);
        assertEquals(1, invitations.size());
        assertEquals(invitation1.getId(), invitations.get(0).getId());
    }

    public void testGetInvitationsByGroupe() {
        List<Invitation> invitations = invitationManager.getInvitationsByGroupe(groupe);
        assertNotNull(invitations);
        assertEquals(2, invitations.size());
    }

    public void testGetInvitationByUserGroupe() {
        Invitation invitation = invitationManager.getInvitationByUserGroupe(user1, groupe);
        assertNotNull(invitation);
        assertEquals(invitation1.getId(), invitation.getId());
    }

    public void testCreateInvitation() {
        Invitation newInvitation = invitationManager.createInvitation(user2, groupe, true);
        assertNotNull(newInvitation);
        assertEquals(user2.getId(), newInvitation.getUser().getId());
        assertEquals(groupe.getId(), newInvitation.getGroupe().getId());

        Invitation existingInvitation = invitationManager.createInvitation(user1, groupe, true);
        assertNull(existingInvitation);
    }

    public void testDeleteInvitation() {
        invitationManager.deleteInvitation(invitation1.getId());
        Invitation deletedInvitation = invitationManager.getInvitation(invitation1.getId());
        assertNull(deletedInvitation);
    }
}
