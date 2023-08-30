package fr.togepic.objects;

import fr.togepic.entities.Role;

import java.io.Serializable;

public class MemberLightDTO implements Serializable {
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isValid() {
        for (Role r : Role.values())
            if (r.name().equals(role))
                return true;
        return false;
    }
}
