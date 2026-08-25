package com.foodreels.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foodreels.backend.dto.PreferenceRequestDTO;
import com.foodreels.backend.dto.PreferenceResponseDTO;
import com.foodreels.backend.entity.User;
import com.foodreels.backend.entity.UserPreference;
import com.foodreels.backend.exception.UserNotFoundException;
import com.foodreels.backend.repository.UserPreferenceRepository;
import com.foodreels.backend.repository.UserRepository;

@Service
public class UserPreferenceService {

    private final UserPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public UserPreferenceService(
            UserPreferenceRepository preferenceRepository,
            UserRepository userRepository) {

        this.preferenceRepository =
                preferenceRepository;

        this.userRepository =
                userRepository;
    }

    public PreferenceResponseDTO addPreference(
            String email,
            PreferenceRequestDTO requestDTO) {

        User user = findUserByEmail(email);

        String category =
                normalizeCategory(
                        requestDTO.getCategory()
                );

        UserPreference preference =
                preferenceRepository
                        .findByUser_IdAndCategoryIgnoreCase(
                                user.getId(),
                                category
                        )
                        .orElse(null);

        if (preference == null) {

            preference =
                    new UserPreference();

            preference.setUser(user);
            preference.setCategory(category);

            preference =
                    preferenceRepository.save(
                            preference
                    );
        }

        return toResponseDTO(preference);
    }

    public List<PreferenceResponseDTO>
            getMyPreferences(String email) {

        User user = findUserByEmail(email);

        return preferenceRepository
                .findByUser_IdOrderByCreatedAtAsc(
                        user.getId()
                )
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public void removePreference(
            String email,
            String category) {

        User user = findUserByEmail(email);

        String normalizedCategory =
                normalizeCategory(category);

        preferenceRepository
                .findByUser_IdAndCategoryIgnoreCase(
                        user.getId(),
                        normalizedCategory
                )
                .ifPresent(
                        preferenceRepository::delete
                );
    }

    private User findUserByEmail(
            String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));
    }

    private String normalizeCategory(
            String category) {

        return category
                .trim()
                .toUpperCase();
    }

    private PreferenceResponseDTO toResponseDTO(
            UserPreference preference) {

        PreferenceResponseDTO dto =
                new PreferenceResponseDTO();

        dto.setId(
                preference.getId()
        );

        dto.setCategory(
                preference.getCategory()
        );

        dto.setCreatedAt(
                preference.getCreatedAt()
        );

        return dto;
    }
}