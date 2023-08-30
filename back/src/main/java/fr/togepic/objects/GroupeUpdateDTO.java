package fr.togepic.objects;

import fr.togepic.entities.Groupe;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.util.Base64;

public class GroupeUpdateDTO implements Serializable {
    private String name;
    private String description;
    private String picture;
    private boolean isPrivate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean isValid() {
        return name != null;
    }

    private static Groupe toGroupe(GroupeUpdateDTO dto) {
        Groupe groupe = new Groupe();
        groupe.setName(dto.getName());
        if (dto.getPicture() != null)
            groupe.setPicture(Base64.getDecoder().decode(dto.getPicture()));
        groupe.setDescription(dto.getDescription());
        groupe.setIsPrivate(dto.getIsPrivate());
        return groupe;
    }

    public static Groupe toEntity(Groupe groupe, GroupeUpdateDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull()).setSkipNullEnabled(true);
        Groupe temp = toGroupe(dto);
        temp.setId(groupe.getId());
        temp.setPosts(groupe.getPosts());
        temp.setUsers(groupe.getUsers());
        modelMapper.map(temp, groupe);
        return groupe;
    }
}
