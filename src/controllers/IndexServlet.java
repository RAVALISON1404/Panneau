package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Secteur;
import models.Delestage;
import utils.Connexion;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.util.Vector;

@WebServlet(name = "index", value = "")
public class IndexServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Vector<Secteur> secteurs = Secteur.all(null);
        request.setAttribute("secteurs", secteurs);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Date date = Date.valueOf(request.getParameter("date"));
        Vector<Delestage> delestages = new Vector<>();
        try {
            Connection connection = new Connexion().getConnection();
            Vector<Secteur> secteurs = Secteur.all(connection);
            for (Secteur secteur : secteurs) {
                Delestage delestage = secteur.predir(connection, date);
                if (delestage != null) {
                    delestages.add(delestage);
                }
            }
            connection.close();
            request.setAttribute("secteurs", secteurs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        request.setAttribute("delestages", delestages);
        request.getRequestDispatcher("prediction.jsp").forward(request, response);
    }
}