<%@ page import="models.Delestage" %>
<%@ page import="models.Secteur" %>
<%@ page import="java.util.Vector" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="fr">
<head>
	<meta charset="UTF-8">
	<meta name="viewport"
		  content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
	<meta http-equiv="X-UA-Compatible" content="ie=edge">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bulma@0.9.4/css/bulma.min.css">
	<title>Panneau</title>
</head>
<body>
<header class="container">
	<nav class="navbar">
		<div class="navbar-brand">
			<div class="navbar-item">
				<h3 class="title">
					Panneau
				</h3>
			</div>
		</div>
	</nav>
</header>
<main class="section">
	<div class="table-container container">
		<table class="table is-bordered is-fullwidth">
			<thead>
			<tr>
				<th>
					Secteur
				</th>
				<th>
					Coupure
				</th>
				<th>

				</th>
			</tr>
			</thead>
			<tbody>
			<% Vector<Secteur> secteurs = (Vector<Secteur>) request.getAttribute("secteurs");
				Vector<Delestage> delestages = (Vector<Delestage>) request.getAttribute("delestages");
				for (int i = 0; i < secteurs.size(); i++) { %>
			<tr>
				<td>
					<%= secteurs.get(i).getNom() %>
				</td>
				<td>
					<%= delestages.get(i).getDebut() %>
				</td>
				<td>
					<a href="DetailsPrediction?secteur_id=<%= secteurs.get(i).getId() %>&date=<%= delestages.get(i).getDate() %>">
						Voir les détails
					</a>
				</td>
			</tr>
			<% } %>
			</tbody>
		</table>
	</div>
</main>
</body>
</html>