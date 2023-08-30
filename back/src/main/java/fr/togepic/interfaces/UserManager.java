package fr.togepic.interfaces;

import fr.togepic.entities.Groupe;
import fr.togepic.entities.User;
import fr.togepic.objects.UserUpdateDTO;
import fr.togepic.objects.UserUpdateFCMTokenDTO;

import java.util.List;

public interface UserManager {
    User getUser(long id);

    List<User> getAll();

    List<User> getUserByName(String name);

    User createUser(User user);

    User updateUser(long id, UserUpdateDTO dto);
    User updateUserFCMToken(User user);

    void deleteUser(long id);

    User getUserByLogs(String email, String password);

    User getUserByEmail(String email);

    User getUserByToken(String token);

    void verifyUser(long id);

    void connectUser(long id);

    void pushToken(long id);

    void joinGroupe(long id, Groupe groupe);

    void leaveGroupe(long id, Groupe groupe);

}
