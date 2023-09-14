package ru.practicum.shareit.request;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Table(name = "requests", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Request {
    @Id
    @Column(name = "request_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description", nullable = false, length = 2000)
    private String description;
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    @CreatedDate
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    @OneToMany(mappedBy = "request")
    List<Item> items;
}
