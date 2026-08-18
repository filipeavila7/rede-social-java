package com.example.demo.exeptions.user;

public class UserConflictException extends RuntimeException{
    public UserConflictException(String m) {
        super(m);
    }
}
