package com.roommate.service;

import com.roommate.dto.RoommatePreferenceRequest;
import com.roommate.model.RoommatePreference;
import com.roommate.model.User;
import com.roommate.repository.RoommatePreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PreferenceService {

    private final RoommatePreferenceRepository preferenceRepository;

    public PreferenceService(RoommatePreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    public Optional<RoommatePreference> findByUserId(Long userId) {
        return preferenceRepository.findByUserId(userId);
    }

    @Transactional
    public RoommatePreference upsert(User user, RoommatePreferenceRequest request) {
        RoommatePreference preference = preferenceRepository.findByUserId(user.getId())
                .orElseGet(RoommatePreference::new);

        preference.setUser(user);
        preference.setPreferredCity(request.getPreferredCity());
        preference.setMinBudget(request.getMinBudget());
        preference.setMaxBudget(request.getMaxBudget());
        preference.setPreferredGender(request.getPreferredGender());
        preference.setSmokingAllowed(request.getSmokingAllowed());
        preference.setPetsAllowed(request.getPetsAllowed());
        preference.setCleanlinessPreference(request.getCleanlinessPreference());
        preference.setSleepSchedule(request.getSleepSchedule());

        return preferenceRepository.save(preference);
    }
}
