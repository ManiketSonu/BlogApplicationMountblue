package com.mani.example.blogapp.controller;

import com.mani.example.blogapp.entities.Post;
import com.mani.example.blogapp.repository.PostRepository;
import com.mani.example.blogapp.repository.TagRepository;
import com.mani.example.blogapp.services.PostService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    public PostController(PostService postService, TagRepository tagRepository, PostRepository postRepository) {
        this.postService = postService;
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        return "createPost";
    }

    @PostMapping
    public String createPost(@ModelAttribute("post") Post post,
                             @RequestParam("tagNames") String tagNames, Model model) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        postService.createPost(post, tagNames);
        model.addAttribute("post", new Post());
        return "createPost";
    }

    @GetMapping
    public String listPosts(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String publishedAt,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sort,
            Model model) {

        Page<Post> posts = postService.getFilteredPosts(author, tag, publishedAt, page, size, sort);

        model.addAttribute("posts", posts.getContent());
        model.addAttribute("author", author);
        model.addAttribute("tag", tag);
        model.addAttribute("publishedAt", publishedAt);
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);
        model.addAttribute("page", page);

        model.addAttribute("currentPage", page-1);
        model.addAttribute("totalPages", posts.getTotalPages());

        return "list";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Integer id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "view";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "edit";
    }

    @PostMapping("/update/{id}")
    public String updatePost(@PathVariable Integer id,
                             @ModelAttribute("post") Post updatedPost,
                             @RequestParam("tagNames") String tagNames) {

        Post existingPost = postService.getPostById(id);

        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setExcerpt(updatedPost.getExcerpt());
        existingPost.setContent(updatedPost.getContent());
        existingPost.setAuthor(updatedPost.getAuthor());
        existingPost.setPublishedAt(updatedPost.getPublishedAt());
        existingPost.setUpdatedAt(LocalDateTime.now());

        postService.updatePost(existingPost, tagNames);

        return "redirect:/posts";
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Integer id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }
}
