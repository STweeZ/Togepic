package fr.togepic.objects;

import fr.togepic.entities.Invitation;

import java.io.Serializable;
import java.util.Base64;

public class InvitationDTO implements Serializable {
    private long id;
    private String name;
    private String picture;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public static InvitationDTO toDTOGroupe(Invitation invitation) {
        InvitationDTO dto = new InvitationDTO();
        dto.id = invitation.getGroupe().getId();
        dto.name = invitation.getGroupe().getName();
        if (invitation.getGroupe().getPicture() != null)
            dto.picture = Base64.getEncoder().encodeToString(invitation.getGroupe().getPicture());
        return dto;
    }

    public static InvitationDTO toDTOUser(Invitation invitation) {
        InvitationDTO dto = new InvitationDTO();
        dto.id = invitation.getUser().getId();
        dto.name = invitation.getUser().getName();
        if (invitation.getUser().getPicture() != null)
            dto.picture = Base64.getEncoder().encodeToString(invitation.getUser().getPicture());
        return dto;
    }
}
