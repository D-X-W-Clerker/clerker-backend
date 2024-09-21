package conference.clerker.domain.schedule.controller;


import conference.clerker.domain.meeting.dto.response.FindMeetingsDTO;
import conference.clerker.domain.meeting.service.MeetingService;
import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.notification.service.NotificationService;
import conference.clerker.domain.organization.service.OrganizationService;
import conference.clerker.domain.schedule.dto.request.CreateScheduleRequestDTO;
import conference.clerker.domain.schedule.dto.response.FindSchedulesDTO;
import conference.clerker.domain.schedule.dto.request.JoinScheduleRequestDTO;
import conference.clerker.domain.schedule.dto.response.ScheduleTimeWithMemberInfoDTO;
import conference.clerker.domain.schedule.dto.response.SchedulesAndMeetingsListResponseDTO;
import conference.clerker.domain.schedule.service.ScheduleService;
import conference.clerker.domain.schedule.service.ScheduleTimeService;
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
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final MeetingService meetingService;
    private final ScheduleTimeService scheduleTimeService;
    private final NotificationService notificationService;
    private final OrganizationService organizationService;

    @PostMapping("/create/{projectID}")
    @Operation(summary = "스케쥴 생성 API", description = "스케쥴 생성 API")
    public ResponseEntity<Void> createSchedule(
            @AuthenticationPrincipal Member member,
            @Parameter(required = true, description = "프로젝트 ID", in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId,
            @RequestBody CreateScheduleRequestDTO requestDTO) {
        scheduleService.create(projectId, member.getId(), requestDTO);
        if (requestDTO.isNotify()) {
            notificationService.notify(member.getId(),
                    projectId,
                    requestDTO.name(),
                    requestDTO.startDate(),
                    requestDTO.endDate(),
                    "회의 스케쥴 조율");
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{projectID}")
    @Operation(summary = "프로젝트 캘린더 목록 API", description = "특정 프로젝트의 Meeting 및 Schedule 목록")
    public ResponseEntity<SchedulesAndMeetingsListResponseDTO> findAllSchedules(
            @Parameter(required = true, description = "프로젝트 ID", in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId) {
        List<FindSchedulesDTO> scheduleList = scheduleService.findByProjectId(projectId);
        List<FindMeetingsDTO> meetingList = meetingService.findByProjectId(projectId);
        return ResponseEntity.ok().body(new SchedulesAndMeetingsListResponseDTO(scheduleList, meetingList));
    }

    @PostMapping("/{scheduleID}")
    @Operation(summary = "개인별 스케쥴 참여 API", description = "시간표 드래그 후 입력 시 요청, 30분 단위로 보내주세용")
    public ResponseEntity<Void> joinSchedule(
            @Parameter(required = true, description = "스케쥴 ID", in = ParameterIn.PATH)
            @PathVariable("scheduleID") Long scheduleId,
            @AuthenticationPrincipal Member member,
            @RequestBody JoinScheduleRequestDTO requestDTO) {

        scheduleTimeService.create(scheduleId, requestDTO.timeTable(), member.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/detail/{projectID}/{scheduleID}")
    @Operation(summary = "스케쥴 상세 조회 API", description = "개인별 입력한 스케쥴 시간 List + 참여한 인원 List 조회")
    public ResponseEntity<List<ScheduleTimeWithMemberInfoDTO>> detailSchedule(
            @Parameter(required = true, description = "프로젝트 ID", in = ParameterIn.PATH)
            @PathVariable("projectID") Long projectId,
            @Parameter(required = true, description = "스케쥴 ID", in = ParameterIn.PATH)
            @PathVariable("scheduleID") Long scheduleId) {
        return ResponseEntity.ok().body(scheduleTimeService.findScheduleTimeAndMemberInfo(scheduleId, projectId));
    }
}
