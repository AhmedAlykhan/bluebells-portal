package com.bluebells.lms;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lms_course_modules")
public class CourseModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 1;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    @OrderBy("sequenceOrder ASC")
    private List<Lesson> lessons = new ArrayList<>();

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    private List<LearningResource> resources = new ArrayList<>();

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    @OrderBy("displayOrder ASC")
    private List<YouTubeVideo> videos = new ArrayList<>();

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    private List<Assignment> assignments = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public CourseModule() {
    }

    public CourseModule(Course course, String title, String description, int displayOrder) {
        this.course = course;
        this.title = title;
        this.description = description;
        this.displayOrder = displayOrder;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<LearningResource> getResources() {
        return resources;
    }

    public void setResources(List<LearningResource> resources) {
        this.resources = resources;
    }

    public List<YouTubeVideo> getVideos() {
        return videos;
    }

    public void setVideos(List<YouTubeVideo> videos) {
        this.videos = videos;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
