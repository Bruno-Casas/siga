package br.gov.jfrj.siga.dp;

import java.io.Serializable;
import javax.persistence.*;

import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.cp.model.HistoricoAuditavel;
import br.gov.jfrj.siga.model.HistoricoSuporte;

/**
 * Classe que representa uma linha na tabela DP_CARGO. Você pode customizar o
 * comportamento desta classe editando a classe {@link DpCargo}.
 */
@MappedSuperclass
@NamedQueries({
        @NamedQuery(name = "consultarPorSiglaDpCargo", query = "select cargo from DpCargo cargo where cargo.siglaCargo = :siglaCargo"),
        @NamedQuery(name = "consultarPorFiltroDpCargo", query = "from DpCargo o "
                + "  where (:nome = null or upper(o.nomeCargoAI) like upper('%' || :nome || '%'))"
                + "  	and (:idOrgaoUsu = null or :idOrgaoUsu = 0L or o.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu)"
                + "   	and o.hisDtFim = null" + "   	order by upper(o.nomeCargo)"),
        @NamedQuery(name = "consultarPorFiltroDpCargoInclusiveInativos", query = "from DpCargo cargo"
                + "     where (:idOrgaoUsu = null or :idOrgaoUsu = 0L or cargo.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu)"
                + "  	and (:nome = null or upper(cargo.nomeCargoAI) like upper('%' || :nome || '%'))"
                + "		and exists (select 1 from DpCargo cAux where cAux.hisIdIni = cargo.hisIdIni"
                + "			group by cAux.hisIdIni having max(cAux.hisDtIni) = cargo.hisDtIni)"
                + "   	order by upper(nomeCargo)"),
        @NamedQuery(name = "consultarQuantidadeDpCargo", query = "select count(o) from DpCargo o "
                + "  where (:nome = null or upper(o.nomeCargoAI) like upper('%' || :nome || '%'))"
                + "  	and (:idOrgaoUsu = null or :idOrgaoUsu = 0L or o.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu)"
                + "   	and o.hisDtFim = null"),
        @NamedQuery(name = "consultarQuantidadeDpCargoInclusiveInativos", query = "select count(distinct cargo.hisIdIni) from DpCargo cargo "
                + "     where (:idOrgaoUsu = null or :idOrgaoUsu = 0L or cargo.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu)"
                + "  	and (:nome = null or upper(cargo.nomeCargoAI) like upper('%' || :nome || '%'))"),
        @NamedQuery(name = "consultarPorNomeDpCargoOrgao", query = "select cargo from DpCargo cargo "
                + " where upper(REMOVE_ACENTO(cargo.nomeCargo)) = upper(REMOVE_ACENTO(:nome)) and cargo.orgaoUsuario.idOrgaoUsu = :idOrgaoUsuario and cargo.hisDtFim = null")})
public abstract class AbstractDpCargo extends HistoricoSuporte<DpCargo> implements Serializable, HistoricoAuditavel<DpCargo> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID_CARGO", nullable = false)
    private Long idCargo;

    /**
     * Campos que geram versionamento de registro
     **/
    @Column(name = "NOME_CARGO", nullable = false, length = 100)
    private String nomeCargo;

    @Column(name = "SIGLA_CARGO", length = 30)
    private String siglaCargo;

    @Column(name = "IDE_CARGO", length = 256)
    private String ideCargo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ORGAO_USU", nullable = false)
    private CpOrgaoUsuario orgaoUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HIS_IDC_INI")
    private CpIdentidade hisIdcIni;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HIS_IDC_FIM")
    private CpIdentidade hisIdcFim;

    public Long getIdCargo() {
        return idCargo;
    }

    public String getNomeCargo() {
        return nomeCargo;
    }

    public void setIdCargo(final Long idCargo) {
        this.idCargo = idCargo;
    }

    public void setNomeCargo(final String nomeCargo) {
        this.nomeCargo = nomeCargo;
    }

    @Override
    public boolean equals(final Object rhs) {
        if (!(rhs instanceof DpCargo))
            return false;
        final DpCargo that = (DpCargo) rhs;

        if ((this.getIdCargo() == null ? that.getIdCargo() == null : this
                .getIdCargo().equals(that.getIdCargo()))) {
            return this.getNomeCargo() == null ? that.getNomeCargo() == null
                    : this.getNomeCargo().equals(that.getNomeCargo());

        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        int idValue = this.getIdCargo() == null ? 0 : this.getIdCargo()
                .hashCode();
        result = result * 37 + idValue;
        idValue = this.getNomeCargo() == null ? 0 : this.getNomeCargo()
                .hashCode();
        result = result * 37 + idValue;

        return result;

    }

    public String getIdeCargo() {
        return ideCargo;
    }

    public void setIdeCargo(String ideCargo) {
        this.ideCargo = ideCargo;
    }

    public CpOrgaoUsuario getOrgaoUsuario() {
        return orgaoUsuario;
    }

    public void setOrgaoUsuario(CpOrgaoUsuario orgaoUsuario) {
        this.orgaoUsuario = orgaoUsuario;
    }

    public String getSiglaCargo() {
        return siglaCargo;
    }

    public void setSiglaCargo(String siglaCargo) {
        this.siglaCargo = siglaCargo;
    }

    public CpIdentidade getHisIdcIni() {
        return hisIdcIni;
    }

    public void setHisIdcIni(CpIdentidade hisIdcIni) {
        this.hisIdcIni = hisIdcIni;
    }

    public CpIdentidade getHisIdcFim() {
        return hisIdcFim;
    }

    public void setHisIdcFim(CpIdentidade hisIdcFim) {
        this.hisIdcFim = hisIdcFim;
    }


}
