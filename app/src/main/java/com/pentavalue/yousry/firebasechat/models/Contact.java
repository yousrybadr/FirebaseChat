package com.pentavalue.yousry.firebasechat.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by yousry on 9/10/2017.
 */

public class Contact implements Serializable, Comparable<Contact> {
    private String contact_id;
    private String contact_name;
    private String phone_number;
    private int phone_type;
    private String chatID;
    private UserModel userModel;
    private boolean isMobile;
    private boolean isMessengerContact;


    public boolean isMessengerContact() {
        return isMessengerContact;
    }

    public void setMessengerContact(boolean messengerContact) {
        isMessengerContact = messengerContact;
    }

    public Contact() {
        this.contact_id = "";
        this.contact_name = "";
        this.phone_number = "";
        this.phone_type = -1;

        this.isMobile =false;
        this.chatID ="";
        this.isMessengerContact =false;
        this.userModel = new UserModel();
    }

    public Contact(Contact contact) {
        this.contact_id = contact.getContact_id();
        this.contact_name = contact.getContact_name();
        this.phone_number = contact.getPhone_number();
        this.phone_type = contact.getPhone_type();
        this.isMobile = contact.isMobile();
        this.chatID =contact.getChatID();
        this.userModel =contact.getUserModel();
        this.isMessengerContact=contact.isMessengerContact();

    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public Contact(String contact_id, String contact_name, String phone_number, int phone_type) {
        this.contact_id = contact_id;
        this.contact_name = contact_name;
        this.phone_number = phone_number;
        this.phone_type = phone_type;
        this.chatID ="";
        this.isMobile =false;
        this.isMessengerContact =false;
        this.userModel = new UserModel();
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contact_id='" + contact_id + '\'' +
                ", contact_name='" + contact_name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", phone_type=" + phone_type +
                ", chatID='" + chatID + '\'' +
                ", isMobile=" + isMobile +
                ", isMessengerContact=" + isMessengerContact +
                '}';
    }

    public Contact(String contact_id, String contact_name, String phone_number, int phone_type, String userId, String email) {
        this.contact_id = contact_id;
        this.contact_name = contact_name;
        this.phone_number = phone_number;
        this.phone_type = phone_type;
        this.isMobile =false;
    }

    public boolean isMobile() {
        return isMobile;
    }

    public void setMobile(boolean mobile) {
        isMobile = mobile;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public int getPhone_type() {
        return phone_type;
    }

    public void setPhone_type(int phone_type) {
        this.phone_type = phone_type;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    @Override
    public int compareTo(@NonNull Contact contact) {
        return this.contact_name.compareTo(contact.getContact_name());

    }
}
