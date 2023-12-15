<%@ page import="java.util.Vector" %>
<%@ page import="models.Consommation" %>
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
					Heure
				</th>
				<th>
					Panneau
				</th>
				<th>
					Batterie
				</th>
			</tr>
			</thead>
			<tbody>
			<% double batterie = (double) request.getAttribute("batterie");
				Vector<Consommation> consommations = (Vector<Consommation>) request.getAttribute("consommations");
				for (int i = 0; i < consommations.size(); i++) { %>
			<tr>
				<td>
					<%= consommations.get(i).getHeure() %>
				</td>
				<td>
					<%= consommations.get(i).getPanneau() %>
				</td>
				<td>
					<%= batterie - consommations.get(i).getBatterie() %>
				</td>
			</tr>
			<% } %>
			</tbody>
		</table>
	</div>
</main>
</body>
</html>