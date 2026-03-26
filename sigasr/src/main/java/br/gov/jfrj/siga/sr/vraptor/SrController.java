package br.gov.jfrj.siga.sr.vraptor;

import static br.com.caelum.vraptor.view.Results.http;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.sr.model.SrDao;
import org.apache.http.HttpStatus;

import br.com.caelum.vraptor.view.HttpResult;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.sr.validator.SrError;
import br.gov.jfrj.siga.sr.validator.SrValidator;
import br.gov.jfrj.siga.vraptor.VraptorController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;


public class SrController extends VraptorController {

	@Inject
	protected SrValidator srValidator;

	@Inject
	protected SrDao dao;


	@PostConstruct
	public void init() throws Throwable {
		result.on(AplicacaoException.class).forwardTo(this).appexception();
		result.on(Exception.class).forwardTo(this).exception();
	}

	public void enviarErroValidacao() {
		HttpResult res = this.result.use(http());
		res.setStatusCode(HttpStatus.SC_BAD_REQUEST);
		res.addHeader("Validation", "true");

		result.use(Results.http()).body(jsonErrors().toString());
	}

	private JsonArray jsonErrors() {
		JsonArray jsonArray = new JsonArray();

		for (SrError error : srValidator.getErros()) {
			jsonArray.add(new Gson().toJsonTree(error));
		}

		return jsonArray;
	}

}
