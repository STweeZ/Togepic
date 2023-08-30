package fr.togepic.webservices;

import fr.togepic.objects.UserCreateDTO;
import fr.togepic.objects.UserUpdateDTO;
import fr.togepic.objects.UserUpdateFCMTokenDTO;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
public interface UserWS {

        @POST
        @Path("/")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Create a new user")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully created user"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 409, message = "Email already exists")
        })
        public Response create(
                        @Valid @ApiParam(value = "User details for creation", required = true) UserCreateDTO dto);

        @PUT
        @Path("/{id}")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Modify user information")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully modified user information"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 409, message = "User does not exist")
        })
        public Response modify(
                        @HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the user to modify", required = true) @PathParam("id") final long id,
                        @ApiParam(value = "User information for modification", required = true) UserUpdateDTO dto);

        @PUT
        @Path("/{id}/fcm")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Modify user fcm token")
        @ApiResponses(value = {
                @ApiResponse(code = 200, message = "Successfully modified user information"),
                @ApiResponse(code = 400, message = "Invalid input data"),
                @ApiResponse(code = 401, message = "Unauthorized"),
                @ApiResponse(code = 409, message = "User does not exist")
        })
        public Response modifyFCMToken(
                @HeaderParam("Authorization") String token,
                @ApiParam(value = "ID of the user to modify", required = true) @PathParam("id") final long id,
                @ApiParam(value = "User FCM token for modification", required = true) UserUpdateFCMTokenDTO dto);

        @GET
        @Path("/")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Get all users")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully retrieve users information"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized")
        })
        public Response retrieveAllUsers(@HeaderParam("Authorization") String token,
                        @ApiParam(value = "User name") @QueryParam(value = "name") String name);

        @GET
        @Path("/{id}")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Get user information")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully retrieve user information"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 409, message = "User does not exist")
        })
        public Response retrieveUser(@HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the user to retrieve", required = true) @PathParam("id") final long id);

        @GET
        @Path("/{id}/groupes")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Get user groupes")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully retrieve user groupes"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 409, message = "User does not exist")
        })
        public Response retrieveGroupe(@HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the user to retrieve groupes", required = true) @PathParam("id") final long id);

        @GET
        @Path("/{id}/invitations")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Get user invitation")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully retrieve user invitation"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 409, message = "User does not exist")
        })
        public Response retrieveInvitationAll(@HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the user to retrieve invitations", required = true) @PathParam("id") final long id);

        @POST
        @Path("/{uid}/groupes/{gid}")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "User request to a groupe")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully send request"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 403, message = "User already has a role"),
                        @ApiResponse(code = 409, message = "Groupe does not exist or user does not exist")
        })
        public Response request(@HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the user", required = true) @PathParam("uid") final long uid,
                        @ApiParam(value = "ID of the groupe", required = true) @PathParam("gid") final long gid);

        @DELETE
        @Path("/{uid}/groupes/{gid}")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Remove a groupe or an invitation")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully remove group or invitation"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 409, message = "Groupe does not exist or user does not exist")
        })
        public Response remove(@HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the user", required = true) @PathParam("uid") final long uid,
                        @ApiParam(value = "ID of the groupe to remove", required = true) @PathParam("gid") final long gid);

        @DELETE
        @Path("/{id}")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Delete user")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully delete user"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 409, message = "User does not exist")
        })
        public Response delete(@HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the user to delete", required = true) @PathParam("id") final long id);

        @GET
        @Path("/verify")
        @ApiOperation(value = "Verify email address")
        @ApiResponses(value = {
                        @ApiResponse(code = 204, message = "Successfully verified email address"),
                        @ApiResponse(code = 400, message = "Invalid token"),
                        @ApiResponse(code = 409, message = "Token has expired or email already verified")
        })
        public Response verify(
                        @ApiParam(value = "Verification token", required = true) @QueryParam(value = "token") String token);

        @POST
        @Path("/newToken")
        @ApiOperation(value = "Generate new verification token")
        @ApiResponses(value = {
                        @ApiResponse(code = 204, message = "Successfully generated new verification token"),
                        @ApiResponse(code = 400, message = "Invalid token"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 409, message = "Email already verified or user does not exist")
        })
        public Response newToken(@HeaderParam("Authorization") String token);
}
