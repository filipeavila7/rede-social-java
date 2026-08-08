package com.example.demo.exeptions.comment;

public class CommentConflict extends RuntimeException{
    public CommentConflict(String m) {
        super(m);
    }
}
