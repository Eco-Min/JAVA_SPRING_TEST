package com.example.demo.post.domain;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class PostTest {

    @Test
    public void create_post_from_postCreate() throws Exception {
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1L)
                .content("helloWorld")
                .build();

        User writer = User.builder()
                .id(1L)
                .email("eco@naver.com")
                .nickname("eco")
                .address("seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode(UUID.randomUUID().toString())
                .build();

        // when
        Post post = Post.from(writer, postCreate);

        // then
        assertThat(post.getContent()).isEqualTo(postCreate.getContent());
        assertThat(post.getWriter().getEmail()).isEqualTo(writer.getEmail());
        assertThat(post.getWriter().getAddress()).isEqualTo(writer.getAddress());
        assertThat(post.getWriter().getNickname()).isEqualTo(writer.getNickname());
        assertThat(post.getWriter().getStatus()).isEqualTo(writer.getStatus() );

    }


}