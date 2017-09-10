package com.pentavalue.yousry.firebasechat.models;

/**
 * Created by yousry on 9/10/2017.
 */

public class Contact {
    private String contact_id;
    private String contact_name;
    private String phone_number;
    private int phone_type;
    private String userId;
    private String email;
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
        this.userId = "";
        this.email = "";
        isMobile =false;
        isMessengerContact =false;
    }

    public Contact(Contact contact) {
        this.contact_id = contact.getContact_id();
        this.contact_name = contact.getContact_name();
        this.phone_number = contact.getPhone_number();
        this.phone_type = contact.getPhone_type();
        this.userId = contact.getUserId();
        this.email = contact.getEmail();
        isMobile = contact.isMobile();
        isMessengerContact=contact.isMessengerContact();

    }

    public Contact(String contact_id, String contact_name, String phone_number, int phone_type) {
        this.contact_id = contact_id;
        this.contact_name = contact_name;
        this.phone_number = phone_number;
        this.phone_type = phone_type;
        this.userId = "";
        this.email = "";
        isMobile =false;
        isMessengerContact =false;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contact_id='" + contact_id + '\'' +
                ", contact_name='" + contact_name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", phone_type=" + phone_type +
                ", userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", isMobile=" + isMobile + '\'' +
                ", isMessengerContact=" + isMessengerContact +
                '}';
    }

    public Contact(String contact_id, String contact_name, String phone_number, int phone_type, String userId, String email) {
        this.contact_id = contact_id;
        this.contact_name = contact_name;
        this.phone_number = phone_number;
        this.phone_type = phone_type;
        this.userId = userId;
        this.email = email;
        isMobile =false;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
