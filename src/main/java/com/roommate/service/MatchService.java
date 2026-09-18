package com.roommate.service;

import com.roommate.dto.MatchResult;
import com.roommate.model.Room;
import com.roommate.model.RoommatePreference;
import com.roommate.model.User;
import com.roommate.repository.RoomRepository;
import com.roommate.repository.RoommatePreferenceRepository;
import com.roommate.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class MatchService {

    private final RoomRepository roomRepository;
    private final RoommatePreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public MatchService(RoomRepository roomRepository,
                        RoommatePreferenceRepository preferenceRepository,
                        UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
    }

    public List<MatchResult> findMatchesForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Optional<RoommatePreference> userPreference = preferenceRepository.findByUserId(userId);
        List<Room> candidateRooms = roomRepository.findByOwnerIdNot(userId);

        List<MatchResult> results = new ArrayList<>();
        for (Room room : candidateRooms) {
            Score score = scoreRoom(user, userPreference.orElse(null), room);
            results.add(new MatchResult(room, score.value, String.join("; ", score.reasons)));
        }

        results.sort(Comparator.comparingInt(MatchResult::getScore).reversed());
        return results;
    }

    private Score scoreRoom(User user, RoommatePreference pref, Room room) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        if (pref != null && equalsIgnoreCase(pref.getPreferredCity(), room.getLocation())) {
            score += 30;
            reasons.add("Preferred city matches room location");
        }

        if (pref != null && withinBudget(pref.getMinBudget(), pref.getMaxBudget(), room.getMonthlyRent())) {
            score += 30;
            reasons.add("Rent is within preferred budget");
        }

        if (pref != null && equalsIgnoreCase(pref.getPreferredGender(), room.getOwner().getGender())) {
            score += 15;
            reasons.add("Owner matches preferred gender");
        }

        Optional<RoommatePreference> ownerPref = preferenceRepository.findByUserId(room.getOwner().getId());
        if (pref != null && ownerPref.isPresent()) {
            if (pref.getSmokingAllowed() != null && pref.getSmokingAllowed().equals(ownerPref.get().getSmokingAllowed())) {
                score += 10;
                reasons.add("Smoking preference aligns");
            }
            if (pref.getPetsAllowed() != null && pref.getPetsAllowed().equals(ownerPref.get().getPetsAllowed())) {
                score += 10;
                reasons.add("Pet preference aligns");
            }
            if (equalsIgnoreCase(pref.getCleanlinessPreference(), ownerPref.get().getCleanlinessPreference())) {
                score += 5;
                reasons.add("Cleanliness preference aligns");
            }
            if (equalsIgnoreCase(pref.getSleepSchedule(), ownerPref.get().getSleepSchedule())) {
                score += 5;
                reasons.add("Sleep schedule aligns");
            }
        }

        if (reasons.isEmpty()) {
            reasons.add("General availability match based on provided details");
        }

        return new Score(score, reasons);
    }

    private boolean withinBudget(BigDecimal min, BigDecimal max, BigDecimal rent) {
        if (rent == null) {
            return false;
        }

        BigDecimal effectiveMin = min == null ? BigDecimal.ZERO : min;
        BigDecimal effectiveMax = max == null ? new BigDecimal("1000000") : max;

        return rent.compareTo(effectiveMin) >= 0 && rent.compareTo(effectiveMax) <= 0;
    }

    private boolean equalsIgnoreCase(String left, String right) {
        if (left == null || left.isBlank() || right == null || right.isBlank()) {
            return false;
        }
        return left.toLowerCase(Locale.ROOT).trim().equals(right.toLowerCase(Locale.ROOT).trim());
    }

    private record Score(int value, List<String> reasons) {
    }
}
