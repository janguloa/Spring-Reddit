package com.springredditclone.service;

import static java.time.Instant.now;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springredditclone.dto.SubredditDto;
import com.springredditclone.exception.SubredditNotFoundException;
import com.springredditclone.model.Subreddit;
import com.springredditclone.repository.SubredditRepository;
import static java.util.stream.Collectors.toList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

	private final SubredditRepository subredditRepository;
	private final AuthService authService;

	@Transactional(readOnly = true)
	public List<SubredditDto> getAll() {

		return subredditRepository.findAll().stream().map(this::mapToDto).collect(toList());

	}

	@Transactional
	public SubredditDto save(SubredditDto subredditDto) {

		Subreddit save = subredditRepository.save(mapToSubreddit(subredditDto));
		subredditDto.setId(save.getId());

		return subredditDto;
	}

	public SubredditDto getSubreddit(Long id) {

		Subreddit subreddit = subredditRepository.findById(id)
				.orElseThrow(() -> new SubredditNotFoundException("Subreddit no encontrado con el id -" + id));

		return mapToDto(subreddit);

	}

	private SubredditDto mapToDto(Subreddit subreddit) {
		return SubredditDto.builder()
				.name(subreddit.getName())
				.id(subreddit.getId())
				.postCount(subreddit.getPosts().size())
				.build();
	}

	private Subreddit mapToSubreddit(SubredditDto subredditDto) {
		return Subreddit.builder()
				.name(subredditDto.getName())
				.description(subredditDto.getDescription())
				.user(authService.getCurrentUser())
				.createdDate(now())
				.build();
	}
}