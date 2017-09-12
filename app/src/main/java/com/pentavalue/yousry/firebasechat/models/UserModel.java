package com.pentavalue.yousry.firebasechat.models;

import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alessandro Barreto on 22/06/2016.
 */

@IgnoreExtraProperties
public class UserModel implements Serializable{

    private String id;
    private String name;

    private String email;

    private String password;

    private String imageUrl;

    private String phone;


    //Default Const
    public UserModel() {
        this.id ="";
        this.name ="";
        this.email ="";
        this.password ="";
        this.phone ="";
        this.imageUrl ="";
    }

    //Const with params
    public UserModel(String name, String imageUrl, String id) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.id = id;
        this.email ="";
        phone ="";
    }

    public UserModel(String id, String name, String email, String password, String phone, String imageUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.imageUrl =imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

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
        this.password = password;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }





    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    // [START post_to_map]

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_id", id);
        result.put("name", name);
        result.put("email", email);
        result.put("password", password);
        result.put("phone", phone);
        result.put("image_url", imageUrl);
        return result;
    }
    @Override
    public String toString() {
        return "UserModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", image_url='" + imageUrl +'\'' +
                '}';
    }
}
