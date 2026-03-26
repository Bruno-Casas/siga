package br.gov.jfrj.siga.api.v1;

import java.util.ArrayList;
import java.util.List;

import br.gov.jfrj.siga.api.v1.ISigaApiV1.IPinResetPost;
import br.gov.jfrj.siga.base.RegraNegocioException;
import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.cp.CpToken;
import br.gov.jfrj.siga.cp.bl.CpBL;
import br.gov.jfrj.siga.cp.bl.CpCompetenciaBL;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.vraptor.Transacional;

import javax.inject.Inject;

@Transacional
public class PinResetPost implements IPinResetPost {

	@Inject
	private CpBL bl;

	@Inject
	private CpDao dao;

	@Inject
	private CpCompetenciaBL comp;

	@Override
	public void run(Request req, Response resp, SigaApiV1Context ctx) throws Exception {
		final String tokenPin = req.tokenPin.toUpperCase();
		final String pin = req.pin;

		CpIdentidade identidadeCadastrante = ctx.getIdentidadeCadastrante();

		DpPessoa cadastrante = ctx.getCadastrante();

		if (!comp.podeSegundoFatorPin(cadastrante, cadastrante.getLotacao()))
			throw new RegraNegocioException(
					"PIN como Segundo Fator de Autenticação: Acesso não permitido a esse recurso.");

		if (bl.isTokenValido(CpToken.TOKEN_PIN, cadastrante.getCpfPessoa(), tokenPin)) {
			if (bl.consisteFormatoPin(pin)) {

				List<CpIdentidade> listaIdentidades = new ArrayList<CpIdentidade>();
				listaIdentidades = dao.consultaIdentidadesPorCpf(cadastrante.getCpfPessoa().toString());

				bl.definirPinIdentidade(listaIdentidades, pin, identidadeCadastrante);
				bl.invalidarTokenUtilizado(CpToken.TOKEN_PIN, cadastrante.getCpfPessoa(), tokenPin);
				bl.enviarEmailDefinicaoPIN(cadastrante, "Redefinição de PIN",
						"Você redefiniu seu PIN.");
				resp.mensagem = "PIN foi redefinido.";

			}
		} else {
			throw new RegraNegocioException("Token para reset de PIN inválido ou expirado.");
		}
	}

	@Override
	public String getContext() {
		return "reset PIN";
	}

}
