<%--suppress CheckTagEmptyBody XmlPathReference HtmlUnknownTarget --%>

<%--@elvariable id="itens" type="java.util.List<br.gov.jfrj.siga.cp.CpConfiguracao>"--%>
<%--@elvariable id="servPai" type="java.lang.String"--%>
<%--@elvariable id="recbObrigatorio" type="java.lang.Integer"--%>
<%--@elvariable id="pode" type="java.lang.Integer"--%>
<%--@elvariable id="naoPode" type="java.lang.Integer"--%>

<%@ page pageEncoding="UTF-8" session="false" buffer="64kb" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://localhost/jeetags" prefix="siga" %>
<%@ taglib uri="http://localhost/libstag" prefix="f" %>
<%@ taglib tagdir="/WEB-INF/tags/mensagem" prefix="siga-mensagem" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<siga:pagina popup="false" titulo="Receber notificação por email">
    <script type="text/javascript" src="/siga/javascript/jquery.blockUI.js"></script>

    <main class="container-fluid">
        <div class="card bg-light mb-3">
            <div class="card-header">
                <h5 id="et">Receber notificação por e-mail</h5>
            </div>

            <form class="card-body p-0" name="frm" id="listar" action="listar" method="GET">
                <table style="border: none;" class="form-group table table-sm table-striped">
                    <thead class="thead-dark">
                    <tr>
                        <th class="pl-4" style="width: 70%;">Ações</th>
                        <th class="text-center">Receber</th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:forEach items="${itens}" var="conf">
                        <c:if test="${conf.cpServico.siglaServico != servPai}">
                            <c:url var="ativar" value="editar">
                                <c:param name="siglaServ" value="${conf.cpServico.siglaServico}"></c:param>
                                <c:param name="idSituacao" value="1"></c:param>
                            </c:url>

                            <c:url var="inativar" value="editar">
                                <c:param name="siglaServ" value="${conf.cpServico.siglaServico}"></c:param>
                                <c:param name="idSituacao" value="2"></c:param>
                            </c:url>

                            <tr>
                                <td class="align-middle pl-4">
                                        ${conf.cpServico.dscServico}
                                    <a
                                            class="fas fa-info-circle text-secondary ml-1"
                                            data-toggle="tooltip" data-trigger="click"
                                            data-placement="bottom"
                                            title="${conf.cpServico.labelServico} ">
                                    </a>
                                </td>

                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${conf.cpSituacaoConfiguracao.id == pode}">
                                            <button class="btn btn-primary btnAcao" style="min-width: 100px;"
                                                    type="button"
                                                    onclick="submitPost('${inativar}')">Inativar
                                            </button>
                                        </c:when>
                                        <c:when test="${conf.cpSituacaoConfiguracao.id == naoPode}">
                                            <button class="btn btn-danger btnAcao" style="min-width: 100px;"
                                                    type="button"
                                                    onclick="submitPost('${ativar}')">Ativar
                                            </button>
                                        </c:when>
                                        <c:when test="${conf.cpSituacaoConfiguracao.id == recbObrigatorio}">
                                            <p style="font-size: 12px; color: grey;">Recebimento obrigatório</p>
                                        </c:when>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                    </tbody>
                </table>
            </form>
        </div>
        <script>
            function submitPost(url) {
                const btn = document.querySelector(".btnAcao")
                btn.disabled = true;
                btn.innerHtml = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>Aguarde'

                $(function () {
                    $('[data-toggle="tooltip"]').tooltip()
                })

                setTimeout(function () {
                    const frm = document.getElementById('listar');
                    frm.method = "POST";
                    frm.action = url;
                    frm.submit();
                }, 1000)
            }
        </script>
    </main>
</siga:pagina>
