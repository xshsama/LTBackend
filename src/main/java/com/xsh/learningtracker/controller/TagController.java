package com.xsh.learningtracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xsh.learningtracker.entity.Tag;
import com.xsh.learningtracker.service.TagService;
import com.xsh.learningtracker.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    @Autowired
    private final UserService userService;

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<Tag>> getAllTags(Authentication authentication) {
        // 获取当前登录用户的ID
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        return ResponseEntity.ok(tagService.getTagsByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTagById(@PathVariable Integer id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag, Authentication authentication) {
        // 设置当前登录用户
        String username = authentication.getName();
        tag.setUser(userService.findByUsername(username));
        return ResponseEntity.ok(tagService.createTag(tag));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tag> updateTag(@PathVariable Integer id, @RequestBody Tag tag) {
        return ResponseEntity.ok(tagService.updateTag(id, tag));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Integer id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
