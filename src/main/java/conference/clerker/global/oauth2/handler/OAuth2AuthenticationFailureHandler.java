package conference.clerker.global.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {


        // 예외 메세지 url 인코딩
        String errorMessage = URLEncoder.encode(exception.getLocalizedMessage(), StandardCharsets.UTF_8);

        log.error("error : {}", errorMessage);

        // 인코딩된 메세지를 queryParam 에 추가 - 건이형이 알려줌
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/login/callback")
                .queryParam("error", errorMessage)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}