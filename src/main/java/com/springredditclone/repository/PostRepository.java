package com.springredditclone.repository;

import com.springredditclone.model.Post;
import com.springredditclone.model.Subreddit;
import com.springredditclone.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllBySubreddit(Subreddit subreddit);

    List<Post> findByUser(Users user);
}
