package fr.togepic.entities;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;
import java.util.Base64;

@Entity
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id = 0L;

    @ManyToOne
    private Post post;

    @Lob
    private byte[] text;

    private String xGeolocate;
    private String yGeolocate;

    public Picture() {
    }

    public Picture(byte[] text, String xGeolocate, String yGeolocate) {
        this.text = text;
        this.xGeolocate = xGeolocate;
        this.yGeolocate = yGeolocate;
    }

    public long getId() {
        return id;
    }

    public byte[] getText() {
        return text;
    }

    public String getXGeolocate() {
        return xGeolocate;
    }

    public String getYGeolocate() {
        return yGeolocate;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public JsonObject toJsonObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("xGeolocate",getXGeolocate());
        builder.add("yGeolocate",getYGeolocate());
        builder.add("text", Base64.getEncoder().encodeToString(getText()));

        return builder.build();
    }
}
