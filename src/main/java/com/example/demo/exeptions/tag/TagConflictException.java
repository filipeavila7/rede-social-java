package com.example.demo.exeptions.tag;

public class TagConflictException extends RuntimeException{
    public TagConflictException(String message) {
        super(message);
    }
}
