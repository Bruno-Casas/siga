package br.gov.jfrj.siga.dp;

import br.gov.jfrj.siga.base.util.Texto;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.model.Selecionavel;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "corporativo.dp_cargo")
@Cache(region = CpDao.CACHE_CORPORATIVO, usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class DpCargo extends AbstractDpCargo implements Serializable, Selecionavel, DpConvertableEntity {

    @Formula(value = "REMOVE_ACENTO(NOME_CARGO)")
    private String nomeCargoAI;

    public String getNomeCargoAI() {
        return nomeCargoAI;
    }

    public DpCargo() {

    }

    public String getSigla() {
        return getSiglaCargo();
    }

    public Long getId() {
        return getIdCargo();
    }

    public String getDescricao() {
        return getNomeCargo();
    }

    public String getDescricaoIniciaisMaiusculas() {
        return Texto.maiusculasEMinusculas(getDescricao());
    }

    public void setSigla(String sigla) {
        setSiglaCargo(sigla);
    }

    public void setId(Long id) {
        setIdCargo(id);
    }

    public void setDescricao(String descricao) {
        setNomeCargo(descricao);

    }

    public Date getDataFim() {
        return this.getHisDtFim();
    }

    public Date getDataInicio() {
        return this.getHisDtIni();
    }

    public String getIdExterna() {
        return getIdeCargo();
    }

    public void setDataFim(Date dataFim) {
        this.setHisDtFim(dataFim);
    }

    public void setDataInicio(Date dataInicio) {
        this.setHisDtIni(dataInicio);
    }

    public void setIdExterna(String idExterna) {
        setIdeCargo(idExterna);
    }

    public void setIdInicial(Long idInicial) {
        this.setHisIdIni(idInicial);
    }
}
