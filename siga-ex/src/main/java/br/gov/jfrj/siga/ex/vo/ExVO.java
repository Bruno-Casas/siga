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
package br.gov.jfrj.siga.ex.vo;

import br.gov.jfrj.siga.base.VO;
import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.ex.bl.ExBL;
import br.gov.jfrj.siga.ex.bl.ExCompetenciaBL;
import br.gov.jfrj.siga.ex.bl.ExDocumentoBL;
import br.gov.jfrj.siga.ex.bl.ExMobilBL;
import br.gov.jfrj.siga.hibernate.ExDao;

import javax.enterprise.inject.spi.CDI;

public class ExVO extends VO {

    protected ExDao dao;
    protected ExCompetenciaBL comp;
    protected ExBL bl;
    protected ExDocumentoBL docBl;
    protected ExMobilBL mobBl;

    public ExVO() {
        dao = CDI.current().select(ExDao.class).get();
        comp = CDI.current().select(ExCompetenciaBL.class).get();
        bl = CDI.current().select(ExBL.class).get();
        docBl = CDI.current().select(ExDocumentoBL.class).get();
        mobBl = CDI.current().select(ExMobilBL.class).get();
    }

}
