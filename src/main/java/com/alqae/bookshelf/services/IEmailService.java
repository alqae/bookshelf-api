package com.alqae.bookshelf.services;

import com.alqae.bookshelf.models.EmailTemplates;

import java.io.File;
import java.util.Map;

public interface IEmailService {
    void sendEmail(String[] to, String subject, String text);

    void sendEmailWithAttachment(String[] to, String subject, String text, File file);

    void sendHtmlMessage(String[] to, String subject, EmailTemplates template, Map<String, Object> variables);
}
