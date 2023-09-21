package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;

@Builder
@Entity
@Table(name = "items", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@NamedEntityGraph(name = "item_comments", attributeNodes = @NamedAttributeNode("comments"))
public class Item {
    @Id
    @Column(name = "item_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    @Column(name = "description", nullable = false, length = 200)
    private String description;
    @Column(name = "available", nullable = false)
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Request request;
    @OneToMany(mappedBy = "item")
    private List<Comment> comments;
}
