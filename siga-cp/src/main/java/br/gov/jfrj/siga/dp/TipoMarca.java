package br.gov.jfrj.siga.dp;

public enum TipoMarca {
    SIGA_EX(1L, "Siga Exibição"),
    SIGA_SR(2L, "Siga Secretaria"),
    SIGA_GC(3L, "Siga Gestão");

    private final Long id;
    private final String descricao;

    TipoMarca(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public static TipoMarca findByName(String name) {
        try {
            return TipoMarca.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }
}