package br.gov.jfrj.siga.cp;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.*;

import br.gov.jfrj.siga.dp.*;
import br.gov.jfrj.siga.model.ActiveRecord;

@Entity
@Table(name = "corporativo.cp_configuracao")
@Inheritance(strategy = InheritanceType.JOINED)
public class CpConfiguracao extends AbstractCpConfiguracao implements CpConvertableEntity {

    public static final ActiveRecord<CpConfiguracao> AR = new ActiveRecord<>(CpConfiguracao.class);

    public CpConfiguracao() {
    }

    @Transient
    private boolean buscarPorPerfis = false;

    public boolean isBuscarPorPerfis() {
        return buscarPorPerfis;
    }

    public void setBuscarPorPerfis(boolean buscarPorPerfis) {
        this.buscarPorPerfis = buscarPorPerfis;
    }

    public boolean isEspecifica(CpConfiguracao filtro) {
        if (filtro.getDpPessoa() != null)
            return getDpPessoa() != null;
        if (filtro.getLotacao() != null)
            return getLotacao() != null;
        if (filtro.getOrgaoUsuario() != null)
            return getOrgaoUsuario() != null;
        if (filtro.getCpGrupo() != null)
            return getCpGrupo() != null && getCpGrupo().getId().equals(filtro.getCpGrupo().getId());
        return false;
    }

    public Long getId() {
        return getIdConfiguracao();
    }

    public void setId(Long id) {
        setIdConfiguracao(id);
    }

    /**
     * @return retorna o objeto que é a origem da configuração
     */
    public Object getOrigem() {
        if (getDpPessoa() != null) {
            return getDpPessoa();
        } else if (getLotacao() != null) {
            return getLotacao();
        } else if (getCpGrupo() != null) {
            return getCpGrupo();
        } else if (getOrgaoUsuario() != null) {
            return getOrgaoUsuario();
        } else {
            return null;
        }
    }

    /**
     * @return retorna uma string representativa da origem para exibições curtas
     */
    public String printOrigemCurta() {
        Object ori = getOrigem();
        if (ori instanceof DpPessoa) {
            DpPessoa pes = (DpPessoa) ori;
            return (pes.getSesbPessoa() + pes.getMatricula());
        } else if (ori instanceof DpLotacao) {
            return ((DpLotacao) ori).getSiglaLotacao();
        } else if (ori instanceof CpGrupo) {
            return ((CpGrupo) ori).getSiglaGrupo();
        } else if (ori instanceof CpOrgaoUsuario) {
            return ((CpOrgaoUsuario) ori).getSiglaOrgaoUsu();
        } else {
            return "";
        }
    }

    /**
     * @return retorna uma String representativa da origem
     */
    public String printOrigem() {
        Object ori = getOrigem();
        if (ori instanceof DpPessoa) {
            return ((DpPessoa) ori).getNomePessoa();
        } else if (ori instanceof DpLotacao) {
            return ((DpLotacao) ori).getNomeLotacao();
        } else if (ori instanceof CpGrupo) {
            return ((CpGrupo) ori).getDescricao();
        } else if (ori instanceof CpOrgaoUsuario) {
            return ((CpOrgaoUsuario) ori).getNmOrgaoUsu();
        } else {
            return "";
        }
    }

    public boolean ativaNaData(Date dt) {
        return super.ativoNaData(dt);
    }

    /**
     * Retorna a data de fim de vigência no formato dd/mm/aa HH:MM:SS, por exemplo,
     * 01/02/10 17:52:23.
     */
    public String getHisDtFimDDMMYY_HHMMSS() {
        if (getHisDtFim() != null) {
            final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            return df.format(getHisDtFim());
        }
        return "";
    }

    public String getHisDtIniDDMMYY() {
        if (getHisDtIni() != null) {
            final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
            return df.format(getHisDtIni());
        }
        return "";
    }

    @Override
    public String toString() {
        return "id: " + getId() + " ,pessoa: " + (getDpPessoa() != null ? getDpPessoa().getNomePessoa() : "")
                + " ,lotacao: " + (getLotacao() != null ? getLotacao().getSigla() : "") + " ,situação: "
                + (getCpSituacaoConfiguracao() != null ? getCpSituacaoConfiguracao().getDescr() : "")
                + " ,tipo conf: " + (getCpTipoConfiguracao().getDescr());
    }

    public void atualizarObjeto() {
        setLotacao(getLotacao().getHistoricoAtual());
        setCargo(getCargo().getHistoricoAtual());
        setFuncaoConfianca(getFuncaoConfianca().getHistoricoAtual());
        setDpPessoa(getDpPessoa().getHistoricoAtual());
        setCpIdentidade(getCpIdentidade().getHistoricoAtual());
        setLotacaoObjeto(getLotacaoObjeto().getHistoricoAtual());
        setCargoObjeto(getCargoObjeto().getHistoricoAtual());
        setFuncaoConfiancaObjeto(getFuncaoConfiancaObjeto().getHistoricoAtual());
        setPessoaObjeto(getPessoaObjeto().getHistoricoAtual());
        setCpGrupo(getCpGrupo().getHistoricoAtual());
    }

    public void substituirPorObjetoInicial() {
        setLotacao(getLotacao().getHistoricoInicial());
        setCargo(getCargo().getHistoricoInicial());
        setFuncaoConfianca(getFuncaoConfianca().getHistoricoInicial());
        setDpPessoa(getDpPessoa().getHistoricoInicial());
        setCpIdentidade(getCpIdentidade().getHistoricoInicial());
        setLotacaoObjeto(getLotacaoObjeto().getHistoricoInicial());
        setCargoObjeto(getCargoObjeto().getHistoricoInicial());
        setFuncaoConfiancaObjeto(getFuncaoConfiancaObjeto().getHistoricoInicial());
        setPessoaObjeto(getPessoaObjeto().getHistoricoInicial());
        setCpGrupo(getCpGrupo().getHistoricoInicial());
    }

    public CpConfiguracaoCache converterParaCache() {
        return new CpConfiguracaoCache(this);
    }
}
