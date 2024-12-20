<%@ tag body-content="scriptless" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://localhost/jeetags" prefix="siga" %>
<%@ taglib uri="http://localhost/libstag" prefix="f" %>
<%@ attribute name="popup" %>
<%@ attribute name="pagina_de_erro" %>
<%@ attribute name="incluirJs" %>
<%@ attribute name="incluirBS" required="false" %>

<!--[if gte IE 5.5]><script src="/siga/javascript/jquery.ienav.js" type="text/javascript"></script><![endif]-->

<c:choose>
    <c:when test="${not empty sigaModalAlerta}">
        <siga:siga-modal id="sigaModalAlerta" centralizar="${sigaModalAlerta.centralizar}" abrirAoCarregarPagina="true"
                         exibirRodape="true"
                         tituloADireita="${empty sigaModalAlerta.titulo ? 'Alerta' : sigaModalAlerta.titulo}">
            <div class="modal-body">${sigaModalAlerta.mensagem}</div>
        </siga:siga-modal>
    </c:when>
    <c:otherwise>
        <siga:siga-modal id="sigaModalAlerta" exibirRodape="true" tituloADireita="Alerta">
            <div class="modal-body">Mensagem de alerta</div>
        </siga:siga-modal>
    </c:otherwise>
</c:choose>
<siga:siga-spinner/>

<script src="/siga/javascript/jquery/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>

<script src="/siga/javascript/siga.js?v=1622040065" type="text/javascript" charset="utf-8"></script>

<script src="/siga/javascript/picketlink.js" type="text/javascript" charset="utf-8"></script>

<script src="/siga/javascript/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min.js"
        type="text/javascript"></script>
<link rel="stylesheet" href="/siga/javascript/jquery-ui-1.10.3.custom/css/ui-lightness/jquery-ui-1.10.3.custom.min.css"
      type="text/css" media="screen, projection">
<script src="/siga/popper-1-14-3/popper.min.js"></script>

<c:if test="${empty incluirBS or incluirBS}">
    <script src="/siga/bootstrap/js/bootstrap.min.js?v=4.1.1" type="text/javascript"></script>
</c:if>

<script src="/siga/javascript/datepicker-pt-BR.js" type="text/javascript"></script>

<c:if test="${not empty incluirJs}">
    <script src="${incluirJs}" type="text/javascript"></script>
</c:if>


<script type="text/javascript">
    $(document).ready(function () {
        $('.links li code').hide();
        $('.links li p').click(function () {
            $(this).next().slideToggle('fast');
        });
        $('.once').click(function (e) {
            if (this.beenSubmitted)
                e.preventDefault();
            else
                this.beenSubmitted = true;
        });
        $('.campoData').datepicker({
            onSelect: function () {
                ${onSelect}
            }
        });

        //$('.autogrow').css('overflow', 'hidden').autogrow();
    });
</script>

<script>
    $('.dropdown-menu a.dropdown-toggle').on(
        'click',
        function (e) {
            if (!$(this).next().hasClass('show')) {
                $(this).parents('.dropdown-menu').first().find('.show')
                    .removeClass("show");
            }
            var $subMenu = $(this).next(".dropdown-menu");
            $subMenu.toggleClass('show');

            $(this).parents('li.nav-item.dropdown.show').on(
                'hidden.bs.dropdown', function (e) {
                    $('.dropdown-submenu .show').removeClass("show");
                });

            return false;
        });

    $('[data-html="true"]').tooltip();
</script>

</body>
</html>
