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
package br.gov.jfrj.siga.ex;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import br.gov.jfrj.siga.dp.dao.CpDao;

@Entity
@BatchSize(size = 500)
@Immutable
@Cacheable
@Cache(region = CpDao.CACHE_QUERY_HOURS, usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "EX_SITUACAO_CONFIGURACAO", catalog = "SIGA")
public class ExSituacaoConfiguracao extends AbstractExSituacaoConfiguracao {

	/**
	 * 
	 */
	public static final long serialVersionUID = 3624557793773660738L;

	public static final long SITUACAO_PODE = 1;

	public static final long SITUACAO_NAO_PODE = 2;

	public static final long SITUACAO_OBRIGATORIO = 3;

	public static final long SITUACAO_OPCIONAL = 4;

	public static final long SITUACAO_DEFAULT = 5;

	public static final long SITUACAO_NAO_DEFAULT = 6;

	public static final long SITUACAO_PROIBIDO = 7;

	public ExSituacaoConfiguracao() {
	}
}
