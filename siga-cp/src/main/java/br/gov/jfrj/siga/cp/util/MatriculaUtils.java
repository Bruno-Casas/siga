package br.gov.jfrj.siga.cp.util;

import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.dao.CpDao;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class MatriculaUtils {

    @Inject
    private Provider<CpDao> daoProvider;

    private class Separado {
        String sigla;
        String complemento;
    }

    private Separado separar(String junto) {
        Separado separado = new Separado();

        Map<String, CpOrgaoUsuario> mapAcronimo = new TreeMap<String, CpOrgaoUsuario>();
        for (CpOrgaoUsuario ou : daoProvider.get().listarOrgaosUsuarios()) {
            mapAcronimo.put(ou.getAcronimoOrgaoUsu(), ou);
            mapAcronimo.put(ou.getSiglaOrgaoUsu(), ou);
        }

        SortedSet<String> set = new TreeSet<>(new Comparator<String>() {
            @Override
            public int compare(String arg0, String arg1) {
                int i = Integer.compare(arg1.length(), arg0.length());
                if (i != 0)
                    return i;
                return arg0.compareTo(arg1);
            }
        });
        set.addAll(mapAcronimo.keySet());
        StringBuilder acronimos = new StringBuilder();
        for (String s : set) {
            if (acronimos.length() > 0)
                acronimos.append("|");
            acronimos.append(s);
        }

        final Pattern p1 = Pattern.compile("^(?<orgao>" + acronimos + "){0,1}-?(?<complemento>[0-9A-Za-z\\-/ºª_ ]{1,20})$");
        final Matcher m1 = p1.matcher(junto);

        if (m1.find()) {
            String orgao = m1.group("orgao");
            String complemento = m1.group("complemento");

            if (StringUtils.isNotEmpty(orgao)) {
                try {
                    if (mapAcronimo.containsKey(orgao)) {
                        separado.sigla = orgao;
                    } else {
                        CpOrgaoUsuario orgaoUsuario = new CpOrgaoUsuario();
                        orgaoUsuario.setSiglaOrgaoUsu(orgao);

                        orgaoUsuario = daoProvider.get().consultarPorSigla(
                                orgaoUsuario);

                        separado.sigla = orgao;
                    }
                } catch (final Exception ignored) {

                }
            }

            if (complemento != null) {
                separado.complemento = complemento;
            }
        }
        return separado;
    }

    /**
     * Retorna a parte numérica da matrícula
     *
     * @param matricula a matrícula
     * @return a parte numérica da matrícula ou null se não existir
     * @throws AplicacaoException caso a matrícula não seja válida
     */
    public String getParteNumericaDaMatricula(String matricula)
            throws AplicacaoException {
        validaPreenchimentoMatricula(matricula);
        Separado separado = separar(matricula);
        String strParteNumerica = separado.complemento;

        return strParteNumerica;
    }

    public String getSiglaDoOrgaoDaMatricula(String matricula)
            throws AplicacaoException {
        validaPreenchimentoMatricula(matricula);
        Separado separado = separar(matricula);
        String sigla = separado.sigla;
        if (StringUtils.isNumeric(sigla)) {
            throw new AplicacaoException(
                    "A sigla da matrícula é inválida. Matrícula: " + matricula
                            + ". Sigla: " + sigla);
        }

        return sigla;
    }

    public String getSiglaDoOrgaoDaLotacao(String matricula)
            throws AplicacaoException {
        Separado separado = separar(matricula);
        return separado.sigla;
    }

    public String getSiglaDaLotacao(String matricula)
            throws AplicacaoException {
        Separado separado = separar(matricula);
        return separado.complemento;
    }

    protected void validaPreenchimentoMatricula(String matricula)
            throws AplicacaoException {
        if (StringUtils.isBlank(matricula) || matricula.length() <= 2) {
            throw new AplicacaoException(
                    "A matrícula informada é nula ou inválida. Matrícula: "
                            + matricula);
        }
    }

}
