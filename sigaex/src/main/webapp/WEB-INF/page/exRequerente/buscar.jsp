<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="64kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<script>
	function sbmt(page) {
		const search = new URLSearchParams(location.search);
		if(!search.get('page'))
			search.append('page', page)
		else
			search.set('page', page);
		location.search = us.toString();
	}
	const requerentes = {};
</script>

<siga:pagina titulo="Buscar Pessoa" popup="true">
	<!-- main content -->
	<main class="card-body" style="max-height: 100%">
		<div class="row">
			<span>Nome ou CPF/CNPJ</span>
			<div class="input-group mb-2">
				<input style="cursor: pointer" type="text" class="form-control" onkeypress="buscar(event)" placeholder="Busca" id="ref">
				<div class="input-group-append">
					<span class="input-group-text" onclick="buscar();">Buscar</span>
				</div>
			</div>
		</div>
		<div class="row">
			<table class="table">
				<thead>
					<tr>
						<th scope="col">#</th>
						<th scope="col">Nome</th>
						<th scope="col">CPF/CNPJ</th>
						<th scope="col">E-Mail</th>
						<th scope="col">Telefone</th>
					</tr>
				</thead>
				<siga:paginador maxItens="10" maxIndices="10"
					totalItens="${numResultados}" itens="${requerentes}"
					var="requerente">
					<c:set var="tipoReq" scope="session"
						value="${requerente.tipoRequerente}" />

					<script class="temp">
						requerentes["${requerente.id}"] = {
							nomeRequerente : "${requerente.nomeRequerente}",
							email : "${requerente.emailRequerente}",
							tipo : "${requerente.tipoRequerente}",
							endereco : "${requerente.endereco}",
							numEndereco : "${requerente.numeroEndereco}",
							bairro : "${requerente.bairro}",
							cidade : "${requerente.cidade}",
							telefone : "${requerente.telefoneRequerente}",
							cadastro : "${requerente.cpfRequerente}"
						}
						<c:if test="${tipoReq == '1'}">
						let aux = {
							registro : "${requerente.rgRequerente}",
							nascimento : "${requerente.dtNascimentoFormatado}",
							nacionalidade : "${requerente.nacionalidadeRequerente}",
							estadoCivil : "${requerente.estadoCivil}",
							conjugue : "${requerente.nomeConjugue}"
						}
						requerentes["${requerente.id}"] = Object
							.assign(requerentes["${requerente.id}"], aux);
						</c:if>
					</script>
					<tr>

						<th scope="row">1</th>
						<td><a
							href="javascript: parent.setEntrevista(requerentes['${requerente.id}']);">${requerente.nomeRequerente}</a>
						</td>
						<td>${requerente.cpfFormatado}</td>
						<td>${requerente.emailRequerente}</td>
						<td>${requerente.telefoneRequerente}</td>
					</tr>
				</siga:paginador>
			</table>
		</div>
		<script>
			let temps = document.getElementsByClassName('temp');
			for (let i = 0; i < temps.length; i++) {
				els.item(i).remove();
			}

			function buscar(event) {
				
				if(event && event.keyCode != 13)
					return null;
					
				let value = document.getElementById('ref').value;
				location.replace('/sigaex/app/requerente/buscar?ref=' + value);
			}
		</script>
	</main>
</siga:pagina>