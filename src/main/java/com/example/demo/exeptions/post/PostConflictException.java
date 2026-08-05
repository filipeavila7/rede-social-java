package com.example.demo.exeptions.post;

public class PostConflictException extends RuntimeException{

    public PostConflictException(String m) {
        super(m);
    }
}
