<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="models.Secteur" %>
<%@ page import="java.util.Vector" %>
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
		<div class="navbar-menu">
			<div class="navbar-end">
				<div class="navbar-item">
					<form action="" method="post">
						<div class="field has-addons">
							<label class="control" for="date">
								<input class="input" name="date" type="date" id="date" placeholder="Date">
							</label>
							<div class="control">
								<button type="submit" class="button is-info">
									Prédir
								</button>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</nav>
</header>
<main class="section">
	<div class="tile is-ancestor columns is-multiline">
		<%
			Vector<Secteur> secteurs = (Vector<Secteur>) request.getAttribute("secteurs");
			for (Secteur secteur : secteurs) { %>
			<div class="tile is-parent is-3">
				<div class="tile is-child box">
					<div class="content">
						<p class="has-text-centered has-text-weight-bold subtitle">
							<%= secteur.getNom() %>
						</p>
					</div>
				</div>
			</div>
		<% } %>
	</div>
</main>
</body>
</html>