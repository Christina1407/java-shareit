create table if not exists users
(
    user_id bigint generated by default as identity primary key,
    email     varchar(320) not null,
    name     varchar(200) not null,
     unique (email)
);

create table if not exists requests
(
    request_id bigint generated by default as identity primary key,
    description varchar(2000) not null,
    requester_id bigint,
    created_date    timestamp not null,
    constraint fk_requests_requester_id
           foreign key (requester_id)
           references users (user_id) ON DELETE CASCADE
);

create table if not exists items
(
    item_id bigint generated by default as identity primary key,
    name     varchar(200) not null,
    description     varchar(200) not null,
    available boolean not null,
    owner_id bigint not null,
    request_id bigint,
    constraint fk_items_owner_id
           foreign key (owner_id)
           references users (user_id) ON DELETE CASCADE,
    constraint fk_items_request_id
               foreign key (request_id)
               references requests (request_id)
);

create table if not exists bookings
(
    booking_id bigint generated by default as identity primary key,
    start_date     timestamp not null,
    end_date    timestamp not null,
    item_id bigint not null,
    booker_id bigint not null,
    status varchar(20) not null,
    constraint fk_bookings_booker_id
           foreign key (booker_id)
           references users (user_id) ON DELETE CASCADE,
    constraint fk_bookings_item_id
           foreign key (item_id)
           references items (item_id)
);

create table if not exists comments
(
    comment_id bigint generated by default as identity primary key,
    text varchar(2000) not null,
    item_id bigint not null,
    author_id bigint not null,
    created_date    timestamp not null,
    constraint fk_comments_author_id
           foreign key (author_id)
           references users (user_id) ON DELETE CASCADE,
    constraint fk_comments_item_id
           foreign key (item_id)
           references items (item_id)
);
