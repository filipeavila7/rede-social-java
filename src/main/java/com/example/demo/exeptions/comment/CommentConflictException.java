package com.example.demo.exeptions.comment;

public class CommentConflictException extends RuntimeException{
    public CommentConflictException(String m) {
        super(m);
    }
}
