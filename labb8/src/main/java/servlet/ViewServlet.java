package servlet;

import entity.ChatUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// Сервлет ViewServlet наследуется от ChatServlet и предназначен для отображения главной страницы чата.
public class ViewServlet extends ChatServlet {
	private static final long serialVersionUID = 1L;

	// Метод doGet обрабатывает GET-запросы к сервлету.
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Установка кодировки ответа на UTF-8 для корректного отображения кириллицы.
		response.setCharacterEncoding("utf8");

		// Переменная для проверки, зарегистрирован ли пользователь в чате.
		boolean b = false;

		// Проверка, есть ли пользователь с таким именем в списке активных пользователей.
		for (ChatUser aUser : activeUsers.values()) {
			if (aUser.getName().equals((String) request.getSession().getAttribute("name"))) {
				b = true;
			}
		}

		// Если пользователь не найден, перенаправление на страницу входа.
		if (!b)
			response.sendRedirect(response.encodeRedirectURL("/labb8_war_exploded/login.do"));

		// Создание объекта PrintWriter для отправки HTML-контента.
		PrintWriter pw = response.getWriter();

		// Начало HTML-документа с указанием кодировки и заголовка.
		pw.println("<!DOCTYPE html>\n<html>" +
				"<head>" +
				"<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>" +
				"<title>Чат: Сообщения</title>" +
				"</head>");

		// Создание фреймсета для разделения страницы на различные части: список сообщений, список пользователей и форму отправки сообщения.
		pw.println("<frameset rows=\"90,10\">" +
				"<frameset cols=\"80,20\">" +
				"<frame name=\"frameMessages\" src=\"/labb8_war_exploded/messages.do\">" +
				"<frame name=\"frameUsers\" src=\"/labb8_war_exploded/users.do\" noresize>" +
				"</frameset>" +
				"<frame name=\"frameMessage\" src=\"/labb8_war_exploded/WebContent/compose_message.html\" noresize>" +
				"</frameset>");

		// Закрытие HTML-документа.
		pw.println("</html>");
	}
}
