package servlet;

import entity.ChatMessage;
import entity.ChatUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;

// Класс NewMessageServlet наследуется от ChatServlet и предназначен для обработки отправки новых сообщений в чат.
public class NewMessageServlet extends ChatServlet {
	private static final long serialVersionUID = 1L;

	// Метод doPost обрабатывает POST-запросы, отправленные из формы ввода сообщений.
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Установка кодировки запроса на UTF-8 для корректного приема сообщений.
		request.setCharacterEncoding("UTF-8");

		// Инициализация переменной для имени получателя приватного сообщения.
		String pname = null;

		// Получение текста сообщения из параметров запроса.
		String message = (String) request.getParameter("message");

		// Проверка, что сообщение не пустое.
		if (message != null && !"".equals(message)) {
			// Получение имени получателя приватного сообщения из атрибутов сессии, если оно есть.
			String privatem = (String) request.getSession().getAttribute("privatem");
			if (privatem != null && !"toall".equals(privatem))
				pname = privatem;

			// Получение объекта пользователя-автора сообщения из списка активных пользователей.
			ChatUser author = activeUsers.get((String) request.getSession().getAttribute("name"));

			// Синхронизированный блок для безопасного добавления нового сообщения в список сообщений.
			synchronized (messages) {
				messages.add(new ChatMessage(message, author, Calendar.getInstance().getTimeInMillis(), pname));
			}
		}

		// Перенаправление пользователя на страницу ввода сообщений после отправки сообщения.
		response.sendRedirect("/labb8_war_exploded/WebContent/compose_message.html");
	}
}
