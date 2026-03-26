package br.gov.jfrj.siga.tp.vraptor;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.validator.Validator;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.vraptor.VraptorController;
import br.gov.jfrj.siga.vraptor.SigaObjects;

public class TpController extends VraptorController {

    @Inject
    protected Validator validator;

    @Inject
    protected TpDao dao;

    @PostConstruct
    public void init() {
        this.result.include("currentTimeMillis", new Date().getTime());
    }

    protected void error(boolean errorCondition, String category, String message) {
        if (errorCondition)	 {
            validator.add(new I18nMessage(category, message));
        }
    }

}