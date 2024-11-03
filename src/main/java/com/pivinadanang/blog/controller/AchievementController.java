package com.pivinadanang.blog.controller;

import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.dtos.AchievementDTO;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.achievement.AchievementResponse;
import com.pivinadanang.blog.services.achievement.IAchievementService;
import com.pivinadanang.blog.ultils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/achievements")
@Validated
@RequiredArgsConstructor
public class AchievementController {
    private final IAchievementService achievementService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> insertAchievement(@Valid @RequestBody AchievementDTO achievementDTO) throws Exception {
        if(achievementService.existsAchievementByKey(achievementDTO.getKey())){
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.ACHIEVEMENT_ALREADY_EXISTS))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());

        }
        AchievementResponse achievement = achievementService.addAchievement(achievementDTO) ;
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_ACHIEVEMENT_SUCCESSFULLY))
                .status(HttpStatus.CREATED)
                .data(achievement)
                .build());
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> updateAchievement(@PathVariable Long id,
                                                            @Valid @RequestBody AchievementDTO achievementDTO) throws Exception {
        if(achievementService.existsAchievementByKey(achievementDTO.getKey())){
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.ACHIEVEMENT_ALREADY_EXISTS))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());

        }
        AchievementResponse achievement = achievementService.updateAchievement(id, achievementDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_ACHIEVEMENT_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(achievement)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> deleteAchievement(@PathVariable Long id) throws Exception {
        achievementService.deleteAchievement(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_ACHIEVEMENT_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllAchievements() {
        List<AchievementResponse> achievements = achievementService.getAllAchievements();
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get list of achievements successfully")
                .status(HttpStatus.OK)
                .data(achievements)
                .build());
    }
}
