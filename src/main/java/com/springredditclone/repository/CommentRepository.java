package com.springredditclone.repository;

import com.springredditclone.model.Comment;
import com.springredditclone.model.Post;
import com.springredditclone.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(Users user);
}
