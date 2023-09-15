package ru.practicum.shareit.request.model.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RequestMapper {
    private final ItemMapper itemMapper;
    public List<RequestDtoResponse> map(List<Request> requests, boolean needItems) {
        return requests.stream()
                .map(request -> getBuild(request, needItems))
                .collect(Collectors.toList());
    }

    private RequestDtoResponse getBuild(Request request, boolean needItems) {
        RequestDtoResponse requestDtoResponse = RequestDtoResponse.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreatedDate())
                .build();
        if (needItems) {
            requestDtoResponse.setItems(itemMapper.map(request.getItems()));
        }

        return requestDtoResponse;
    }

    private Request getBuild(RequestDto requestDto, User user) {
        return Request.builder()
                .description(requestDto.getDescription())
                .requester(user)
                .build();
    }

    public RequestDtoResponse map(Request request, boolean needItems) {
        return getBuild(request, needItems);
    }

    public Request map(RequestDto requestDto, User user) {
        return getBuild(requestDto, user);
    }
}
