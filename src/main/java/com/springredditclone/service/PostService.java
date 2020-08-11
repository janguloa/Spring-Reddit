package com.springredditclone.service;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.springredditclone.dto.PostResponse;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springredditclone.dto.PostRequest;
import com.springredditclone.exception.SubredditNotFoundException;
import com.springredditclone.exception.PostNotFoundException;
import com.springredditclone.model.Post;
import com.springredditclone.model.Subreddit;
import com.springredditclone.model.Users;
import com.springredditclone.model.Vote;
import com.springredditclone.model.VoteType;
import com.springredditclone.repository.CommentRepository;
import com.springredditclone.repository.PostRepository;
import com.springredditclone.repository.SubredditRepository;
import com.springredditclone.repository.UserRepository;
import com.springredditclone.repository.VoteRepository;
import lombok.AllArgsConstructor;
import static java.util.stream.Collectors.toList;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import static com.springredditclone.model.VoteType.DOWNVOTE;
import static com.springredditclone.model.VoteType.UPVOTE;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

	PostRepository postRepository;
	SubredditRepository subredditRepository;
	CommentRepository commentRepository;
	UserRepository userRepository;
	AuthService authService;
	VoteRepository voteRepository;

	public void save(PostRequest postRequest) {

		Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
				.orElseThrow(() -> new SubredditNotFoundException(
						"SubReddit encontrado con el siguiente nombre: " + postRequest.getSubredditName()));

		postRepository.save(mapToPost(postRequest, subreddit, authService.getCurrentUser()));
	}

	@Transactional(readOnly = true)
	public PostResponse getPost(Long id) {

		Post post = postRepository.findById(id)
				.orElseThrow(() -> new PostNotFoundException(id.toString()));

		return mapToDto(post);

	}

	@Transactional(readOnly = true)
	public List<PostResponse> getAllPosts() {

		return postRepository.findAll().stream().map(this::mapToDto).collect(toList());

	}

	@Transactional(readOnly = true)
	public List<PostResponse> getPostsBySubreddit(Long subredditId) {

		Subreddit subreddit = subredditRepository.findById(subredditId)
				.orElseThrow(() -> new SubredditNotFoundException("Id no encontrado con el número: " +subredditId.toString()));

		List<Post> posts = postRepository.findAllBySubreddit(subreddit);

		return posts.stream().map(this::mapToDto).collect(toList());
	}

	@Transactional(readOnly = true)
	public List<PostResponse> getPostsByUsername(String username) {

		Users user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));

		return postRepository.findByUser(user).stream().map(this::mapToDto).collect(toList());

	}

//	private PostResponse mapToDto(List<Post> postLista) {
//
//		PostResponse postRep = new PostResponse();
//
//		postLista.stream().forEach((p) -> {
//
//			postRep.setId(p.getPostId());
//			postRep.setSubredditName(p.getSubreddit().getName());
//			postRep.setCommentCount(commentCount(p));
//			postRep.setDuration(getDuration(p));
//			postRep.setUpVote(isPostUpVoted(p));
//			postRep.setDownVote(isPostDownVoted(p));
//
//		});
//
//		return PostResponse.builder().id(postRep.getId()).subredditName(postRep.getSubredditName())
//				.commentCount(postRep.getCommentCount()).duration(postRep.getDuration()).upVote(postRep.isUpVote())
//				.downVote(postRep.isDownVote()).build();
//	}

	private PostResponse mapToDto(Post post) {

		return PostResponse.builder()
				.id(post.getPostId())
				.postName(post.getPostName())
				.url(post.getUrl())
				.description(post.getDescription())
				.userName(post.getUser().getUsername())
				.subredditName(post.getSubreddit().getName())
				.voteCount(post.getVoteCount())
				.commentCount(commentCount(post))
				.duration(getDuration(post))
				.upVote(isPostUpVoted(post))
				.downVote(isPostDownVoted(post))
				.build();
	}

	private Post mapToPost(PostRequest postRequest, Subreddit subreddit, Users user) {

		return Post.builder()
				.postName(postRequest.getPostName())
				.url(postRequest.getUrl())
				.description(postRequest.getDescription())
				.createdDate(Instant.now())
				.voteCount(0)
				.user(user)
				.subreddit(subreddit)
				.build();
	}

	private Integer commentCount(Post post) {

		return commentRepository.findByPost(post).size();
	}

	private String getDuration(Post post) {
		return TimeAgo.using(post.getCreatedDate().toEpochMilli());
	}

	boolean isPostUpVoted(Post post) {
		return checkVoteType(post, UPVOTE);
	}

	boolean isPostDownVoted(Post post) {
		return checkVoteType(post, DOWNVOTE);
	}

	private boolean checkVoteType(Post post, VoteType voteType) {
		if (authService.isLoggedIn()) {
			Optional<Vote> voteForPostByUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post,
					authService.getCurrentUser());
			return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType)).isPresent();
		}
		return false;
	}
}