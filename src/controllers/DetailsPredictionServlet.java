package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Consommation;
import models.Delestage;
import models.Secteur;
import utils.Connexion;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.util.Vector;

@WebServlet(name = "DetailsPrediction", value = "/DetailsPrediction")
public class DetailsPredictionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Date date = Date.valueOf(request.getParameter("date"));
        int secteur_id = Integer.parseInt(request.getParameter("secteur_id"));
        Secteur secteur = new Secteur();
        secteur.setId(secteur_id);
        try {
            Connection connection = new Connexion().getConnection();
            double batterie = secteur.getBatterie(connection);
            Vector<Consommation> consommations = secteur.getConsommation(connection, date);
            Vector<Consommation> newConsommations = new Vector<>();
            Delestage delestage = consommations.get(0).getSecteur().getCoupure(connection, date);
            double conso = 0;
            for (Consommation consommation : consommations) {
                conso += consommation.getBatterie();
                if (conso > batterie/2) {
                    consommation.setBatterie(batterie/2);
//                    newConsommations.add(consommation);
                }
            }
            request.setAttribute("consommations", consommations);
            request.setAttribute("batterie", batterie);
            request.getRequestDispatcher("detailsPrediction.jsp").forward(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

    }
}