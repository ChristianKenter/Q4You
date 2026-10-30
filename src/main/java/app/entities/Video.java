package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id", nullable = false)
    int videoId;

    @Column(name = "topic", nullable = false, length = 150)
    String topic;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Client createdBy;

    public Video(String topic, Client createdBy) {
        this.topic = topic;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }
}
