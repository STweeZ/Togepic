package fr.togepic.objects;

import fr.togepic.entities.User;
import fr.togepic.utils.Encryption;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.util.Base64;

public class UserUpdateDTO implements Serializable {
    private String email;
    private String password;
    private String name;
    private String picture;
    private String description;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Encryption.encrypt(password);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static UserUpdateDTO toDTO(User user) {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.email = user.getEmail();
        dto.password = user.getPassword();
        dto.name = user.getName();
        if (user.getPicture() != null)
            dto.picture = Base64.getEncoder().encodeToString(user.getPicture());
        dto.description = user.getDescription();
        return dto;
    }

    private static User toUser(UserUpdateDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setName(dto.getName());
        if (dto.getPicture() != null)
            user.setPicture(Base64.getDecoder().decode(dto.getPicture()));
        user.setDescription(dto.getDescription());
        return user;
    }

    public static User toEntity(User user, UserUpdateDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull()).setSkipNullEnabled(true);
        User temp = toUser(dto);
        temp.setId(user.getId());
        temp.setGroupes(user.getGroupes());
        temp.setPosts(user.getPosts());
        temp.setToken(user.getToken());
        temp.setEmailVerified(user.getEmailVerified());
        modelMapper.map(temp, user);
        return user;
    }
}
