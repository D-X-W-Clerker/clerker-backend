package conference.clerker.domain.member.service;


import conference.clerker.domain.member.repository.MemberRepository;
import conference.clerker.domain.member.repository.ProfileRepository;
import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.member.schema.Profile;
import conference.clerker.global.aws.s3.S3FileService;
import conference.clerker.global.exception.ErrorCode;
import conference.clerker.global.exception.domain.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final S3FileService s3FileService;

    @Transactional
    public void update(Long memberId, MultipartFile profileImage, String username) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new AuthException(ErrorCode.MEMBER_NOT_FOUND));

        //구현 시작 기존 프로필 가져오기
        Profile existingProfile = profileRepository.findByMember(member).orElse(null);

        //username 만 업데이트 ( jpa 더티체킹으로?)
        member.setUsername(username);

        // 프로필 이미지가 업로드된 경우
        if (profileImage != null && !profileImage.isEmpty()) {
            // s3에서 삭제
            if (existingProfile != null) {
                s3FileService.deleteFile(profileImage.getOriginalFilename());
            }
            String filename = profileImage.getOriginalFilename();
            String profileURL = null;
            try {
                profileURL = s3FileService.uploadFile("profile", filename, profileImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 기존 프로필 수정 or 새롭게 생성
            if (existingProfile != null) {
                existingProfile.setUrl(profileURL);
                existingProfile.setFilename(filename);
            } else {
                Profile profile = Profile.create(member, profileURL, filename);
                profileRepository.save(profile);
            }
        }
    }
}
