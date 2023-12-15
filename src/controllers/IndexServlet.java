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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Date date = Date.valueOf(request.getParameter("date"));
        try {
            Vector<Delestage> delestages = new Vector<>();
            Connection connection = new Connexion().getConnection();
            Vector<Secteur> secteurs = Secteur.all(connection);
            Vector<Secteur> sansDelestage = new Vector<>();
            for (Secteur secteur : secteurs) {
                Delestage delestage = secteur.predir(connection, date);
                if (delestage != null) {
                    delestages.add(delestage);
                } else {
                    sansDelestage.add(secteur);
                }
            }
            for (Secteur secteur : sansDelestage) {
                secteurs.remove(secteur);
            }
            connection.close();
            request.setAttribute("secteurs", secteurs);
            request.setAttribute("delestages", delestages);
            request.getRequestDispatcher("prediction.jsp").forward(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}