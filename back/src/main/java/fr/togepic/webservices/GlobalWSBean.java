package fr.togepic.webservices;

import fr.togepic.entities.User;
import fr.togepic.interfaces.UserManager;
import fr.togepic.objects.UserDTO;
import fr.togepic.objects.UserLoginDTO;
import io.swagger.annotations.Api;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.Response;

@Stateless
@Api(value = "GlobalWSBean", description = "The REST API for global services")
public class GlobalWSBean implements GlobalWS {
    @EJB
    private UserManager sbUser;

    public Response login(String token, UserLoginDTO dto) {
        if (token == null && !dto.isValid())
            return Response.status(Response.Status.BAD_REQUEST).build();
        User user = null;
        if (dto.getEmail() == null || dto.getPassword() == null)
            user = sbUser.getUserByToken(token);
        else
            user = sbUser.getUserByLogs(dto.getEmail(), dto.getPassword());
        if (user != null) {
            sbUser.connectUser(user.getId());
            return Response.ok(UserDTO.toDTO(user)).build();
        }
        return Response.status(Response.Status.CONFLICT).build();
    }
}
