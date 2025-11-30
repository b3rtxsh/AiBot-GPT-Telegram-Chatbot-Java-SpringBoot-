package com.telegram.bot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_completion_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatCompletionRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ai_model", nullable = false)
    private String model;

    @OneToOne(mappedBy = "completion", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonIgnore
    private Message messages;

    @OneToMany(mappedBy = "request", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UsageEntity> usages = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "chat_history_id", nullable = false)
    @JsonIgnore
    private ChatHistory chatHistory;

}
