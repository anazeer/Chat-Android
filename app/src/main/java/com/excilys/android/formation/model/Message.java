package com.excilys.android.formation.model;


/**
 * Model for a chat message
 */
public class Message {

    private String login;
    private String message;
    private boolean user;
    private String image;

    public Message() {
        super();
    }

    public String getLogin() {
        return login;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return user;
    }

    public String getImage() {
        return image;
    }

    /**
     * Chat message builder
     */
    public static class Builder {

        Message chat;

        public Builder() {
            chat  = new Message();
        }

        public Builder login(String login) {
            chat.login = login;
            return this;
        }

        public Builder message(String message) {
            chat.message = message;
            return this;
        }

        public Builder user(boolean user) {
            chat.user = user;
            return this;
        }

        public Builder image(String image) {
            chat.image = image;
            return this;
        }

        public Message build() {
            return chat;
        }
    }
}