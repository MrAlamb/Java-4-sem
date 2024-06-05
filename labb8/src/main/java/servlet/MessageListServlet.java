package servlet;

import entity.ChatMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// Класс MessageListServlet наследуется от ChatServlet и предназначен для отображения списка сообщений чата.
public class MessageListServlet extends ChatServlet {
	private static final long serialVersionUID = 1L;

	// Метод doGet обрабатывает GET-запросы и отображает список сообщений.
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Установка кодировки ответа на UTF-8 для корректного отображения текста.
		response.setCharacterEncoding("utf8");

		// Создание объекта PrintWriter для отправки HTML-контента клиенту.
		PrintWriter pw = response.getWriter();

		// Начало HTML-документа с мета-тегами, включая автообновление страницы каждую секунду.
		pw.println("<!DOCTYPE html>\n<html>" +
				"<head>" +
				"<title></title>" +
				"<meta charset='UTF-8' />" +
				"<meta http-equiv='refresh' content='1'>" +
				"</head>");

		// Начало тела документа.
		pw.println("<body>");

		// Вывод имени текущего пользователя, полученного из атрибутов сессии.
		pw.println("<div><strong>" + (String)request.getSession().getAttribute("name") + "</strong></div>");

		// Цикл для обхода списка сообщений в обратном порядке (от новых к старым).
		for (int i = messages.size() - 1; i >= 0; i--) {
			ChatMessage aMessage = messages.get(i);
			String priv = aMessage.getPrivatem();
			String auth = aMessage.getAuthor().getName();
			String uname = (String)request.getSession().getAttribute("name");

			// Проверка, является ли сообщение приватным.
			if (priv != null) {
				// Если сообщение приватное, выводится только если отправитель или получатель - текущий пользователь.
				if (priv.equals(uname) || auth.equals(uname)) {
					pw.println("<div><strong>" + aMessage.getAuthor().getName()
							+ "</strong>: " + aMessage.getMessage() + "</div>");
				}
			} else {
				// Если сообщение не приватное, оно выводится для всех пользователей.
				pw.println("<div><strong>" + aMessage.getAuthor().getName()
						+ "</strong>: " + aMessage.getMessage() + "</div>");
			}
		}

		// Закрытие тела и HTML-документа.
		pw.println("</body></html>");
	}
}
