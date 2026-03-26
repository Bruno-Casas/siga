package br.gov.jfrj.siga.dp;

import br.gov.jfrj.siga.base.util.Texto;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.model.ActiveRecord;
import br.gov.jfrj.siga.model.Selecionavel;
import br.gov.jfrj.siga.parser.SiglaParser;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import static java.util.Objects.nonNull;

@Entity
@Table(name = "corporativo.dp_lotacao")
@Cache(region = CpDao.CACHE_CORPORATIVO, usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class DpLotacao extends AbstractDpLotacao implements Serializable,
        Selecionavel, Comparable<DpLotacao>, DpConvertableEntity {

    public static ActiveRecord<DpLotacao> AR = new ActiveRecord<>(
            DpLotacao.class);

    public DpLotacao() {
        super();
    }

    public boolean isFechada() {
        if (this.getHisDtFim() == null)
            return false;
        DpLotacao lotIni = getLotacaoInicial();
        Set<DpLotacao> setLotas = lotIni.getLotacoesPosteriores();
        if (setLotas != null)
            for (DpLotacao l : setLotas)
                if (l.getHisDtFim() == null)
                    return false;

        return true;
    }

    /**
     * Retorna o nome da Localidade da {@link DpLotacao Lotação}.
     *
     * @return O {@link CpLocalidade#getNmLocalidade() nome} da {@link CpLocalidade
     * Localidade} ou o {@link CpOrgaoUsuario#getMunicipioOrgaoUsu()
     * município} do {@link CpOrgaoUsuario Órgão} ou
     * "{@literal Indeterminado}".
     */
    public String getLocalidadeString() {
        if (nonNull(getLocalidade())) {
            return getLocalidade().getNmLocalidade();
        }
        if (nonNull(getOrgaoUsuario())) {
            return getOrgaoUsuario().getMunicipioOrgaoUsu();
        }
        return "Indeterminado";
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
        return iniciais(getNomeLotacao());
    }

    public Long getId() {
        return super.getIdLotacao();
    }

    public void setId(Long id) {
        setIdLotacao(id);
    }

    public Long getIdLotacaoPai() {
        if (getLotacaoPai() == null)
            return null;
        return getLotacaoPai().getIdLotacao();
    }

    public Long getIdLotacaoIniPai() {
        if (getLotacaoPai() == null)
            return null;
        return getLotacaoPai().getHisIdIni();
    }

    public String getSigla() {
        String s = getSiglaLotacao();
        if (isFechada())
            s += " (extinta)";
        return s;
    }

    public String getSiglaCompleta() {
        return getOrgaoUsuario().getSiglaOrgaoUsu() + getSiglaLotacao();
    }

    public String getSiglaCompletaFormatada() {
        return getOrgaoUsuario().getSiglaOrgaoUsu() + "-" + getSiglaLotacao();
    }

    public void setSigla(String sigla) {
        if (sigla == null) {
            sigla = "";
        }
        setSiglaLotacao(sigla.toUpperCase());

        validarESepararSigla();
    }

    /*
     * public String getSiglaLotacao() { if (getOrgaoUsuario() != null) return
     * getOrgaoUsuario().getAcronimoOrgaoUsu() + "/" + super.getSiglaLotacao();
     * return super.getSiglaLotacao(); }
     */

    // TAH: Foi necessario criar essa funcao para resolver um problema
    // especifico da SJRJ-SRH. Optamos por fazer dessa forma por acreditarmos
    // que no futuro essa necessidade seja revista.
    public String getSiglaAmpliada() {
        if (getOrgaoUsuario().getIdOrgaoUsu() == 1L && isSubsecretaria())
            return getSiglaLotacao() + " Direção";
        return getSiglaLotacao();
    }

    public String getDescricao() {
        return getNomeLotacao();
    }

    public String getDescricaoAmpliada() {
        if (getOrgaoUsuario().getIdOrgaoUsu() == 1L && isSubsecretaria())
            return "Direção da " + getNomeLotacao();
        return getNomeLotacao();
    }

    public String getDescricaoIniciaisMaiusculas() {
        return Texto.maiusculasEMinusculas(getDescricao());
    }

    public Boolean isSubsecretaria() {
        return getSiglaLotacao().length() == 3;
    }

    public DpLotacao getLotacaoPorNivelMaximo(int iNivelMaximo) {
        int iNivel = 1;
        DpLotacao lot = this;

        while (lot.getLotacaoPai() != null) {
            lot = lot.getLotacaoPai();
            iNivel++;
        }
        if (iNivel <= iNivelMaximo) {
            return this;
        }

        lot = this;
        for (; iNivel > iNivelMaximo; iNivel--) {
            lot = lot.getLotacaoPai();
        }
        return lot;
    }

    public String getIdExterna() {
        return getIdeLotacao();
    }

    public boolean ativaNaData(Date dt) {
        if (dt == null)
            return this.getHisDtFim() == null;
        if (dt.before(this.getHisDtIni()))
            return false;
        // dt >= DtIni
        if (this.getHisDtFim() == null)
            return true;
        return dt.before(this.getHisDtFim());
    }

    public Long getIdOrgaoUsuario() {
        Long idOrgaoUsuario = null;
        CpOrgaoUsuario orgaoUsuario = super.getOrgaoUsuario();
        if (orgaoUsuario != null) {
            idOrgaoUsuario = orgaoUsuario.getId();
        }
        return idOrgaoUsuario;
    }

    public int compareTo(DpLotacao other) {
        return getNomeLotacao().compareTo(other.getNomeLotacao());
    }

    @Override
    public String toString() {
        return getSiglaCompleta();
    }

    @Override
    public String getSiglaDePessoaEOuLotacao() {
        return SiglaParser.makeSigla(null, this);
    }

    @PrePersist
    @PreUpdate
    private void validarESepararSigla() {
        if (this.siglaLotacao == null || this.siglaLotacao.isEmpty()) {
            return;
        }

        String siglaUpper = this.siglaLotacao.toUpperCase();

        if (this.orgaoUsuario != null && this.orgaoUsuario.getSiglaOrgaoUsu() != null) {
            String siglaOrgao = this.orgaoUsuario.getSiglaOrgaoUsu().toUpperCase();

            if (siglaUpper.startsWith(siglaOrgao)) {
                String resto = siglaUpper.substring(siglaOrgao.length());
                if (resto.startsWith("-") || resto.startsWith("_")) {
                    resto = resto.substring(1);
                }
                this.siglaLotacao = resto.isEmpty() ? siglaUpper : resto;
            } else {
                this.siglaLotacao = siglaUpper;
            }
        } else {
            this.siglaLotacao = siglaUpper;
        }
    }
}