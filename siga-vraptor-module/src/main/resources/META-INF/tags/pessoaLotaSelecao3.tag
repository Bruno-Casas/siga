<%@ tag body-content="scriptless" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://localhost/jeetags" prefix="siga" %>
<%@ taglib uri="http://localhost/libstag" prefix="libs" %>

<%@ attribute name="propriedadePessoa" required="true" %>
<%@ attribute name="propriedadeLotacao" required="true" %>
<%@ attribute name="propriedadeEmail" required="false" %>
<%@ attribute name="disabled" required="false" %>
<%@ attribute name="labelPessoaLotacao" required="false" %>

<c:set var="propriedadePessoaClean"
       value="${libs:slugify(propriedadePessoa, false, true)}"/>
<c:set var="propriedadeLotacaoClean"
       value="${libs:slugify(propriedadeLotacao, false, true)}"/>
<c:set var="propriedadeEmailClean"
       value="${libs:slugify(propriedadeEmail, false, true)}"/>

<c:set var="desativar" value="nao"></c:set>
<c:if test="${disabled == 'sim'}">
    <c:set var="pessoaLotaSelecaoDisabled" value="disabled='disabled'"
           scope="request"/>
    <c:set var="desativar" value="sim"></c:set>
</c:if>

<div class="row">
    <div class="col-sm-3">
        <div class="form-group">
            <label>${labelPessoaLotacao}</label>
            <select id="${propriedadePessoaClean}${propriedadeLotacaoClean}"
                    onchange="" ${pessoaLotaSelecaoDisabled} class="form-control">
                <option value="1">Pessoa</option>
                <option value="2">Lotação</option>
                <c:if test="${not empty propriedadeEmail}">
                    <option value="3">E-mail</option>
                </c:if>
            </select>
        </div>
    </div>
    <div class="col-sm-9">
        <div id="spanPessoa${propriedadePessoaClean}" class="form-group">
            <label>Pessoa</label>
            <siga:selecao3
                    tipo="pessoa" propriedade="${propriedadePessoa}" tema="simple"
                    modulo="siga" desativar="${desativar}"/>
        </div>

        <!-- Lotacao -->
        <div id="spanLotacao${propriedadeLotacaoClean}" class="form-group" style="display: none">
            <label>Lotação</label>
            <siga:selecao3
                    tipo="lotacao" propriedade="${propriedadeLotacao}" tema="simple"
                    modulo="siga" desativar="${desativar}"/>
        </div>
    </div>
</div>

<c:if test="${not empty propriedadeEmail}">
    <c:set var="inputNameEmail" value="${propriedadeEmail}"/>
</c:if>
<span style="display: none;" id="spanEmail${propriedadeEmailClean}">
			<input type='text' name='${inputNameEmail}' size="70"
                   id="formulario_${propriedadeEmailClean}"/> Obs.: Ao informar
			v&aacute;rios, separar por espa&ccedil;o.
		</span>

<script language="javascript">
    var select = document
        .getElementById('${propriedadePessoaClean}${propriedadeLotacaoClean}');

    if (document.getElementById('formulario_${propriedadePessoaClean}_id').value)
        select.value = 1;
    else
        select.value = 2;
    // O onchange tem de ser definido da forma abaixo porque, quando esta tag está dentro de um código
    // carregado por ajax, não funciona o tratamento do modo tradicional (onchange="", etc)
    // http://stackoverflow.com/questions/8893786/uncaught-referenceerror-x-is-not-defined
    select.onchange = function () {
        var select = document
            .getElementById('${propriedadePessoaClean}${propriedadeLotacaoClean}');

        if (select.value == '1') {
            document.getElementById('spanLotacao${propriedadeLotacaoClean}').style.display = 'none';
            document.getElementById('spanPessoa${propriedadePessoaClean}').style.display = 'inline';
            document.getElementById('formulario_${propriedadeLotacaoClean}_id').value = '';
            document
                .getElementById('formulario_${propriedadeLotacaoClean}_sigla').value = '';
            document
                .getElementById('formulario_${propriedadeLotacaoClean}_descricao').value = '';

            document.getElementById('${propriedadeLotacaoClean}Span').innerHTML = '';

            document.getElementById('spanEmail${propriedadeEmailClean}').style.display = 'none';
            document.getElementById('formulario_${propriedadeEmailClean}').value = '';
        } else if (select.value == '2') {
            document.getElementById('spanPessoa${propriedadePessoaClean}').style.display = 'none';
            document.getElementById('spanLotacao${propriedadeLotacaoClean}').style.display = 'inline';
            document.getElementById('formulario_${propriedadePessoaClean}_id').value = '';
            document
                .getElementById('formulario_${propriedadePessoaClean}_sigla').value = '';
            document
                .getElementById('formulario_${propriedadePessoaClean}_descricao').value = '';
            document.getElementById('${propriedadePessoaClean}Span').innerHTML = '';

            document.getElementById('spanEmail${propriedadeEmailClean}').style.display = 'none';
            document.getElementById('formulario_${propriedadeEmailClean}').value = '';
        } else if (select.value == '3') {
            document.getElementById('spanLotacao${propriedadeLotacaoClean}').style.display = 'none';
            document.getElementById('formulario_${propriedadeLotacaoClean}_id').value = '';
            document
                .getElementById('formulario_${propriedadeLotacaoClean}_sigla').value = '';
            document
                .getElementById('formulario_${propriedadeLotacaoClean}_descricao').value = '';
            document.getElementById('${propriedadeLotacaoClean}Span').innerHTML = '';

            document.getElementById('spanPessoa${propriedadePessoaClean}').style.display = 'none';
            document.getElementById('formulario_${propriedadePessoaClean}_id').value = '';
            document
                .getElementById('formulario_${propriedadePessoaClean}_sigla').value = '';
            document
                .getElementById('formulario_${propriedadePessoaClean}_descricao').value = '';

            document.getElementById('${propriedadePessoaClean}Span').innerHTML = '';

            document.getElementById('spanEmail${propriedadeEmailClean}').style.display = 'inline';
        }
    }
    select.onchange();
</script>