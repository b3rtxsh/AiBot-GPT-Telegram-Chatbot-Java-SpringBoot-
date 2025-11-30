package com.telegram.bot.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_history_id")
    @JsonBackReference
    private ChatHistory chatHistory;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "message_content", columnDefinition = "text", nullable = false)
    private String content;

    @OneToOne
    @JoinColumn(name = "chat_completion_request_id")
    @JsonIgnore
    private ChatCompletionRequestEntity completion;

    public Message(String user, String test) {
    }
}

