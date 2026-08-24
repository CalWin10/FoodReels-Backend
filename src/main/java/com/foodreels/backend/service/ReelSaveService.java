package com.foodreels.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foodreels.backend.dto.ReelResponseDTO;
import com.foodreels.backend.dto.SaveResponseDTO;
import com.foodreels.backend.entity.Reel;
import com.foodreels.backend.entity.ReelSave;
import com.foodreels.backend.entity.User;
import com.foodreels.backend.exception.ReelNotFoundException;
import com.foodreels.backend.exception.UserNotFoundException;
import com.foodreels.backend.mapper.ReelMapper;
import com.foodreels.backend.repository.ReelRepository;
import com.foodreels.backend.repository.ReelSaveRepository;
import com.foodreels.backend.repository.UserRepository;

@Service
public class ReelSaveService {

    private final ReelSaveRepository reelSaveRepository;
    private final ReelRepository reelRepository;
    private final UserRepository userRepository;
    private final ReelMapper reelMapper;

    public ReelSaveService(
            ReelSaveRepository reelSaveRepository,
            ReelRepository reelRepository,
            UserRepository userRepository,
            ReelMapper reelMapper) {

        this.reelSaveRepository = reelSaveRepository;
        this.reelRepository = reelRepository;
        this.userRepository = userRepository;
        this.reelMapper = reelMapper;
    }

    public SaveResponseDTO saveReel(
            Long reelId,
            String email) {

        User user = findUserByEmail(email);
        Reel reel = findReelById(reelId);

        boolean alreadySaved =
                reelSaveRepository.existsByUser_IdAndReel_Id(
                        user.getId(),
                        reelId
                );

        if (!alreadySaved) {

            ReelSave save = new ReelSave();

            save.setUser(user);
            save.setReel(reel);

            reelSaveRepository.save(save);
        }

        long savedCount =
                reelSaveRepository.countByUser_Id(
                        user.getId()
                );

        return new SaveResponseDTO(
                true,
                savedCount
        );
    }

    public SaveResponseDTO unsaveReel(
            Long reelId,
            String email) {

        User user = findUserByEmail(email);

        findReelById(reelId);

        reelSaveRepository
                .findByUser_IdAndReel_Id(
                        user.getId(),
                        reelId
                )
                .ifPresent(reelSaveRepository::delete);

        long savedCount =
                reelSaveRepository.countByUser_Id(
                        user.getId()
                );

        return new SaveResponseDTO(
                false,
                savedCount
        );
    }

    public SaveResponseDTO getSaveStatus(
            Long reelId,
            String email) {

        User user = findUserByEmail(email);

        findReelById(reelId);

        boolean saved =
                reelSaveRepository.existsByUser_IdAndReel_Id(
                        user.getId(),
                        reelId
                );

        long savedCount =
                reelSaveRepository.countByUser_Id(
                        user.getId()
                );

        return new SaveResponseDTO(
                saved,
                savedCount
        );
    }

    public List<ReelResponseDTO> getSavedReels(
            String email) {

        User user = findUserByEmail(email);

        return reelSaveRepository
                .findByUser_IdOrderByCreatedAtDesc(
                        user.getId()
                )
                .stream()
                .map(ReelSave::getReel)
                .map(reelMapper::toResponseDTO)
                .toList();
    }

    private User findUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));
    }

    private Reel findReelById(Long reelId) {

        return reelRepository.findById(reelId)
                .orElseThrow(() ->
                        new ReelNotFoundException(
                                "Reel not found with id: " + reelId
                        ));
    }
}