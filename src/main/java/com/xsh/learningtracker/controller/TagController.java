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

import com.xsh.learningtracker.dto.TagDTO;
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
    public ResponseEntity<List<TagDTO>> getAllTags(Authentication authentication) {
        // 获取当前登录用户的ID
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        List<Tag> tags = tagService.getTagsByUserId(userId);
        List<TagDTO> tagDTOs = tags.stream()
                .map(tag -> new TagDTO(tag.getId(), tag.getName(), tag.getColor(), userId))
                .toList();
        return ResponseEntity.ok(tagDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getTagById(@PathVariable Integer id) {
        Tag tag = tagService.getTagById(id);
        Integer userId = tag.getUser() != null ? tag.getUser().getId() : null;
        return ResponseEntity.ok(new TagDTO(tag.getId(), tag.getName(), tag.getColor(), userId));
    }

    @PostMapping
    public ResponseEntity<TagDTO> createTag(@RequestBody Tag tag, Authentication authentication) {
        // 设置当前登录用户
        String username = authentication.getName();
        tag.setUser(userService.findByUsername(username));
        Tag createdTag = tagService.createTag(tag);
        Integer userId = createdTag.getUser() != null ? createdTag.getUser().getId() : null;
        return ResponseEntity.ok(new TagDTO(createdTag.getId(), createdTag.getName(), createdTag.getColor(), userId));
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
