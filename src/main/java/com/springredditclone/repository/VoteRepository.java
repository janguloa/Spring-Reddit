package com.springredditclone.repository;

import com.springredditclone.model.Post;
import com.springredditclone.model.Users;
import com.springredditclone.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, Users currentUser);
}
