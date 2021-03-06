<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="128kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://localhost/customtag" prefix="tags"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>

<script type="text/javascript" language="Javascript1.1">

function sbmt() {
	frmRelExpedientes.action='${url}';
	frmRelExpedientes.submit();	
}
</script>
<c:set var="titulo_pagina" scope="request">
	Despachos e TransferĂȘncias
</c:set>
<input type="hidden" name="secaoUsuario" id="secaoUsuario" value="${lotaTitular.orgaoUsuario.descricaoMaiusculas}" />
<div class="row">
	<div class="col-sm-4">
		<label>&Oacute;rg&atilde;o</label>
		<select name="orgaoUsu" id="orgaoUsu" class="form-control">
			<option value="0">
			</option>
			<c:forEach var="item" items="${orgaosUsu}">
				<option value="${item.idOrgaoUsu}">
					${item.nmOrgaoUsu}
				</option>
			</c:forEach>			
		</select>
	</div>
	<div class="col-sm-6">
		<label><fmt:message key="usuario.lotacao"/></label>
		<siga:selecao propriedade="lotacaoDestinatario" tema="simple" modulo="siga"/>
	</div>
</div>
<input type="hidden" name="lotacao" id="lotacao" value="${lotacaoDestinatarioSel.id}" />
<input type="hidden" name="siglalotacao" id="siglalotacao" value="${lotacaoDestinatarioSel.sigla}" />
<div class="row">
	<div class="col-sm-2">
		<label>Data Inicial</label>
		<input type="text" name="dataInicial" id="dataInicial" onblur="javascript:verifica_data(this, true);comparaData(dataInicial,dataFinal);"
				theme="simple" maxlength="10" class="form-control" />
	</div>
	<div class="col-sm-2">
		<label>Data Final</label>
		<input type="text" name="dataFinal" id="dataFinal" onblur="javascript:verifica_data(this,true);comparaData(dataInicial,dataFinal);"
				theme="simple" maxlength="10" class="form-control" />
	</div>
</div>
<input type="hidden" name="lotacaoTitular" id="lotacaoTitular" value="${lotaTitular.siglaLotacao}" />
<input type="hidden" name="idTit" id="idTit" value="${titular.id}" />
