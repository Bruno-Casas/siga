/*******************************************************************************
 * Copyright (c) 2006 - 2011 SJRJ.
 * 
 *     This file is part of SIGA.
 * 
 *     SIGA is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     SIGA is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with SIGA.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*
 * Criado em  23/11/2005
 *
 */
package br.gov.jfrj.siga.vraptor;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.cp.CpModelo;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.model.dao.ModeloDao;
import br.gov.jfrj.siga.util.FreemarkerIndent;

@Controller
public class ModeloController extends VraptorController {

	private CpModelo daoMod(long id) {
		return cpDao.consultar(id, CpModelo.class, false);
	}

	private CpModelo daoModAtual(long id) {
		return cpDao.consultarPorIdInicialCpModelo(daoMod(id).getIdInicial());
	}

	@Get("/app/modelo/listar")
	public void lista() throws Exception {
		assertAcesso("FE:Ferramentas;MODVER:Visualizar modelos");
		result.include("itens", cpDao.consultaCpModelos());
	}

	@Transacional
	@Post("/app/modelo/gravar")
	public void gravar(Integer id, String conteudo) throws Exception {
		assertAcesso("FE:Ferramentas;MODEDITAR:Editar modelos");

		if (id != null) {
			CpModelo mod = daoModAtual(id);
			this.cpBl
					.alterarCpModelo(mod, conteudo, getIdentidadeCadastrante());
		} else {
			try {
				CpModelo mod = new CpModelo();
				mod.setConteudoBlobString(conteudo);
				if (paramLong("idOrgUsu") != null)
					mod.setCpOrgaoUsuario(cpDao.consultar(
							paramLong("idOrgUsu"), CpOrgaoUsuario.class, false));
				mod.setHisDtIni(cpDao.consultarDataEHoraDoServidor());
				cpDao.gravarComHistorico(mod, getIdentidadeCadastrante());
			} catch (Exception e) {
				throw new AplicacaoException(
						"Não foi possível gravar o modelo.", 9, e);
			}
		}

		result.redirectTo(this).lista();
	}

	@Post("/public/app/modelo/indentar")
	public void indentar(String conteudo) throws Exception {
		String r = FreemarkerIndent.indent(conteudo);
		result.use(Results.http()).body(r);
	}
}
