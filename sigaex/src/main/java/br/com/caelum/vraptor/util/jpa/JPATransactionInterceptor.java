/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.util.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.Session;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.gov.jfrj.siga.vraptor.AplicacaoExceptionInterceptor;

/**
 * An interceptor that manages Entity Manager Transaction. All requests are
 * intercepted and a transaction is created before execution. If the request has
 * no erros, the transaction will commited, or a rollback occurs otherwise.
 * 
 * @author Lucas Cavalcanti
 */
@Component
@Intercepts(after = AplicacaoExceptionInterceptor.class)
public class JPATransactionInterceptor implements Interceptor {

	private final EntityManager manager;
	private final Validator validator;

	public JPATransactionInterceptor(EntityManager manager, Validator validator) {
		this.manager = manager;
		this.validator = validator;
	}

	public void intercept(InterceptorStack stack, ResourceMethod method, Object instance) {
		EntityTransaction transaction = null;
		try {
			transaction = manager.getTransaction();
			transaction.begin();

			stack.next(method, instance);

			transaction = manager.getTransaction();

			if (!validator.hasErrors() && transaction.isActive()) {
				// Executando flush no EM e a Session para garantir n??o nada
				// ficar?? pendente.
				manager.flush();
				Session s = (Session) (manager.getDelegate());
				s.flush();
				transaction.commit();
			}
		} finally {
			transaction = manager.getTransaction();
			if (transaction != null && transaction.isActive()) {
				manager.clear();
				// Executando um clear na session do Hibernate para garantir que
				// comandos enviados diretamente para a session, n??o via JPA,
				// n??o tenham ficado em um cache e sejam executados depois do
				// rollback
				Session s = (Session) (manager.getDelegate());
				s.clear();
				try {
					s.flush();
				} catch (Exception e) {
					// Do nothing, just making sure everything is flushed
				}
				transaction.rollback();
			}
		}
	}

	public boolean accepts(ResourceMethod method) {
		return true; // Will intercept all requests
	}
}
