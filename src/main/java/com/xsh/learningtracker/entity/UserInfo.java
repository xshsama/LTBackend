package com.xsh.learningtracker.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "user_info")
@EqualsAndHashCode(exclude = "user")
@ToString(exclude = "user")
public class UserInfo {
    @Id
    private Integer id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @jakarta.persistence.Column(name = "nickname")
    private String nickname;

    @jakarta.persistence.Column(name = "avatar")
    private String avatar;

    @jakarta.persistence.Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @jakarta.persistence.Column(name = "birthday")
    private LocalDate birthday;

    @jakarta.persistence.Column(name = "location")
    private String location;

    @jakarta.persistence.Column(name = "created_at")
    private LocalDate createdAt;

    @jakarta.persistence.Column(name = "education")
    private String education;

    @jakarta.persistence.Column(name = "profession")
    private String profession;
}
