package fr.togepic.webservices;

import fr.togepic.objects.UserDTO;
import fr.togepic.objects.UserLoginDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public interface GlobalWS {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Login", notes = "Login with email and password or token", response = UserDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful login", response = UserDTO.class),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 409, message = "Login conflict")
    })
    public Response login(@HeaderParam("Authorization") String token, UserLoginDTO dto);
}
