/*-*****************************************************************************
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
 * Criado em  20/12/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package br.gov.jfrj.siga.dp;

import br.gov.jfrj.siga.base.util.Texto;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.model.Selecionavel;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "corporativo.dp_funcao_confianca")
@Cache(region = CpDao.CACHE_CORPORATIVO, usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class DpFuncaoConfianca extends AbstractDpFuncaoConfianca implements
        Serializable, Selecionavel, DpConvertableEntity {

    @Formula(value = "REMOVE_ACENTO(NOME_FUNCAO_CONFIANCA)")
    private String nmFuncaoConfiancaAI;

    public DpFuncaoConfianca() {
        super();
    }

    public String iniciais(String s) {
        final StringBuilder sb = new StringBuilder(10);
        boolean f = true;

        s = s.replace(" E ", " ");
        s = s.replace(" DA ", " ");
        s = s.replace(" DE ", " ");
        s = s.replace(" DO ", " ");

        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            if (f) {
                sb.append(c);
                f = false;
            }
            if (c == ' ') {
                f = true;
            }
        }
        return sb.toString();
    }

    public String getIniciais() {
        return iniciais(getNomeFuncao());
    }

    public String getDescricaoIniciaisMaiusculas() {
        return Texto.maiusculasEMinusculas(getDescricao());
    }

    public Long getId() {
        return getIdFuncao();
    }

    public String getSigla() {
        return getSiglaFuncaoConfianca();
    }

    public void setSigla(final String sigla) {
        setSiglaFuncaoConfianca(sigla);
    }

    public String getDescricao() {
        return getNomeFuncao();
    }

    public String getNmFuncaoConfiancaAI() {
        return nmFuncaoConfiancaAI;
    }

    public Date getDataFim() {
        return this.getHisDtFim();
    }

    public String getIdExterna() {
        return getIdeFuncao();
    }

    public void setDataFim(Date dataFim) {
        this.setHisDtFim(dataFim);
    }


    public void setIdExterna(String idExterna) {
        setIdeFuncao(idExterna);
    }

    public void setIdInicial(Long idInicial) {
        this.setHisIdIni(idInicial);
    }

    public boolean equivale(Object other) {
        if (other == null)
            return false;
        return this.getIdInicial().longValue() == ((DpFuncaoConfianca) other)
                .getIdInicial().longValue();
    }

    @Override
    public void setId(Long id) {
        setIdFuncao(id);
    }

}
