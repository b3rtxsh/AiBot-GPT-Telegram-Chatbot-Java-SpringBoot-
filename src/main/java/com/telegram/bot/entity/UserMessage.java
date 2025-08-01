package com.telegram.bot.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gpt_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_history_id")
    @JsonBackReference
    private ChatHistory chatHistory;

    @Column(name = "user_role", nullable = false)
    private String role;

    @Column(name = "message_content", nullable = false)
    private String content;

    @OneToOne
    @JoinColumn(name = "chat_completion_request_id")
    @JsonIgnore
    private ChatCompletionRequestEntity completion;

}

