package com.example.demo.exeptions.comment;

public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException() {
        super("Comentário não encontrado");
    }
}
