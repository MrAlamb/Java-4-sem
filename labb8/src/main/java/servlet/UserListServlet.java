package servlet;

import entity.ChatUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// Класс UserListServlet расширяет функциональность ChatServlet для обработки списка пользователей чата.
public class UserListServlet extends ChatServlet {
	private static final long serialVersionUID = 1L;

	// Метод doGet обрабатывает GET-запросы и отображает список пользователей чата.
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Установка кодировки ответа на UTF-8 для корректного отображения текста.
		response.setCharacterEncoding("utf8");

		// Создание объекта PrintWriter для отправки HTML-контента клиенту.
		PrintWriter pw = response.getWriter();

		// Получение количества активных пользователей чата.
		Integer count = activeUsers.size();

		// Начало HTML-документа с мета-тегами, включая автообновление страницы каждые 5 секунд.
		pw.println("<!DOCTYPE html>\n<html><head>" +
				"<title></title>" +
				"<meta charset='UTF-8' />" +
				"<meta http-equiv='refresh' content='5'>" +
				"</head>");

		// Начало тела документа.
		pw.println("<body>");

		// Стили для элемента SELECT, чтобы он занимал всю ширину.
		pw.println("<style type=\"text/css\">" +
				"SELECT {" +
				"width: 100%;" +
				"}" +
				" </style>");

		// Форма для отправки выбранного пользователя из списка.
		pw.println("<form id=mform action=\"/labb8_war_exploded/users.do\" method=\"post\">");

		// Создание выпадающего списка пользователей с размером, зависящим от количества пользователей.
		pw.println("<select size=\"" + new Integer(count.intValue() + 2).toString() + "\" name=\"userlist\" " +
				"onchange=\"javascript:document.forms['mform'].submit();\">");

		// Опция для отправки сообщения всем пользователям.
		pw.println("<option value = 'toall'>Всем</option>");

		// Получение имени текущего пользователя из сессии.
		String uname = (String) request.getSession().getAttribute("name");

		// Добавление остальных пользователей в список, исключая текущего пользователя.
		for (ChatUser aUser : activeUsers.values()) {
			if (aUser.getName().equals(uname))
				continue;
			pw.println("<option>" + aUser.getName() + "</option>");
		}

		// Закрытие списка и формы.
		pw.println("</select>");
		pw.println("</form>");

		// Проверка, выбран ли приватный режим для отправки сообщений.
		String privatem = (String) request.getSession().getAttribute("privatem");
		boolean b = false;
		for (ChatUser aUser : activeUsers.values()) {
			if (aUser.getName().equals(privatem))
				b = true;
		}

		// Если выбранный пользователь не найден, устанавливается режим "Всем".
		if (!b) {
			privatem = "toall";
		}

		// Сообщение о текущем режиме отправки сообщений.
		String m = "Вы отправляете сообщения";
		if (privatem == null || "toall".equals(privatem))
			m += " всем";
		else
			m += ("\nпользователю: " + privatem);

		// Вывод сообщения и закрытие HTML-документа.
		pw.println(m);
		pw.println("</body></html>");
	}

	// Метод doPost обрабатывает POST-запросы, устанавливает режим отправки сообщений и вызывает doGet.
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Установка кодировки запроса на UTF-8.
		request.setCharacterEncoding("UTF-8");

		// Получение выбранного пользователя из параметров запроса.
		String privatem = (String) request.getParameter("userlist");

		// Установка выбранного пользователя в атрибуты сессии.
		request.getSession().setAttribute("privatem", privatem);

		// Вызов метода doGet для продолжения обработки запроса.
		doGet(request, response);
	}
}
