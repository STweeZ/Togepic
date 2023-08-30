package fr.togepic.objects;

import fr.togepic.entities.Member;

import java.io.Serializable;
import java.util.Base64;

public class MemberDTO implements Serializable {

    private long id;
    private String name;
    private String picture;
    private String role;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static MemberDTO toDTO(Member member) {
        MemberDTO dto = new MemberDTO();
        dto.id = member.getUser().getId();
        dto.name = member.getUser().getName();
        if (member.getUser().getPicture() != null)
            dto.picture = Base64.getEncoder().encodeToString(member.getUser().getPicture());
        dto.role = member.getRole().toString();
        return dto;
    }
}
