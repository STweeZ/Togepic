import fr.togepic.entities.User;
import fr.togepic.entities.Verification;
import fr.togepic.interfaces.GroupeManager;
import fr.togepic.interfaces.UserManager;
import fr.togepic.interfaces.VerificationManager;
import fr.togepic.objects.UserUpdateDTO;
import junit.framework.TestCase;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import java.util.Date;
import java.util.Properties;

public class VerificationManagerTest extends TestCase {
    private EJBContainer ejbContainer;
    @EJB
    private UserManager userManager;
    @EJB
    private VerificationManager verificationManager;
    @EJB
    private GroupeManager groupeManager;

    private User user;

    public void setUp() throws Exception {
        Properties p = new Properties();
        p.put("togepicDB", "new://Resource?type=DataSource");
        ejbContainer = EJBContainer.createEJBContainer(p);
        ejbContainer.getContext().bind("inject", this);

        user = userManager.createUser(new User("john.doe@example.com", "password", "John Doe", null, null));
    }

    public void tearDown() throws Exception {
        if (ejbContainer != null) {
            ejbContainer.close();
        }
    }

    public void testCreateVerification() {
        Verification verification = verificationManager.createVerification(user);
        assertNotNull(verification);
        assertNotNull(verification.getToken());
        assertNotNull(verification.getExpiration());
        assertEquals(user, verification.getUser());
    }

    public void testCreateVerificationUserAlreadyVerified() {
        user.setEmailVerified(true);
        userManager.updateUser(user.getId(), UserUpdateDTO.toDTO(user));
        Verification verification = verificationManager.createVerification(user);
        assertNull(verification);
    }

    public void testGetVerification() {
        Verification verification = verificationManager.createVerification(user);
        Verification retrievedVerification = verificationManager.getVerification(verification.getId());
        assertNotNull(retrievedVerification);
        assertEquals(verification, retrievedVerification);
    }

    public void testUpdateVerification() {
        Verification verification = verificationManager.createVerification(user);
        Date newExpiration = new Date(System.currentTimeMillis() + 3600000);
        Verification updatedVerification = verificationManager.updateVerification(verification.getId(), verification);
        assertEquals(verification.getToken(), updatedVerification.getToken());
        assertEquals(newExpiration, updatedVerification.getExpiration());
    }

    public void testDeleteVerification() {
        Verification verification = verificationManager.createVerification(user);
        long id = verification.getId();
        verificationManager.deleteVerification(id);
        Verification deletedVerification = verificationManager.getVerification(id);
        assertNull(deletedVerification);
        assertNull(userManager.getUser(user.getId()).getVerification());
    }

    public void testGetVerificationByToken() {
        Verification verification = verificationManager.createVerification(user);
        Verification retrievedVerification = verificationManager.getVerificationByToken(verification.getToken());
        assertNotNull(retrievedVerification);
        assertEquals(verification, retrievedVerification);
    }

    public void testGetVerificationByUser() {
        Verification verification = verificationManager.createVerification(user);
        Verification retrievedVerification = verificationManager.getVerificationByUser(user.getId());
        assertNotNull(retrievedVerification);
        assertEquals(verification, retrievedVerification);
    }

    public void testPushVerification() throws InterruptedException {
        Verification verification = verificationManager.createVerification(user);
        Date originalExpiration = verification.getExpiration();
        Thread.sleep(1000);
        verificationManager.pushVerification(verification.getId());
        Verification updatedVerification = verificationManager.getVerification(verification.getId());
        assertNotNull(updatedVerification);
        assertNotNull(updatedVerification.getExpiration());
        assertEquals(originalExpiration.getTime() + 1000, updatedVerification.getExpiration().getTime());
    }
}
