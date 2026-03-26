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
package br.gov.jfrj.siga.relatorio;

import br.gov.jfrj.siga.cp.CpConfiguracao;
import br.gov.jfrj.siga.cp.CpServico;
import br.gov.jfrj.siga.cp.model.enm.CpSituacaoDeConfiguracaoEnum;
import br.gov.jfrj.siga.dp.DpPessoa;

import java.util.Date;
import java.util.Objects;

public class AlteracaoDireitosItem implements Comparable<AlteracaoDireitosItem> {
    DpPessoa pessoa;
    CpServico servico;
    CpConfiguracao cfgAntes;
    CpConfiguracao cfgDepois;

    public DpPessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(DpPessoa pessoa) {
        this.pessoa = pessoa;
    }

    public CpServico getServico() {
        return servico;
    }

    public void setServico(CpServico servico) {
        this.servico = servico;
    }

    public CpConfiguracao getCfgAntes() {
        return cfgAntes;
    }

    public void setCfgAntes(CpConfiguracao cfgAntes) {
        this.cfgAntes = cfgAntes;
    }

    public CpConfiguracao getCfgDepois() {
        return cfgDepois;
    }

    public void setCfgDepois(CpConfiguracao cfgDepois) {
        this.cfgDepois = cfgDepois;
    }

    public Date getInicio() {
        try {
            return getCfgAntes().getHisDtIni();
        } catch (Exception e) {
            return null;
        }
    }

    public Date getFim() {
        try {
            return getCfgDepois().getHisDtFim();
        } catch (Exception e) {
            return null;
        }
    }

    public CpSituacaoDeConfiguracaoEnum getSituacaoAntes() {
        if (cfgAntes != null)
            return cfgAntes.getCpSituacaoConfiguracao();
        if (servico != null && servico.getCpTipoServico() != null)
            return servico.getCpTipoServico().getSituacaoDefault();
        return null;
    }

    public CpSituacaoDeConfiguracaoEnum getSituacaoDepois() {
        if (cfgDepois != null)
            return cfgDepois.getCpSituacaoConfiguracao();
        if (servico != null && servico.getCpTipoServico() != null)
            return servico.getCpTipoServico().getSituacaoDefault();
        return null;
    }

    public String getIdExterna() {
        return (servico != null ? servico.getId() : "") + ": " + (pessoa != null ? pessoa.getIdInicial() : "");
    }

    @Override
    public int compareTo(AlteracaoDireitosItem o) {
        return getIdExterna().compareTo(o.getIdExterna());
    }

    public String printOrigemCurta() {
        try {
            if (getCfgDepois() == null) return "DEFAULT";
            return getCfgDepois().printOrigemCurta();
        } catch (Exception e) {
            return new String();
        }
    }

    public DpPessoa getCadastrante() {
        try {
            return getCfgDepois().getHisIdcIni().getDpPessoa();
        } catch (Exception e) {
            return null;
        }
    }
}
