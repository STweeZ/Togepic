package fr.togepic.webservices;

import fr.togepic.entities.*;
import fr.togepic.interfaces.*;
import fr.togepic.objects.*;
import io.swagger.annotations.Api;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Stateless
@Api(value = "GroupeWSBean", description = "The REST API for groupe services")
public class GroupeWSBean implements GroupeWS {
    @EJB
    private GroupeManager sbGroupe;
    @EJB
    private UserManager sbUser;
    @EJB
    private InvitationManager sbInvitation;
    @EJB
    private MemberManager sbMember;
    @EJB
    private PostManager sbPost;


    private static final String PRIVATE_KEY_FCM="AAAAq1RSV6Q:APA91bGWEQm4sPXGW3gUyj36Zomqth6oz0O5fyxYz8vP770GS3ATSKpMOacztE6GOadvYgZgEdXZx--KFKLsEHVVIlRJBjJ-cZmRED45HThXoNaI7-tsRcOaaHOyG8K73Y_wvn66GncM";

    @Override
    public Response create(String token, GroupeCreateDTO groupeDto) {
        if (token == null || !groupeDto.isValid())
            return Response.status(Response.Status.BAD_REQUEST).build();
        User user = sbUser.getUserByToken(token);
        if (user == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        else if (!user.getEmailVerified())
            return Response.status(Response.Status.FORBIDDEN).build();
        Groupe groupe = new Groupe(groupeDto.getName(), groupeDto.getDescription(),
                groupeDto.getPicture() != null ? Base64.getDecoder().decode(groupeDto.getPicture()) : null,
                groupeDto.getIsPrivate());
        sbGroupe.createGroupe(groupe);
        groupe.addUser(user, Role.ADMIN);
        return Response.ok().build();
    }

    @Override
    public Response retrieveGroupeAll(String token, String name) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User user = sbUser.getUserByToken(token);
        if (user == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        List<GroupeRetrieveDTO> groupes = new ArrayList<>();
        List<Groupe> allGroupes = name == null || name.isEmpty() ? sbGroupe.getAll() : sbGroupe.getGroupeByName(name);
        for (Groupe groupe : allGroupes) {
            groupes.add(GroupeRetrieveDTO.toDTO(groupe));
        }
        return Response.ok(groupes).build();
    }

    @Override
    public Response modify(String token, long id, GroupeUpdateDTO groupeDto) {
        if (token == null || !groupeDto.isValid())
            return Response.status(Response.Status.BAD_REQUEST).build();
        User user = sbUser.getUserByToken(token);
        if (user == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        Groupe groupe = sbGroupe.getGroupe(id);
        if (groupe == null)
            return Response.status(Response.Status.CONFLICT).build();
        else if (sbGroupe.getRole(id, user) != Role.ADMIN)
            return Response.status(Response.Status.FORBIDDEN).build();
        groupe = sbGroupe.updateGroupe(id, groupeDto);
        return Response.ok(GroupeRetrieveDTO.toDTO(groupe)).build();
    }

    @Override
    public Response retrieveGroupe(String token, long id) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User user = sbUser.getUserByToken(token);
        if (user == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        Groupe groupe = sbGroupe.getGroupe(id);
        if (groupe == null)
            return Response.status(Response.Status.CONFLICT).build();
        if (sbGroupe.getRole(id, user) == null)
            return Response.ok(GroupeRetrieveDTO.toDTO(groupe)).build();
        GroupeDTO groupeDto = GroupeDTO.toDTO(groupe);
        groupeDto.setRole(sbGroupe.getRole(id, user));
        return Response.ok(groupeDto).build();
    }

    @Override
    public Response modify(String token, long gid, long uid, MemberLightDTO memberDto) {
        if (token == null || !memberDto.isValid())
            return Response.status(Response.Status.BAD_REQUEST).build();
        User base = sbUser.getUserByToken(token);
        if (base == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = sbUser.getUser(uid);
        Groupe groupe = sbGroupe.getGroupe(gid);
        if (user == null || groupe == null)
            return Response.status(Response.Status.CONFLICT).build();
        else if (sbGroupe.getRole(gid, base) != Role.ADMIN)
            return Response.status(Response.Status.FORBIDDEN).build();
        sbGroupe.setRole(gid, user, Role.valueOf(memberDto.getRole()));
        return Response.ok().build();
    }

    @Override
    public Response request(String token, long gid, long uid) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User base = sbUser.getUserByToken(token);
        if (base == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = sbUser.getUser(uid);
        Groupe groupe = sbGroupe.getGroupe(gid);
        if (user == null || groupe == null)
            return Response.status(Response.Status.CONFLICT).build();
        else if (sbGroupe.getRole(gid, base) != Role.ADMIN)
            return Response.status(Response.Status.FORBIDDEN).build();
        if (sbGroupe.getRole(gid, user) == null && sbInvitation.createInvitation(user, groupe, true) == null)
            sbGroupe.addUser(gid, user);
        return Response.ok().build();
    }

    @Override
    public Response delete(String token, long id) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User user = sbUser.getUserByToken(token);
        if (user == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        Groupe groupe = sbGroupe.getGroupe(id);
        if (groupe == null)
            return Response.status(Response.Status.CONFLICT).build();
        else if (sbGroupe.getRole(id, user) != Role.ADMIN)
            return Response.status(Response.Status.FORBIDDEN).build();
        sbGroupe.deleteGroupe(groupe.getId());
        return Response.ok().build();
    }

    @Override
    public Response retrieveUserAll(String token, long id) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User user = sbUser.getUserByToken(token);
        if (user == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        Groupe groupe = sbGroupe.getGroupe(id);
        if (groupe == null)
            return Response.status(Response.Status.CONFLICT).build();
        else if (sbGroupe.getRole(id, user) == null)
            return Response.status(Response.Status.FORBIDDEN).build();
        List<MemberDTO> members = new ArrayList<>();
        for (Member member : groupe.getUsers())
            members.add(MemberDTO.toDTO(member));
        return Response.ok(members).build();
    }

    @Override
    public Response retrieveInvitationAll(String token, long id) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User user = sbUser.getUserByToken(token);
        if (user == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        Groupe groupe = sbGroupe.getGroupe(id);
        if (groupe == null)
            return Response.status(Response.Status.CONFLICT).build();
        else if (sbGroupe.getRole(id, user) != Role.ADMIN)
            return Response.status(Response.Status.FORBIDDEN).build();

        List<InvitationDTO> invitations = new ArrayList<>();
        for (Invitation invitation : sbInvitation.getInvitationsByGroupe(groupe))
            invitations.add(InvitationDTO.toDTOUser(invitation));
        return Response.ok(invitations).build();
    }

    @Override
    public Response remove(String token, long gid, long uid) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User base = sbUser.getUserByToken(token);
        if (base == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = sbUser.getUser(uid);
        Groupe groupe = sbGroupe.getGroupe(gid);
        if (user == null || groupe == null)
            return Response.status(Response.Status.CONFLICT).build();
        else if (sbGroupe.getRole(gid, base) != Role.ADMIN)
            return Response.status(Response.Status.FORBIDDEN).build();
        Role role = sbGroupe.getRole(gid, user);
        if (role != null) {
            sbGroupe.removeUser(groupe.getId(), user);
            Member member = sbMember.getMemberByUserGroupe(user, groupe);
            if (member != null)
                sbMember.deleteMember(member.getId());
            if (sbGroupe.getGroupe(gid).getUsers().size() == 0)
                sbGroupe.deleteGroupe(groupe.getId());
        } else {
            Invitation invitation = sbInvitation.getInvitationByUserGroupe(user, groupe);
            if (invitation != null)
                sbInvitation.deleteInvitation(invitation.getId());
        }
        return Response.ok().build();
    }

    @Override
    public Response createPost(String token, long id, PostCreateDTO dto) {
        if (token == null || !dto.isValid())
            return Response.status(Response.Status.BAD_REQUEST).build();
        User user = sbUser.getUserByToken(token);
        Groupe groupe = sbGroupe.getGroupe(id);
        if (groupe == null)
            return Response.status(Response.Status.CONFLICT).build();
        else if (user == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        else if (sbGroupe.getRole(id, user) != Role.EDITOR && sbGroupe.getRole(id, user) != Role.ADMIN)
            return Response.status(Response.Status.FORBIDDEN).build();
        Post parent = null;
        if (dto.getParent() != null) {
            parent = sbPost.getPost(dto.getParent().getId());
            if (parent == null)
                return Response.status(Response.Status.CONFLICT).build();
        }
        Post post = sbPost.createPost(new Post(parent, user, groupe, dto.getText()));
        for (Picture picture : dto.transformPictures())
            post.addPicture(picture);
        user.addPost(post);
        groupe.addPost(post);

        for(Member m : groupe.getUsers()) {
            String FCM = m.getUser().getFCMToken();

            URL url = null;
            try {
                url = new URL("https://fcm.googleapis.com/fcm/send");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Authorization","key="+GroupeWSBean.PRIVATE_KEY_FCM);
                connection.setDoOutput(true);
                connection.setDoInput(true);


                JsonObject notif = Json.createObjectBuilder()
                        .add("title",groupe.getName())
                        .add("body",(dto.getText() == null || dto.getText().isEmpty()) ? "Photo recue" : dto.getText()).build();

                JsonObject body = Json.createObjectBuilder()
                        .add("to",FCM)
                        .add("notification", notif)
                        .build();

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(body.toString());
                writer.flush();
                writer.close();

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        return Response.ok().build();
    }

    @Override
    public Response retrievePictureAll(String token, long id) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User user = sbUser.getUserByToken(token);
        if (user == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        Groupe groupe = sbGroupe.getGroupe(id);
        if (groupe == null)
            return Response.status(Response.Status.CONFLICT).build();
        else if (sbGroupe.getRole(id, user) == null)
            return Response.status(Response.Status.FORBIDDEN).build();
        List<PictureDTO> pictures = new ArrayList<>();
        for (Post post : groupe.getPosts())
            for (Picture picture : post.getPictures())
                pictures.add(PictureDTO.toDTO(picture));
        return Response.ok(pictures).build();
    }

    @Override
    public Response modifyPost(String token, long gid, long pid, PostUpdateDTO dto) {
        if (token == null || !dto.isValid())
            return Response.status(Response.Status.BAD_REQUEST).build();
        User user = sbUser.getUserByToken(token);
        Groupe groupe = sbGroupe.getGroupe(gid);
        Post post = sbPost.getPost(pid);
        if (groupe == null || post == null || post.getGroupe().getId() != groupe.getId())
            return Response.status(Response.Status.CONFLICT).build();
        else if (user == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        else if (post.getUser().getId() != user.getId())
            return Response.status(Response.Status.FORBIDDEN).build();
        sbPost.updatePost(pid, dto);
        return Response.ok().build();
    }

    @Override
    public Response deletePost(String token, long gid, long pid) {
        if (token == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        User user = sbUser.getUserByToken(token);
        Groupe groupe = sbGroupe.getGroupe(gid);
        Post post = sbPost.getPost(pid);
        if (groupe == null || post == null || post.getGroupe().getId() != groupe.getId())
            return Response.status(Response.Status.CONFLICT).build();
        else if (user == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        else if (post.getUser().getId() != user.getId() && sbGroupe.getRole(gid, user) != Role.ADMIN)
            return Response.status(Response.Status.FORBIDDEN).build();
        sbPost.deletePost(pid);
        return Response.ok().build();
    }
}
