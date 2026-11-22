package com.campuscare.campuscare.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "request_status_history")
public class RequestStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 20)
    private RequestStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private RequestStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private OffsetDateTime changedAt = OffsetDateTime.now();

    public Long getHistoryId() { return historyId; }

    public Request getRequest() { return request; }
    public void setRequest(Request request) { this.request = request; }

    public RequestStatus getOldStatus() { return oldStatus; }
    public void setOldStatus(RequestStatus oldStatus) { this.oldStatus = oldStatus; }

    public RequestStatus getNewStatus() { return newStatus; }
    public void setNewStatus(RequestStatus newStatus) { this.newStatus = newStatus; }

    public User getChangedBy() { return changedBy; }
    public void setChangedBy(User changedBy) { this.changedBy = changedBy; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public OffsetDateTime getChangedAt() { return changedAt; }
}