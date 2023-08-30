package fr.togepic.interfaces;

import fr.togepic.entities.User;
import fr.togepic.entities.Verification;

public interface VerificationManager {

    Verification getVerification(long id);

    Verification createVerification(User user);

    Verification updateVerification(long id, Verification verification);

    void deleteVerification(long id);

    Verification getVerificationByToken(String token);

    Verification getVerificationByUser(long id);

    void pushVerification(long id);
}
