package br.gov.jfrj.siga.vraptor;

/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.isEmpty;
import static java.util.Arrays.asList;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Lazy;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.FlashScope;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Interceptor that loads given entity from the database.
 *
 * @author Lucas Cavalcanti
 * @author Cecilia Fernandes
 * @author Ot??vio Scherer Garcia
 * @since 3.3.2
 *
 */
@Intercepts(before = ParametersInstantiatorInterceptor.class)
@Lazy
public class ParameterOptionalLoaderInterceptor implements Interceptor {

	private final EntityManager em;
	private final HttpServletRequest request;
	private final ParameterNameProvider provider;
	private final Result result;
	private final Converters converters;
	private final Localization localization;
	private final FlashScope flash;

	public ParameterOptionalLoaderInterceptor(EntityManager em,
			HttpServletRequest request, ParameterNameProvider provider,
			Result result, Converters converters, Localization localization,
			FlashScope flash) {
		this.em = em;
		this.request = request;
		this.provider = provider;
		this.result = result;
		this.converters = converters;
		this.localization = localization;
		this.flash = flash;
	}

	public boolean accepts(ResourceMethod method) {
		return any(asList(method.getMethod().getParameterAnnotations()),
				hasAnnotation(LoadOptional.class));
	}

	public void intercept(InterceptorStack stack, ResourceMethod method,
			Object resourceInstance) throws InterceptionException {
		Annotation[][] annotations = method.getMethod()
				.getParameterAnnotations();

		String[] names = provider.parameterNamesFor(method.getMethod());
		Class<?>[] types = method.getMethod().getParameterTypes();

		Object[] args = flash.consumeParameters(method);

		for (int i = 0; i < names.length; i++) {
			if (hasLoadAnnotation(annotations[i])) {
				Object loaded = load(names[i], types[i]);

//				if (loaded == null) {
//					result.notFound();
//					return;
//				}

				if (args != null) {
					args[i] = loaded;
				} else {
					request.setAttribute(names[i], loaded);
				}
			}
		}
		flash.includeParameters(method, args);

		stack.next(method, resourceInstance);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> Object load(String name, Class type) {
		final SingularAttribute<?, ?> idProperty = getIdProperty(type);

		final String parameter = request.getParameter(name + "."
				+ idProperty.getName());
		if (parameter == null) {
			return null;
		}

		Converter<?> converter = converters.to(idProperty.getType()
				.getJavaType());
		checkArgument(converter != null,
				"Entity %s id type %s must have a converter",
				type.getSimpleName(), idProperty.getType());

		Serializable id = (Serializable) converter.convert(parameter, type,
				localization.getBundle());
		return em.find(type, id);
	}

	private boolean hasLoadAnnotation(Annotation[] annotation) {
		return !isEmpty(Iterables.filter(asList(annotation), LoadOptional.class));
	}

	public static Predicate<Annotation[]> hasAnnotation(
			final Class<?> annotation) {
		return new Predicate<Annotation[]>() {
			public boolean apply(Annotation[] param) {
				return any(asList(param), instanceOf(annotation));
			}
		};
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> SingularAttribute<?, ?> getIdProperty(final Class type) {
		IdentifiableType entity = em.getMetamodel().entity(type);

		Type<?> idType = entity.getIdType();
		checkArgument(idType != null,
				"Entity %s must have an id property for @Load.",
				type.getSimpleName());

		if (hasSupertype(entity)) {
			entity = entity.getSupertype();
		}

		return entity.getDeclaredId(idType.getJavaType());
	}

	private <T> boolean hasSupertype(IdentifiableType<? super T> entity) {
		return entity.getSupertype() != null;
	}
}