package KTB3.yun.Joongul.common.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        System.out.println(">>> LoggingFilter ENTER: " + request.getMethod() + " " + request.getRequestURI());

        filterChain.doFilter(request, response);
        System.out.println(">>> LoggingFilter EXIT: " + request.getMethod() + " " + request.getRequestURI());

    }
}
