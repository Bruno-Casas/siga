package br.gov.jfrj.siga.ex.xjus.mov;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.hibernate.Query;

import br.gov.jfrj.siga.ex.xjus.Utils;
import br.gov.jfrj.siga.hibernate.ExDao;
import br.jus.trf2.xjus.record.api.IXjusRecordAPI;
import br.jus.trf2.xjus.record.api.IXjusRecordAPI.ChangedReferencesGetRequest;
import br.jus.trf2.xjus.record.api.IXjusRecordAPI.ChangedReferencesGetResponse;
import br.jus.trf2.xjus.record.api.IXjusRecordAPI.Reference;

import com.crivano.swaggerservlet.SwaggerUtils;

public class ChangedReferencesGet implements
		IXjusRecordAPI.IChangedReferencesGet {

	private static final String HQL = "select mov.idMov, mov.dtIniMov from ExMovimentacao mov where mov.exTipoMovimentacao.idTpMov in (2, 5, 6, 7, 8, 18) and mov.exMobil.exDocumento.dtFinalizacao != null and (((mov.exMobil.exDocumento.dtAltDoc > :dt or mov.dtIniMov > :dt) and mov.idMov > :id) or (mov.exMobil.exDocumento.dtAltDoc > :dt or mov.dtIniMov > :dt)) order by mov.dtIniMov";

	@Override
	public void run(ChangedReferencesGetRequest req,
			ChangedReferencesGetResponse resp) throws Exception {
		resp.list = new ArrayList<>();
		if (req.lastdate == null)
			req.lastdate = new Date(0L);
		if (req.lastid == null)
			req.lastid = Utils.formatId(0L);
		try {
			ExDao dao = ExDao.getInstance();
			Query q = dao.getSessao().createQuery(HQL);
			q.setMaxResults(Integer.valueOf(req.max));
			Date first = req.lastdate;
			Long lastid = Long.valueOf(req.lastid);
			// System.out.println("req.last: " + SwaggerUtils.format(first));
			q.setTimestamp("dt", first);
			q.setLong("id", lastid);
			// System.out.println(q.getQueryString());
			@SuppressWarnings("rawtypes")
			Iterator i = q.iterate();
			while (i.hasNext()) {
				Reference ref = new Reference();
				Object rs = i.next();
				Long id = (Long) ((Object[]) rs)[0];
				Date dt = (Date) ((Object[]) rs)[1];
				// System.out.println(SwaggerUtils.format(dt));
				ref.id = Utils.formatId(id);
				ref.date = dt;
				resp.list.add(ref);
			}
		} finally {
			ExDao.freeInstance();
		}

	}

	public String getContext() {
		return "obter a lista de refer??ncias";
	}
}
