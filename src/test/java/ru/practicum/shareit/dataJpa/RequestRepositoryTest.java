package ru.practicum.shareit.dataJpa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repo.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestRepositoryTest {
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(User.builder()
                .email("test1")
                .name("test1")
                .build());
        user2 = userRepository.save(User.builder()
                .email("test2")
                .name("test2")
                .build());
    }

    @Test
    void shouldBeSuccessFindByRequester_IdNotOrderByCreatedDateDesc() {
        //before
        Request request1 = requestRepository.save(Request.builder()
                .requester(user1)
                .description("test1")
                .build());
        Request request2 = requestRepository.save(Request.builder()
                .requester(user1)
                .description("test2")
                .build());
        Request request3 = requestRepository.save(Request.builder()
                .requester(user2)
                .description("test3")
                .build());
        //when
        List<Request> requests = requestRepository.findByRequester_IdOrderByCreatedDateDesc(user1.getId());
        //then
        assertThat(requests.size()).isEqualTo(2);
        assertThat(requests.get(0)).isEqualTo(request2);
        assertThat(requests.get(1)).isEqualTo(request1);
    }

    @Test
    void shouldBeSuccessFindByRequester_IdOrderByCreatedDateDesc() {
        //before
        Request request1 = requestRepository.save(Request.builder()
                .requester(user1)
                .description("test1")
                .build());
        Request request2 = requestRepository.save(Request.builder()
                .requester(user1)
                .description("test2")
                .build());
        Request request3 = requestRepository.save(Request.builder()
                .requester(user2)
                .description("test3")
                .build());
        Pageable pageable = PageRequest.of(0, 3);
        //when
        List<Request> requests = requestRepository.findByRequester_IdNotOrderByCreatedDateDesc(user2.getId(), pageable);
        //then
        assertThat(requests.size()).isEqualTo(2);
        assertThat(requests.get(0)).isEqualTo(request2);
        assertThat(requests.get(1)).isEqualTo(request1);
    }
}