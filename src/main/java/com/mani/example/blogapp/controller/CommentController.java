package com.mani.example.blogapp.controller;

import com.mani.example.blogapp.entities.Comment;
import com.mani.example.blogapp.entities.Post;
import com.mani.example.blogapp.services.CommentService;
import com.mani.example.blogapp.services.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @PostMapping("/add/{postId}")
    public String addComment(@PathVariable Integer postId, @ModelAttribute Comment comment) {
        Post post = postService.getPostById(postId);
        comment.setPost(post);
        commentService.addComment(comment);
        return "redirect:/posts/" + postId;
    }

    @GetMapping("/edit/{id}")
    public String showEditCommentForm(@PathVariable Integer id, Model model) {
        Comment comment = commentService.getCommentById(id);
        model.addAttribute("comment", comment);
        return "editComment"; // edit.html
    }

    @PostMapping("/update/{id}")
    public String updateComment(@PathVariable Integer id, @ModelAttribute Comment updatedComment) {
        Comment comment = commentService.updateComment(id, updatedComment);
        return "redirect:/posts/" + comment.getPost().getId();
    }

    @PostMapping("/delete/{id}")
    public String deleteComment(@PathVariable Integer id) {
        Comment comment = commentService.getCommentById(id);
        Integer postId = comment.getPost().getId();
        commentService.deleteComment(id);
        return "redirect:/posts/" + postId;
    }
}
