package com.roommate.service;

import com.roommate.dto.MatchResult;
import com.roommate.model.Room;
import com.roommate.model.RoommatePreference;
import com.roommate.model.User;
import com.roommate.repository.RoomRepository;
import com.roommate.repository.RoommatePreferenceRepository;
import com.roommate.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoommatePreferenceRepository preferenceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MatchService matchService;

    @Test
    void matchesAreRankedByScore() {
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setCity("Ahmedabad");

        RoommatePreference pref = new RoommatePreference();
        pref.setPreferredCity("Ahmedabad");
        pref.setMinBudget(new BigDecimal("8000"));
        pref.setMaxBudget(new BigDecimal("15000"));
        pref.setPreferredGender("Female");
        pref.setSmokingAllowed(false);
        pref.setPetsAllowed(true);
        pref.setCleanlinessPreference("Neat");
        pref.setSleepSchedule("Early");

        User owner1 = new User();
        owner1.setId(2L);
        owner1.setGender("Female");

        User owner2 = new User();
        owner2.setId(3L);
        owner2.setGender("Male");

        Room room1 = room("City Center Room", "Ahmedabad", "12000", owner1);
        Room room2 = room("Budget Room", "Delhi", "7000", owner2);

        RoommatePreference owner1Pref = new RoommatePreference();
        owner1Pref.setSmokingAllowed(false);
        owner1Pref.setPetsAllowed(true);
        owner1Pref.setCleanlinessPreference("Neat");
        owner1Pref.setSleepSchedule("Early");

        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(preferenceRepository.findByUserId(1L)).thenReturn(Optional.of(pref));
        when(roomRepository.findByOwnerIdNot(1L)).thenReturn(List.of(room2, room1));
        when(preferenceRepository.findByUserId(2L)).thenReturn(Optional.of(owner1Pref));
        when(preferenceRepository.findByUserId(3L)).thenReturn(Optional.empty());

        List<MatchResult> matches = matchService.findMatchesForUser(1L);

        assertThat(matches).hasSize(2);
        assertThat(matches.get(0).getRoom().getTitle()).isEqualTo("City Center Room");
        assertThat(matches.get(0).getScore()).isGreaterThan(matches.get(1).getScore());
        assertThat(matches.get(0).getSummary()).contains("Preferred city matches room location");
    }

    private Room room(String title, String city, String rent, User owner) {
        Room room = new Room();
        room.setId((long) title.hashCode());
        room.setTitle(title);
        room.setDescription(title + " description");
        room.setLocation(city);
        room.setMonthlyRent(new BigDecimal(rent));
        room.setAvailableFrom(LocalDate.now().plusDays(1));
        room.setOwner(owner);
        return room;
    }
}
