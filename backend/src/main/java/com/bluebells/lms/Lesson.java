package com.bluebells.lms;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private CourseModule module;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "sequence_order", nullable = false)
    private int sequenceOrder = 0;

    @Column(name = "lesson_type", nullable = false, length = 32)
    private String lessonType = "LECTURE";

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes = 0;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<LearningResource> resources = new ArrayList<>();

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<YouTubeVideo> videos = new ArrayList<>();

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<LessonProgress> progressList = new ArrayList<>();

    @Column(name = "published_at")
    private Instant publishedAt = Instant.now();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Lesson() {
    }

    public Lesson(Course course, String title, String content, int sequenceOrder) {
        this.course = course;
        this.title = title;
        this.content = content;
        this.sequenceOrder = sequenceOrder;
        this.lessonType = "LECTURE";
        this.durationMinutes = 0;
        this.publishedAt = Instant.now();
        this.createdAt = Instant.now();
    }

    public Lesson(Course course, CourseModule module, String title, String content, int sequenceOrder, String lessonType, int durationMinutes) {
        this.course = course;
        this.module = module;
        this.title = title;
        this.content = content;
        this.sequenceOrder = sequenceOrder;
        this.lessonType = lessonType != null ? lessonType : "LECTURE";
        this.durationMinutes = durationMinutes;
        this.publishedAt = Instant.now();
        this.createdAt = Instant.now();
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

    public CourseModule getModule() {
        return module;
    }

    public void setModule(CourseModule module) {
        this.module = module;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(int sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public String getLessonType() {
        return lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
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

    public List<LessonProgress> getProgressList() {
        return progressList;
    }

    public void setProgressList(List<LessonProgress> progressList) {
        this.progressList = progressList;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
