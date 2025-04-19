package com.mani.example.blogapp.services;

import com.mani.example.blogapp.entities.Post;
import com.mani.example.blogapp.entities.Tag;
import com.mani.example.blogapp.repository.PostRepository;
import com.mani.example.blogapp.repository.TagRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public void createPost(Post post, String tagNames) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post = postRepository.save(post);
        updateTagsForPost(post, tagNames);
    }

    public Post getPostById(Integer id) {
        return postRepository.findById(id).orElseThrow();
    }

    public void updatePost(Post existingPost, String tagNames) {
        existingPost.setUpdatedAt(LocalDateTime.now());
        postRepository.save(existingPost);
        updateTagsForPost(existingPost, tagNames);
    }

    private void updateTagsForPost(Post post, String tagNames){
        if(tagNames==null || tagNames.isBlank()){
            post.setTags(List.of());
            postRepository.save(post);
        }

        List<String> tagList = Arrays.stream(tagNames.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        List<Tag> tags = tagRepository.findByNameIn(tagList);

        //if the tag is not avl then create the new one
        List<String> existingTagNames = tags.stream()
                .map(Tag::getName)
                .toList();

        tagList.stream()
                .filter(name -> !existingTagNames.contains(name))
                .forEach(name -> {
                    Tag newTag = new Tag();
                    newTag.setName(name);
                    tags.add(tagRepository.save(newTag));
                });

        post.setTags(tags);
        postRepository.save(post);
    }

    public List<Post> filterPosts(String author, String tag, String publishedAt) {
        List<Post> posts = postRepository.findAll();

        if (author != null && !author.isBlank()) {
            posts = posts.stream()
                    .filter(post -> post.getAuthor() != null && post.getAuthor().equalsIgnoreCase(author))
                    .toList();
        }

        if (tag != null && !tag.isBlank()) {
            posts = posts.stream()
                    .filter(post -> post.getTags() != null &&
                            post.getTags().stream().anyMatch(t -> t.getName().equalsIgnoreCase(tag)))
                    .toList();
        }

        if (publishedAt != null && !publishedAt.isBlank()) {
            posts = posts.stream()
                    .filter(post -> post.getPublishedAt() != null &&
                            post.getPublishedAt().toLocalDate().toString().equals(publishedAt))
                    .toList();
        }

        return posts;
    }

    public Page<Post> getFilteredPosts(String author, String tag, String publishedAt, int page, int size, String sortDir) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("publishedAt").descending());
        if ("asc".equalsIgnoreCase(sortDir)) {
            pageable = PageRequest.of(page - 1, size, Sort.by("publishedAt").ascending());
        }

        List<Post> filtered = filterPosts(author, tag, publishedAt);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());

        List<Post> paginated = filtered.subList(start, end);
        return new PageImpl<>(paginated, pageable, filtered.size());
    }

    public void deletePost(Integer id) {
        postRepository.deleteById(id);
    }
}
