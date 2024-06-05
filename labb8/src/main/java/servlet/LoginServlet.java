package servlet;

import entity.ChatUser;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

// Класс LoginServlet наследуется от ChatServlet и предназначен для обработки входа пользователя в чат.
public class LoginServlet extends ChatServlet {
	private static final long serialVersionUID = 1L;
	// Время жизни сессии в секундах.
	private int sessionTimeout = 10*60;

	// Метод init вызывается при инициализации сервлета.
	public void init() throws ServletException {
		super.init();
		// Получение значения таймаута сессии из конфигурации сервлета.
		String value = getServletConfig().getInitParameter("SESSION_TIMEOUT");
		if (value != null) {
			sessionTimeout = Integer.parseInt(value);
		}
	}

	// Метод doGet обрабатывает GET-запросы, связанные с входом пользователя.
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Получение имени пользователя и сообщения об ошибке из сессии.
		String name = (String)request.getSession().getAttribute("name");
		String errorMessage = (String)request.getSession().getAttribute("error");
		String previousSessionId = null;

		// Проверка наличия кукисов и поиск кукиса сессии.
		if (name == null && request.getCookies() != null) {
			for (Cookie aCookie : request.getCookies()) {
				if (aCookie.getName().equals("sessionId")) {
					previousSessionId = aCookie.getValue();
					break;
				}
			}

			// Если найден кукис сессии, попытка восстановить сессию пользователя.
			if (previousSessionId != null) {
				for (ChatUser aUser : activeUsers.values()) {
					if (aUser.getSessionId().equals(previousSessionId)) {
						name = aUser.getName();
						aUser.setSessionId(request.getSession().getId());
					}
				}
			}
		}

		// Если имя пользователя не пустое, обработка попытки входа.
		if (name != null && !"".equals(name)) {
			errorMessage = processLogonAttempt(name, request, response);
		}

		// Установка кодировки ответа и отправка HTML-формы входа.
		response.setCharacterEncoding("utf8");
		PrintWriter pw = response.getWriter();
		pw.println("<!DOCTYPE html>\n<html><head><title>Чат</title>" +
				"<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>" +
				"</head>");
		if (errorMessage != null) {
			pw.println("<p><font color='red'>" + errorMessage + "</font></p>");
		}
		pw.println("<form action='/labb8_war_exploded/' method='post'>Введите имя: <input type='text' name='name' value=''>" +
				"<input type='submit' value='Войти в чат'>");
		pw.println("</form></body></html>");
		request.getSession().setAttribute("error", null);
	}

	// Метод doPost обрабатывает POST-запросы, связанные с входом пользователя.
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String name = (String)request.getParameter("name");
		String errorMessage = null;

		// Проверка, что имя пользователя не пустое.
		if (name == null || "".equals(name)) {
			errorMessage = "Имя пользователя не может быть пустым!";
		} else {
			errorMessage = processLogonAttempt(name, request, response);
		}

		// Если есть сообщение об ошибке, установка атрибутов сессии и перенаправление на страницу входа.
		if (errorMessage != null) {
			request.getSession().setAttribute("name", null);
			request.getSession().setAttribute("error", errorMessage);
			response.sendRedirect(response.encodeRedirectURL("/labb8_war_exploded/view.do"));
		}
	}

	// Метод processLogonAttempt обрабатывает попытку входа пользователя.
	String processLogonAttempt(String name, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String sessionId = request.getSession().getId();
		ChatUser aUser = activeUsers.get(name);

		// Если пользователя нет в списке активных, он добавляется.
		if (aUser == null) {
			aUser = new ChatUser(name, Calendar.getInstance().getTimeInMillis(), sessionId);
			synchronized (activeUsers) {
				activeUsers.put(aUser.getName(), aUser);
			}
		}

		// Проверка идентификатора сессии и времени последнего взаимодействия.
		if (aUser.getSessionId().equals(sessionId) ||
				aUser.getLastInteractionTime() < (Calendar.getInstance().getTimeInMillis() - sessionTimeout * 1000)) {
			request.getSession().setAttribute("name", name);
			aUser.setLastInteractionTime(Calendar.getInstance().getTimeInMillis());

			// Установка кукиса сессии с максимальным возрастом.
			Cookie sessionIdCookie = new Cookie("sessionId", sessionId);
			sessionIdCookie.setMaxAge(60*60*24*365);
			response.addCookie(sessionIdCookie);

			// Очистка атрибута приватных сообщений и перенаправление на страницу чата.
			request.getSession().setAttribute("privatem", null);
			response.sendRedirect(response.encodeRedirectURL("/labb8_war_exploded/view.do"));
			return null;
		} else {
			// Если имя пользователя уже занято, возвращается сообщение об ошибке.
			return "Извините, но имя <strong>" + name + "</strong> уже кем-то занято. Пожалуйста выберите другое имя!";
		}
	}
}
