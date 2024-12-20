package conference.clerker.domain.notification.controller;

import conference.clerker.domain.notification.dto.response.NotificationsResponseDTO;
import conference.clerker.domain.notification.service.NotificationService;
import conference.clerker.global.oauth2.service.OAuth2UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notify")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "알림 목록", description = "알림 예시> 0729 자이 회의 이(가) 09/27 ~ 09/28 까지 회의 이(가) 있습니다.")
    public ResponseEntity<List<NotificationsResponseDTO>> findAll(
            @AuthenticationPrincipal OAuth2UserPrincipal principal) {
        return ResponseEntity.ok().body(notificationService.findAllByMemberId(principal.getMember().getId()));
    }

    @DeleteMapping("/{notificationID}")
    @Operation(summary = "알림 삭제")
    public ResponseEntity<Void> delete(
            @Parameter(required = true, description = "프로젝트 Id" ,in = ParameterIn.PATH)
            @PathVariable("notificationID") Long notificationId) {

        notificationService.delete(notificationId);

        return ResponseEntity.noContent().build();
    }
}
