package fr.togepic.webservices;

import fr.togepic.objects.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/groupes")
public interface GroupeWS {

        @POST
        @Path("/")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Create a new groupe")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully created groupe"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 403, message = "Forbidden"),
        })
        public Response create(
                        @HeaderParam("Authorization") String token,
                        @Valid @ApiParam(value = "Groupe details for creation", required = true) GroupeCreateDTO groupeDto);

        @GET
        @Path("/")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Get all groupes")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully retrieved all groupes"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
        })
        public Response retrieveGroupeAll(@HeaderParam("Authorization") String token,
                        @ApiParam(value = "Groupe name") @QueryParam(value = "name") String name);

        @PUT
        @Path("/{id}")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Modify groupe information")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully modified groupe information"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 403, message = "Forbidden"),
                        @ApiResponse(code = 409, message = "User or groupe does not exist")
        })
        public Response modify(
                        @HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the groupe to modify", required = true) @PathParam("id") final long id,
                        @Valid @ApiParam(value = "Groupe information for modification", required = true) GroupeUpdateDTO groupeDto);

        @GET
        @Path("/{id}")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Get groupe information")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully retrieve groupe information"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 409, message = "Groupe does not exist")
        })
        public Response retrieveGroupe(
                        @HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the groupe to retrieve", required = true) @PathParam("id") final long id);

        @PUT
        @Path("/{gid}/users/{uid}")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Modify a user role in a groupe")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully modify user role"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 403, message = "Forbidden"),
                        @ApiResponse(code = 409, message = "Groupe does not exist or user does not exist")
        })
        public Response modify(
                        @HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the groupe", required = true) @PathParam("gid") final long gid,
                        @ApiParam(value = "ID of the user to modify", required = true) @PathParam("uid") final long uid,
                        @Valid @ApiParam(value = "Member information", required = true) MemberLightDTO memberDto);

        @POST
        @Path("/{gid}/users/{uid}")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Invite a user to a groupe")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully invite user"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 403, message = "Forbidden"),
                        @ApiResponse(code = 409, message = "Groupe does not exist or user does not exist")
        })
        public Response request(
                        @HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the groupe", required = true) @PathParam("gid") final long gid,
                        @ApiParam(value = "ID of the user to invite", required = true) @PathParam("uid") final long uid);

        @DELETE
        @Path("/{id}")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Delete groupe")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully delete groupe"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 403, message = "Forbidden"),
                        @ApiResponse(code = 409, message = "Groupe does not exist")
        })
        public Response delete(
                        @HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the groupe to delete", required = true) @PathParam("id") final long id);

        @GET
        @Path("/{id}/users")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Get user information from groupe")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully retrieve user information"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 403, message = "Forbidden"),
                        @ApiResponse(code = 409, message = "Groupe does not exist")
        })
        public Response retrieveUserAll(
                        @HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the groupe to retrieve users", required = true) @PathParam("id") final long id);

        @GET
        @Path("/{id}/invitations")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Get groupe invitation")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully retrieve groupe invitation"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 403, message = "Forbidden"),
                        @ApiResponse(code = 409, message = "Groupe does not exist")
        })
        public Response retrieveInvitationAll(
                        @HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the groupe to retrieve invitations", required = true) @PathParam("id") final long id);

        @DELETE
        @Path("/{gid}/users/{uid}")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Remove a user from a groupe or an invitation")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully remove user or invitation"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 403, message = "Forbidden"),
                        @ApiResponse(code = 409, message = "Groupe does not exist or user does not exist")
        })
        public Response remove(
                        @HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the groupe", required = true) @PathParam("gid") final long gid,
                        @ApiParam(value = "ID of the user to remove", required = true) @PathParam("uid") final long uid);

        @POST
        @Path("/{id}/posts")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Create a new post")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully created post"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 403, message = "Forbidden"),
                        @ApiResponse(code = 409, message = "Group does not exist")
        })
        public Response createPost(
                        @HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the groupe", required = true) @PathParam("id") final long id,
                        @Valid @ApiParam(value = "Post details for creation", required = true) PostCreateDTO dto);

        @GET
        @Path("/{id}/pictures")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Get groupe pictures")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully retrieve groupe pictures"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 403, message = "Forbidden"),
                        @ApiResponse(code = 409, message = "Groupe does not exist")
        })
        public Response retrievePictureAll(
                        @HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the groupe to retrieve pictures", required = true) @PathParam("id") final long id);

        @PUT
        @Path("/{gid}/posts/{pid}")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Modify a post")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully modified post"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 403, message = "Forbidden"),
                        @ApiResponse(code = 409, message = "Group or post does not exist")
        })
        public Response modifyPost(
                        @HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the groupe", required = true) @PathParam("gid") final long gid,
                        @ApiParam(value = "ID of the post", required = true) @PathParam("pid") final long pid,
                        @Valid @ApiParam(value = "Post and user information", required = true) PostUpdateDTO dto);

        @DELETE
        @Path("/{gid}/posts/{pid}")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @ApiOperation(value = "Delete a post")
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successfully deleted post"),
                        @ApiResponse(code = 400, message = "Invalid input data"),
                        @ApiResponse(code = 401, message = "Unauthorized"),
                        @ApiResponse(code = 403, message = "Forbidden"),
                        @ApiResponse(code = 409, message = "Group or post does not exist")
        })
        public Response deletePost(
                        @HeaderParam("Authorization") String token,
                        @ApiParam(value = "ID of the groupe", required = true) @PathParam("gid") final long gid,
                        @ApiParam(value = "ID of the post", required = true) @PathParam("pid") final long pid);
}
