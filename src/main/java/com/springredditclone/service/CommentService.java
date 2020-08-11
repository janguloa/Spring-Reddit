package com.springredditclone.service;

import static java.time.Instant.now;

import org.springframework.stereotype.Service;
import com.springredditclone.dto.CommentsDto;
import com.springredditclone.exception.CommentNotFoundException;
import com.springredditclone.exception.PostNotFoundException;
import com.springredditclone.exception.UsernameNotFoundException;
import com.springredditclone.model.NotificationEmail;
import com.springredditclone.model.Post;
import com.springredditclone.model.Comment;
import com.springredditclone.model.Users;
import com.springredditclone.repository.CommentRepository;
import com.springredditclone.repository.PostRepository;
import com.springredditclone.repository.UserRepository;

import lombok.AllArgsConstructor;
import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {
	
	private static final String POST_URL = "";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentRepository commentRepository;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;
    
    public void save(CommentsDto commentsDto) {
    
        Post post = postRepository.findById(commentsDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post no encontrado con el id: " + commentsDto.getPostId().toString()));
        Comment comment = this.mapToComment(commentsDto, post, authService.getCurrentUser());
        
        commentRepository.save(comment);

        String message = mailContentBuilder.build(post.getUser().getUsername() + " realizo un comentario en tu publicación" + POST_URL);
        sendCommentNotification(message, post.getUser());
    }
    
    private void sendCommentNotification(String message, Users user) {
    	mailService.sendMail(new NotificationEmail(user.getUsername() + " comentó tu publicación", user.getEmail(), message));
    }
    
    public List<CommentsDto> getAlCommentForPost(long postId) {
    	
    	Post post = postRepository.findById(postId)
    			.orElseThrow(() -> new PostNotFoundException("Post no encontrado con id " + postId));
    	
    	return commentRepository.findByPost(post)
    			.stream()
    			.map(this::mapToDto)
    			.collect(toList());
    	
    }
    
    public List<CommentsDto> getAllCommentsForUser(String userName) {
    	
    	Users user = userRepository.findByUsername(userName)	
    			.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con user: "));
    	
    	return commentRepository.findAllByUser(user)
    			.stream()
    			.map(this::mapToDto)
    			.collect(toList());
    }
    
    public CommentsDto Comment(Long id) {
    	
    	Comment comment = commentRepository.findById(id)
    			.orElseThrow(() -> new CommentNotFoundException("Comment no encontrado con el siguiente id" + id));
    	
    	return mapToDto(comment);

    }
    
    private CommentsDto mapToDto (Comment comment) {
    	
    	return CommentsDto.builder()
    			.id(comment.getId())
    			.postId(comment.getPost().getPostId())
    			.createdDate(comment.getCreatedDate())
    			.text(comment.getText())
    			.userName(comment.getUser().getUsername())
    			.build();
    }
    
    private Comment mapToComment (CommentsDto commentsDto, Post post, Users user) {
    	
    	return Comment.builder()
    			.id(commentsDto.getId())
    			.text(commentsDto.getText())
    			.post(post)
    			.createdDate(Instant.now())
    			.user(user)
    			.build();		
    } 
}