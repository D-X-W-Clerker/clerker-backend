package conference.clerker.global.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import conference.clerker.global.aws.AwsProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class S3FileService {

    private final AwsProperty awsProperty;
    private final AmazonS3 amazonS3;

    // 파일 업로드
    public String uploadFile(String key, MultipartFile file) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        return uploadFile(key, file.getInputStream(), objectMetadata);
    }

    public String uploadFile(String key, InputStream inputStream, ObjectMetadata objectMetadata) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                awsProperty.getS3().getBucket(),
                key,
                inputStream,
                objectMetadata
        ).withCannedAcl(CannedAccessControlList.PublicRead);

        amazonS3.putObject(putObjectRequest);

        return amazonS3.getUrl(awsProperty.getS3().getBucket(), key).toString();
    }

    // 파일 삭제
    public void deleteFile(String key) {
        amazonS3.deleteObject(awsProperty.getS3().getBucket(), key);
    }
}
