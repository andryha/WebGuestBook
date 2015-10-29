package com.suhorukov.dubovik.web;

import com.suhorukov.dubovik.guestbook.GuestBook;
import com.suhorukov.dubovik.guestbook.Record;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


@WebServlet(name = "GuestBook", urlPatterns = {"/"})
public class GuestBookServlet extends HttpServlet {

    @Resource(name = "jdbc/testDS")
    private DataSource ds;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try (Connection connection = ds.getConnection();
             GuestBook guestBook = new GuestBook(connection)) {

            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\" />");
            out.println("<title>WebGuestBook</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h2>Гостевая книга</h2>");

            String message = request.getParameter("message");
            if (message != null)
                if (message.length() > 0 && !"Введите здесь ваш отзыв.".equals(message)) {
                    guestBook.addRecord(message);
                } else {
                    out.println("Нет параметров, введите текст.<br>");
                }
            out.println("<P>");
            out.print("<form ");
            out.println("method=POST>");
            out.println("<textarea name=\"message\" style=\"width: 350px\" spellcheck=true>Введите здесь ваш отзыв.</textarea>");
            out.println("<br>");
            out.println("<input type=submit>");
            out.println("</form>");
            out.println("<u><b>Книга</b></u>");
            out.println("<table border=\"1\">");
            out.println("<thead>");
            out.println("<td><b>ID</b></td>");
            out.println("<td><b>postDate</b></td>");
            out.println("<td><b>postMessage</b></td>");
            out.println("</thead>");

            List<Record> records = guestBook.getRecords();
            if (records.size() == 0) {
                out.println("</table>");
                out.println("<h3>В базе нет элементов для отображения</h3>");
            } else {
                for (Record record : records) {
                    out.println("<tr>");
                    out.println("<td>" + record.getId() + "</td>");
                    out.println("<td>" + record.getPostDate() + "</td>");
                    out.println("<td>" + record.getPostMessage() + "</td>");
                    out.println("</tr>");
                    out.println();
                }
                out.println("</table>");
            }
            out.println("</body>");
            out.println("</html>");
            guestBook.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");
        doGet(request, response);
    }
}
