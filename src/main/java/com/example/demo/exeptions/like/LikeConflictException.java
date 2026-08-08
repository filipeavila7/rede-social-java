package com.example.demo.exeptions.like;

public class LikeConflictException extends RuntimeException{
    public LikeConflictException(String m) {
        super(m);
    }
}
