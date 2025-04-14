package com.xsh.learningtracker.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xsh.learningtracker.entity.Tag;
import com.xsh.learningtracker.repository.TagRepository;
import com.xsh.learningtracker.service.TagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public List<Tag> getTagsByUserId(Integer userId) {
        return tagRepository.findByUserId(userId);
    }

    @Override
    public Tag getTagById(Integer id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("标签不存在"));
    }

    @Override
    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Tag updateTag(Integer id, Tag tagDetails) {
        Tag tag = getTagById(id);
        tag.setName(tagDetails.getName());
        tag.setColor(tagDetails.getColor());
        return tagRepository.save(tag);
    }

    @Override
    public void deleteTag(Integer id) {
        Tag tag = getTagById(id);
        tagRepository.delete(tag);
    }
}
