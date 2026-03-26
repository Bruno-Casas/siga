package br.gov.jfrj.siga.model;

/**
 * Interface simples para objetos que precisam ser sincronizados (encaixados)
 * entre uma lista vinda do formulário e o banco de dados.
 */
public interface SincronizavelSimples {
    /**
     * Retorna um identificador estável para o objeto (ex: hisIdIni ou um ID transiente do editor).
     */
    String getIdSincronizacao();
}
