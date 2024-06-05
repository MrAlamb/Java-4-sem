package com.example.labb8;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

// Аннотация WebServlet используется для объявления сервлета, указывается имя сервлета и URL-паттерн.
@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    // Переменная для хранения сообщения, которое будет отображаться на веб-странице.
    private String message;

    // Метод init() вызывается при инициализации сервлета.
    public void init() {
        // Инициализация переменной message значением "Hello World!".
        message = "Hello World!";
    }

    // Метод doGet() обрабатывает GET-запросы к сервлету.
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Установка типа содержимого ответа на "text/html".
        response.setContentType("text/html");

        // Получение объекта PrintWriter для отправки HTML в ответ.
        PrintWriter out = response.getWriter();
        // Начало HTML-документа.
        out.println("<html><body>");
        // Вывод сообщения в теге <h1>.
        out.println("<h1>" + message + "</h1>");
        // Завершение HTML-документа.
        out.println("</body></html>");
    }

    // Метод destroy() вызывается при остановке сервлета.
    public void destroy() {
    }
}
