package conference.clerker.domain.member.service;


import conference.clerker.domain.member.repository.MemberRepository;
import conference.clerker.domain.member.repository.ProfileRepository;
import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.member.schema.Profile;
import conference.clerker.global.aws.s3.S3FileService;
import conference.clerker.global.exception.ErrorCode;
import conference.clerker.global.exception.domain.AuthException;
import conference.clerker.global.jwt.JwtProvider;
import java.util.HashMap;
import java.util.Map;
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
    private final JwtProvider jwtProvider;

    @Transactional
    public String update(Long memberId, MultipartFile profileImage, String username) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new AuthException(ErrorCode.MEMBER_NOT_FOUND));

        Profile existingProfile = profileRepository.findByMember(member).orElse(null);

        String accessToken = null;

        if(!member.getUsername().equals(username)) {
            member.setUsername(username);
            accessToken = generateToken(member);
        }

        // 프로필 이미지가 업로드된 경우
        if (profileImage != null && !profileImage.isEmpty()) {
            // s3에서 삭제
            if (existingProfile != null) {
                s3FileService.deleteFile("profile", existingProfile.getFilename());
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
        return accessToken;
    }

    private String generateToken(Member member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", member.getUsername());
        claims.put("email", member.getEmail());
        return jwtProvider.generateToken(claims);
    }
}
