package ru.itmo.wp.servlet;

import com.google.gson.Gson;
import ru.itmo.wp.dto.MessageDTO;
import ru.itmo.wp.entity.Message;
import ru.itmo.wp.util.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageServlet extends HttpServlet {
    List<Message> repositoryMessages = new CopyOnWriteArrayList<>();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uriRequest = request.getRequestURI();
        if (ServletUtils.hasUriDots(uriRequest, response)) return;

        response.setContentType("application/json");

        if (uriRequest.endsWith("/auth")){
            auth(request, response);
        }else if (uriRequest.endsWith("/findAll")){
            findAll(request, response);
        } else if (uriRequest.endsWith("/add")){
            add(request, response);
        }
    }

    private void auth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter("user");
        if (user == null || user.isBlank()){
            return;
        }
        user = user.strip();
        request.getSession().setAttribute("user", user);
        ServletUtils.sendObjectGSON(user, response.getWriter());
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LocalDateTime time = LocalDateTime.now();
        String text = request.getParameter("text");
        String user = (String) request.getSession().getAttribute("user");
        if (user == null || text == null || text.isBlank()){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        text = text.strip();
        repositoryMessages.add(new Message(user, text, time));
    }

    private void findAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<MessageDTO> responseObject = repositoryMessages.stream()
                .sorted(Comparator.comparing(Message::createdAt))
                .map(m -> new MessageDTO(m.user(), m.text())).toList();
        ServletUtils.sendObjectGSON(responseObject, response.getWriter());
    }
}
