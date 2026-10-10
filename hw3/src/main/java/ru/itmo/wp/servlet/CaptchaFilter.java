package ru.itmo.wp.servlet;

import ru.itmo.wp.util.ImageUtils;
import ru.itmo.wp.util.ServletUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Random;

public class CaptchaFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!request.getRequestURI().endsWith(".css") &&
                !request.getRequestURI().endsWith(".png")&&
                !request.getRequestURI().endsWith(".js")){
            HttpSession session = request.getSession();
            String result = session.getAttribute("result") == null ? null : session.getAttribute("result").toString();
            String userResult = session.getAttribute("userResult") == null ? null : session.getAttribute("userResult").toString();
            if (result == null || !result.equals(userResult)){
                if (request.getRequestURI().endsWith("/captcha") && request.getParameter("userResult") != null){
                    userResult = request.getParameter("userResult").strip();
                    session.setAttribute("userResult", userResult);
                    if (userResult.equals(result)){
                        response.sendRedirect("/index.html");
                        return;
                    }
                }
                result = Integer.toString(100 + new Random().nextInt(900));
                session.setAttribute("result", result);
                writeResponse(response, result, session.getId());
                return;
            }
        }

        super.doFilter(request, response, chain);
    }

    private void writeResponse(HttpServletResponse response, String captchaNumber, String id) throws IOException {
        response.setContentType("text/html");
        Path path = Paths.get("src/main/webapp/static/img/" + id + "/captcha.png");
        Files.createDirectories(path.getParent());
        Files.write(path, ImageUtils.toPng(captchaNumber));
        try (OutputStream outputStream = response.getOutputStream()) {
            outputStream.write(String.format(Files.readString(Paths.get("src/main/webapp/static/captcha.html")), id).getBytes());
        }
    }
}
