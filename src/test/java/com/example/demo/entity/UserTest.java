package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

public class UserTest {
    public static void main(String[] args) {
        User user = new User("shipin", "shipin@email", "123");
        Post post = new Post("teste", "teste.png", user);


        post.setUser(user);
        System.out.println(user.getPosts());
          
        
       
    }
}
