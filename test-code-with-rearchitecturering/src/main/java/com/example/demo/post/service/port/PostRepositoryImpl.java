package com.example.demo.post.service.port;

import com.example.demo.post.domain.Post;
import com.example.demo.post.infrastructure.PostEntity;
import com.example.demo.post.infrastructure.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository{

    private final PostJpaRepository postJpaRepository;
    @Override
    public Optional<Post> findById(long id) {
        return postJpaRepository.findById(id).map(postEntity -> postEntity.toModel());
    }

    @Override
    public Post save(Post post) {
        return postJpaRepository.save(PostEntity.fromModel(post)).toModel();
    }
}
