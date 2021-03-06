package io.github.lzmz.meetups.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private final SecurityResponder securityResponder;

    public JwtAuthenticationEntryPoint(SecurityResponder securityResponder) {
        this.securityResponder = securityResponder;
    }

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException authException) throws IOException {
        securityResponder.unexpectedJwt(res);
    }
}
