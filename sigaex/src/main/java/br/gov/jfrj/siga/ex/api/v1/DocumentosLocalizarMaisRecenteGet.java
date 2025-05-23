package br.gov.jfrj.siga.ex.api.v1;

import br.gov.jfrj.siga.dp.CpMarcador;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.ex.ExModelo;
import br.gov.jfrj.siga.ex.api.v1.IExApiV1.IDocumentosLocalizarMaisRecenteGet;
import br.gov.jfrj.siga.hibernate.ExDao;
import br.gov.jfrj.siga.persistencia.ExMobilDaoFiltro;
import com.crivano.swaggerservlet.SwaggerException;
import com.crivano.swaggerservlet.SwaggerServlet;

import java.util.List;

public class DocumentosLocalizarMaisRecenteGet implements IDocumentosLocalizarMaisRecenteGet {

    @Override
    public void run(Request req, Response resp, ExApiV1Context ctx) throws Exception {
        final ExMobilDaoFiltro builder = new ExMobilDaoFiltro();

        // Modelo
        ExModelo modelo = null;
        if (req.modelo != null) {
            modelo = ExDao.getInstance().consultarExModelo(null, req.modelo);
            if (modelo == null)
                throw new SwaggerException("Não existe modelo com o nome especificado (" + req.modelo + ")", 400, null,
                        req, resp, null);
        }
        builder.setIdMod(modelo.getHisIdIni());

        // Lota Subscritor
        final DpLotacao fltLotaSubscritor = new DpLotacao();
        fltLotaSubscritor.setSigla(req.lotaSubscritor.toUpperCase());
        DpLotacao lotaSubscritor = CpDao.getInstance().consultarPorSigla(fltLotaSubscritor);
        if (lotaSubscritor == null)
            throw new SwaggerException("Nenhuma lotação foi encontrada contendo a sigla informada.", 400, null, req,
                    resp, null);

        // Ano
        if (req.ano == null)
            throw new SwaggerException("Nenhum ano foi informado.", 400, null, req, resp, null);
        builder.setAnoEmissao(Long.parseLong(req.ano));

        // Marcador
        CpMarcador marcador = null;
        if (req.marcador != null) {
            List<CpMarcador> listMarcador = ExDao.getInstance().consultaCpMarcadorAtivoPorNome(req.marcador, null);
            if (listMarcador.size() > 0) {
                marcador = listMarcador.get(0);
            } else {
                throw new SwaggerException("Não existe marcador com o nome especificado (" + req.marcador + ")", 400,
                        null, req, resp, null);
            }
        }
        builder.setUltMovIdEstadoDoc(marcador.getHisIdIni());

        List<Object[]> l = ExDao.getInstance().consultarPorFiltro(builder, 0, 1);
        if (l.isEmpty())
            throw new SwaggerException("Nenhum documento foi encontrado com os argumentos informados.", 404, null, req,
                    resp, null);
        ExMobil mob = (ExMobil) l.get(0)[1];
        SwaggerServlet.getHttpServletResponse().sendRedirect("/sigaex/api/v1/documentos/" + mob.getCodigoCompacto());
    }

    @Override
    public String getContext() {
        return "localizar documento mais recente";
    }

}
