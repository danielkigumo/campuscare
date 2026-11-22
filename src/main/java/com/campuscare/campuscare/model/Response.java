package com.campuscare.campuscare.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "responses")
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long responseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_internal", nullable = false)
    private boolean internal = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Long getResponseId() { return responseId; }

    public Request getRequest() { return request; }
    public void setRequest(Request request) { this.request = request; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isInternal() { return internal; }
    public void setInternal(boolean internal) { this.internal = internal; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
}