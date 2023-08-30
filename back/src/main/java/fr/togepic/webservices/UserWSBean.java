package fr.togepic.webservices;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.togepic.entities.*;
import fr.togepic.interfaces.*;
import fr.togepic.objects.*;
import fr.togepic.utils.Mail;
import io.swagger.annotations.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

@Stateless
@Api(value = "UserWSBean", description = "The REST API for user services")
public class UserWSBean implements UserWS {
    @EJB
    private UserManager sbUser;
    @EJB
    private VerificationManager sbVerificationToken;
    @EJB
    private GroupeManager sbGroupe;
    @EJB
    private InvitationManager sbInvitation;
    @EJB
    private MemberManager sbMember;

    @Override
    public Response create(UserCreateDTO dto) {
        if (!dto.isValid())
            return Response.status(Response.Status.BAD_REQUEST).build();
        else if (sbUser.getUserByEmail(dto.getEmail()) == null) {
            User user = sbUser.createUser(new User(dto.getEmail(), dto.getPassword(), dto.getName(),dto.getFCMToken(),
                    dto.getPicture() != null ? Base64.getDecoder().decode(dto.getPicture()) : null));
            sendToken(sbVerificationToken.createVerification(user));
            return Response.ok().build();
        }
        return Response.status(Response.Status.CONFLICT).build();
    }

    @Override
    public Response modify(String token, final long id, UserUpdateDTO dto) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User base = sbUser.getUserByToken(token);
        if (base == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = sbUser.getUser(id);
        if (user == null || (base.getId() != user.getId()))
            return Response.status(Response.Status.CONFLICT).build();
        boolean canSend = dto.getEmail() != null && !dto.getEmail().equals(user.getEmail());
        user = sbUser.updateUser(user.getId(), dto);
        // Send a verification token if a new email has been set
        if (canSend)
            sendToken(sbVerificationToken.createVerification(user));
        sbUser.connectUser(user.getId());
        return Response.ok(UserDTO.toDTO(user)).build();
    }

    @Override
    public Response modifyFCMToken(String token, final long id, UserUpdateFCMTokenDTO dto) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User base = sbUser.getUserByToken(token);
        if (base == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = sbUser.getUser(id);
        if (user == null || (base.getId() != user.getId()))
            return Response.status(Response.Status.CONFLICT).build();

        user.setFCMToken(dto.getFCM());
        sbUser.updateUserFCMToken(user);
        return Response.noContent().build();
    }

    @Override
    public Response retrieveAllUsers(String token, String name) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User base = sbUser.getUserByToken(token);
        if (base == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        List<UserMediumDTO> users = new ArrayList<>();
        List<User> allUsers = name == null || name.isEmpty() ? sbUser.getAll() : sbUser.getUserByName(name);
        for (User user : allUsers) {
            users.add(UserMediumDTO.toDTO(user));
        }
        return Response.ok(users).build();
    }

    @Override
    public Response retrieveUser(String token, long id) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        if (sbUser.getUserByToken(token) == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = sbUser.getUser(id);
        if (user == null)
            return Response.status(Response.Status.CONFLICT).build();

        return Response.ok(UserRetrieveDTO.toDTO(user)).build();
    }

    @Override
    public Response retrieveGroupe(String token, long id) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User base = sbUser.getUserByToken(token);
        if (base == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = sbUser.getUser(id);
        if (user == null || (base.getId() != user.getId()))
            return Response.status(Response.Status.CONFLICT).build();
        List<GroupeDTO> members = new ArrayList<>();
        for (Member member : user.getGroupes())
            members.add(GroupeDTO.toDTO(member));
        return Response.ok(members).build();
    }

    @Override
    public Response retrieveInvitationAll(String token, long id) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User base = sbUser.getUserByToken(token);
        if (base == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = sbUser.getUser(id);
        if (user == null || (base.getId() != user.getId()))
            return Response.status(Response.Status.CONFLICT).build();
        List<InvitationDTO> invitations = new ArrayList<>();
        for (Invitation invitation : sbInvitation.getInvitationsByUser(user))
            invitations.add(InvitationDTO.toDTOGroupe(invitation));
        return Response.ok(invitations).build();
    }

    @Override
    public Response request(String token, long uid, long gid) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User base = sbUser.getUserByToken(token);
        if (base == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = sbUser.getUser(uid);
        Groupe groupe = sbGroupe.getGroupe(gid);
        if (user == null || groupe == null || user.getId() != base.getId())
            return Response.status(Response.Status.CONFLICT).build();
        else if (sbGroupe.getRole(gid, user) != null)
            return Response.status(Response.Status.FORBIDDEN).build();
        if (!groupe.getIsPrivate()) {
            Invitation invitation = sbInvitation.getInvitationByUserGroupe(user, groupe);
            if (invitation != null)
                sbInvitation.deleteInvitation(invitation.getId());
            sbGroupe.addUser(gid, user);
        } else {
            Invitation invitation = sbInvitation.createInvitation(user, groupe, false);
            if (invitation == null)
                sbGroupe.addUser(gid, user);
        }
        return Response.ok().build();
    }

    @Override
    public Response remove(String token, long uid, long gid) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User base = sbUser.getUserByToken(token);
        if (base == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = sbUser.getUser(uid);
        Groupe groupe = sbGroupe.getGroupe(gid);
        if (user == null || groupe == null || (base.getId() != user.getId()))
            return Response.status(Response.Status.CONFLICT).build();
        Role role = sbGroupe.getRole(gid, user);
        if (role != null) {
            sbUser.leaveGroupe(user.getId(), groupe);
            Member member = sbMember.getMemberByUserGroupe(user, groupe);
            if (member != null)
                sbMember.deleteMember(member.getId());
        } else {
            Invitation invitation = sbInvitation.getInvitationByUserGroupe(user, groupe);
            if (invitation != null)
                sbInvitation.deleteInvitation(invitation.getId());
        }
        return Response.ok().build();
    }

    @Override
    public Response delete(String token, final long id) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User base = sbUser.getUserByToken(token);
        if (base == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = sbUser.getUser(id);
        if (user == null || (base.getId() != user.getId()))
            return Response.status(Response.Status.CONFLICT).build();

        sbUser.deleteUser(user.getId());
        return Response.ok().build();
    }

    @Override
    public Response verify(String token) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        Verification verification = sbVerificationToken.getVerificationByToken(token);
        if (verification != null) {
            User user = verification.getUser();
            if (!user.getEmailVerified() && verification.getExpiration()
                    .compareTo(new Timestamp(new Date(System.currentTimeMillis()).getTime())) > 0) {
                sbUser.verifyUser(user.getId());
                sbVerificationToken.deleteVerification(verification.getId());
                sbUser.pushToken(user.getId());
                return Response.ok("<html><body><h1>Email verified</h1></body></html>", MediaType.TEXT_HTML).build();
            }
        }
        return Response.status(Response.Status.CONFLICT).build();
    }

    @Override
    public Response newToken(String token) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User user = sbUser.getUserByToken(token);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if (!user.getEmailVerified()) {
            Verification verification = sbVerificationToken.getVerificationByUser(user.getId());
            if (verification != null) {
                if (verification.getExpiration()
                        .compareTo(new Timestamp(new Date(System.currentTimeMillis()).getTime())) < 0) {
                    sbVerificationToken.pushVerification(verification.getId());
                    sendToken(sbVerificationToken.getVerificationByUser(user.getId()));
                }
            } else {
                sendToken(sbVerificationToken.createVerification(user));
            }
            return Response.noContent().build();
        }
        return Response.status(Response.Status.CONFLICT).build();
    }

    private void sendToken(Verification verification) {
        if (verification == null)
            return;
        Properties props = new Properties();
        try {
            props.load(UserWSBean.class.getResourceAsStream("/resources.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String mail = props.getProperty("mail.template").replaceAll("\\{token}", verification.getToken());
        Mail.send("Vérification de votre adresse e-mail", mail, verification.getUser().getEmail());
    }
}
