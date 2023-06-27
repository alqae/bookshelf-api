package com.alqae.bookshelf.models;

public enum EmailTemplates {
    VERIFY_EMAIL("verify-email"),
    INVITE_USER("invite-user"),
    RESET_PASSWORD("reset-password");

    private final String nameTemplate;

    EmailTemplates(String nameTemplate) {
        this.nameTemplate = nameTemplate;
    }

    public String getNameTemplate() {
        return nameTemplate;
    }
}
