package servlet;

import entity.ChatMessage;
import entity.ChatUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

// ChatServlet является базовым сервлетом для всех сервлетов чата и наследуется от HttpServlet.
public class ChatServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Активные пользователи чата хранятся в HashMap, где ключ - это имя пользователя.
	protected HashMap<String, ChatUser> activeUsers;

	// Список сообщений чата хранится в ArrayList.
	protected ArrayList<ChatMessage> messages;

	// Метод init() вызывается при инициализации сервлета.
	@SuppressWarnings("unchecked")
	public void init() throws ServletException {
		super.init();

		// Получение списка активных пользователей и сообщений из контекста сервлета.
		activeUsers = (HashMap<String, ChatUser>)getServletContext().getAttribute("activeUsers");
		messages = (ArrayList<ChatMessage>)getServletContext().getAttribute("messages");

		// Если список активных пользователей не существует, он создается и сохраняется в контексте сервлета.
		if (activeUsers == null) {
			activeUsers = new HashMap<String, ChatUser>();
			getServletContext().setAttribute("activeUsers", activeUsers);
		}

		// Если список сообщений не существует, он создается и сохраняется в контексте сервлета.
		if (messages == null) {
			messages = new ArrayList<ChatMessage>(100);
			getServletContext().setAttribute("messages", messages);
		}
	}

	// Метод checklogint() проверяет, зарегистрирован ли пользователь в чате.
	public boolean checklogint(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean b = false;

		// Проверка наличия имени пользователя в списке активных пользователей.
		for (ChatUser aUser : activeUsers.values()) {
			if (aUser.getName().equals((String)request.getSession().getAttribute("name"))) {
				b = true;
			}
		}

		// Если пользователь не найден, он перенаправляется на страницу входа.
		if (!b)
			response.sendRedirect(response.encodeRedirectURL("/labb8_war_exploded/login.do"));
		return b;
	}

	// Метод doGet() перенаправляет все GET-запросы на страницу просмотра чата.
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect(resp.encodeRedirectURL("/labb8_war_exploded/view.do"));
	}
}
