package com.example.personellistesi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bildirim_log")
public class BildirimLog {

    @Id
    private String id;

    @Column(length = 256)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "to_mail", length = 128)
    private String toAddress;

    @Column(name = "send_time")
    private LocalDateTime sendTime;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.sendTime == null) {
            this.sendTime = LocalDateTime.now();
        }
    }

    public BildirimLog() {}

    public BildirimLog(String subject, String content, String toAddress) {
        this.subject = subject;
        this.content = content;
        this.toAddress = toAddress;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getToAddress() { return toAddress; }
    public void setToAddress(String toAddress) { this.toAddress = toAddress; }

    public LocalDateTime getSendTime() { return sendTime; }
    public void setSendTime(LocalDateTime sendTime) { this.sendTime = sendTime; }
}
