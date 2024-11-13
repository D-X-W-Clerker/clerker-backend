package conference.clerker.domain.member.service;


import conference.clerker.domain.member.repository.MemberRepository;
import conference.clerker.domain.member.repository.ProfileRepository;
import conference.clerker.domain.member.schema.Member;
import conference.clerker.domain.member.schema.Profile;
import conference.clerker.global.aws.s3.S3FileService;
import conference.clerker.global.exception.domain.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static conference.clerker.global.exception.ErrorCode.MEMBER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final S3FileService s3FileService;

    @Transactional
    public void update(Long memberId, @Valid MultipartFile profileImage, @Valid String username) {
        try {
            Member member = memberRepository.findById(memberId).orElseThrow(() -> new AuthException(MEMBER_NOT_FOUND));

            member.setUsername(username);

            String filename = profileImage.getOriginalFilename();
            String profileURL = s3FileService.uploadFile("profile", filename, profileImage);

            Profile profile = Profile.create(member, profileURL, filename);
            profileRepository.save(profile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
