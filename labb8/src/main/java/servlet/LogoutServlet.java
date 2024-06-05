package servlet;

import entity.ChatUser;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Класс LogoutServlet наследуется от ChatServlet и предназначен для обработки выхода пользователя из чата.
public class LogoutServlet extends ChatServlet {
	private static final long serialVersionUID = 1L;

	// Метод doGet обрабатывает GET-запросы, связанные с выходом пользователя из системы.
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Получение имени пользователя из атрибутов сессии.
		String name = (String) request.getSession().getAttribute("name");

		// Проверка, что имя пользователя не равно null.
		if (name != null) {
			// Получение объекта пользователя из списка активных пользователей.
			ChatUser aUser = activeUsers.get(name);

			// Проверка, что пользователь существует и его идентификатор сессии совпадает с текущим идентификатором сессии.
			if (aUser != null && aUser.getSessionId().equals((String) request.getSession().getId())) {
				// Синхронизированный блок для безопасного удаления пользователя из списка активных пользователей.
				synchronized (activeUsers) {
					activeUsers.remove(name);
				}

				// Удаление атрибута имени из сессии.
				request.getSession().setAttribute("name", null);

				// Добавление куки с нулевым идентификатором сессии, чтобы очистить сохраненную сессию в браузере.
				response.addCookie(new Cookie("sessionId", null));

				// Перенаправление пользователя на главную страницу.
				response.sendRedirect(response.encodeRedirectURL("/labb8_war_exploded/"));
			} else {
				// Если проверка не пройдена, очистка куки и перенаправление на страницу входа.
				response.addCookie(new Cookie("sessionId", null));
				response.sendRedirect(response.encodeRedirectURL("/labb8_war_exploded/login.do"));
			}
		} else {
			// Если имя пользователя не найдено в сессии, очистка куки и перенаправление на страницу входа.
			response.addCookie(new Cookie("sessionId", null));
			response.sendRedirect(response.encodeRedirectURL("/labb8_war_exploded/login.do"));
		}
	}
}
