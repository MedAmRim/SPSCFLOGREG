package com.example.demo.email;

import org.springframework.stereotype.Service;


public interface IEmailSender {
    void send(String to, String email);
}
