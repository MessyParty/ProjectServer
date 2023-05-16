package com.glimps.glimpsserver.perfume.application;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glimps.glimpsserver.common.error.CustomException;
import com.glimps.glimpsserver.common.error.EntityNotFoundException;
import com.glimps.glimpsserver.common.error.ErrorCode;
import com.glimps.glimpsserver.perfume.domain.Note;
import com.glimps.glimpsserver.perfume.domain.Perfume;
import com.glimps.glimpsserver.perfume.domain.PerfumeNote;
import com.glimps.glimpsserver.perfume.dto.PerfumeResponse;
import com.glimps.glimpsserver.perfume.dto.PerfumeSearchCondition;
import com.glimps.glimpsserver.perfume.infra.PerfumeCustomRepository;
import com.glimps.glimpsserver.perfume.infra.PerfumeRepository;
import com.glimps.glimpsserver.review.dto.ReviewCreateRequest;
import com.glimps.glimpsserver.review.dto.ReviewUpdateRequest;
import com.glimps.glimpsserver.review.vo.ReviewRatings;

@Service
@Transactional(readOnly = true)
public class PerfumeService {
	private final PerfumeRepository perfumeRepository;
	private final PerfumeCustomRepository perfumeCustomRepository;

	public PerfumeService(PerfumeRepository perfumeRepository, PerfumeCustomRepository perfumeCustomRepository) {
		this.perfumeRepository = perfumeRepository;
		this.perfumeCustomRepository = perfumeCustomRepository;
	}

	@Transactional
	public void updateRatings(Perfume perfume, ReviewCreateRequest reviewCreateRequest) {
		perfume.updateRatings(reviewCreateRequest.getOverallRatings(), reviewCreateRequest.getScentRatings(),
			reviewCreateRequest.getLongevityRatings(), reviewCreateRequest.getSillageRatings());
	}

	@Transactional
	public void updateRatings(Perfume perfume, ReviewUpdateRequest reviewUpdateRequest, ReviewRatings reviewRatings) {
		perfume.updateRatings(reviewUpdateRequest.getOverallRatings(), reviewRatings.getScentRatings(),
			reviewUpdateRequest.getLongevityRatings(), reviewUpdateRequest.getSillageRatings(), reviewRatings);
	}

	@Transactional
	public void updateRatings(Perfume perfume, ReviewRatings reviewRatings) {
		perfume.updateRatings(reviewRatings);
	}

	public Perfume getPerfumeById(UUID uuid) {
		return findPerfume(uuid);
	}

	public PerfumeResponse getPerfumeWithNotesAndBrand(UUID uuid) {
		Perfume perfume = findPerfumeWithEntities(uuid);
		List<Note> notes = getNotes(perfume);
		return PerfumeResponse.of(perfume, notes);
	}

	private Perfume findPerfume(UUID perfumeUuid) {
		return perfumeRepository.findByUuid(perfumeUuid)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PERFUME_NOT_FOUND));
	}

	private Perfume findPerfumeWithEntities(UUID perfumeUuid) {
		return perfumeRepository.findPerfumeWithEntities(perfumeUuid)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PERFUME_NOT_FOUND));
	}

	public List<PerfumeResponse> getAll() {
		List<Perfume> perfumes = perfumeRepository.findAll();
		return perfumes.stream().map(PerfumeResponse::of).collect(Collectors.toList());
	}

	public Slice<PerfumeResponse> getPerfumeByBrand(String brandName, Pageable pageable) {
		Slice<Perfume> slice = perfumeCustomRepository.searchByBrand(brandName, pageable);
		List<PerfumeResponse> content = slice.stream().map(PerfumeResponse::of).collect(Collectors.toList());
		return new SliceImpl<>(content, slice.getPageable(), slice.hasNext());
	}

	public List<PerfumeResponse> getRandomPerfume(Integer amount) {
		if (amount > 10)
			throw new CustomException(ErrorCode.PERFUME_TOO_MANY_AMOUNT);
		return perfumeCustomRepository.findRandom(amount)
			.stream()
			.map(PerfumeResponse::of)
			.collect(Collectors.toList());
	}

	public List<PerfumeResponse> getPerfumeByOverall(Integer amount) {
		if (amount > 10)
			throw new CustomException(ErrorCode.PERFUME_TOO_MANY_AMOUNT);
		return perfumeCustomRepository.findOrderByOverall(amount)
			.stream()
			.map(PerfumeResponse::of)
			.collect(Collectors.toList());
	}

	public Slice<PerfumeResponse> search(PerfumeSearchCondition condition, Pageable pageable) {
		return perfumeCustomRepository.searchByCondition(condition, pageable);
	}

	private static List<Note> getNotes(Perfume perfume) {
		return perfume.getPerfumeNotes().stream().map(PerfumeNote::getNote).collect(Collectors.toList());
	}
}
