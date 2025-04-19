package com.mani.example.blogapp.services;

import com.mani.example.blogapp.entities.Comment;
import com.mani.example.blogapp.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment getCommentById(Integer id) {
        return commentRepository.findById(id).orElseThrow();
    }

    public void addComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public Comment updateComment(Integer id, Comment updatedComment) {
        Comment existing = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        existing.setName(updatedComment.getName());
        existing.setEmail(updatedComment.getEmail());
        existing.setContent(updatedComment.getContent());
        return commentRepository.save(existing);
    }

    public void deleteComment(Integer id) {
        commentRepository.deleteById(id);
    }
}
