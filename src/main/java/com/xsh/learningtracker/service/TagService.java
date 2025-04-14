package com.xsh.learningtracker.service;

import java.util.List;

import com.xsh.learningtracker.entity.Tag;

public interface TagService {

    List<Tag> getAllTags();

    List<Tag> getTagsByUserId(Integer userId);

    Tag getTagById(Integer id);

    Tag createTag(Tag tag);

    Tag updateTag(Integer id, Tag tag);

    void deleteTag(Integer id);
}
