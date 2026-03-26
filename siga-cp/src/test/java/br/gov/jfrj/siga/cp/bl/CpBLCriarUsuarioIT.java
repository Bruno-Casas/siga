package br.gov.jfrj.siga.cp.bl;

import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.dp.DpPessoa;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testes de integração para o método criarUsuario da classe CpBL.
 * 
 * Esta classe testa o método criarUsuario com dados variados, validações
 * obrigatórias e casos de erro, utilizando banco de dados em-memória (H2)
 * para testes isolados e rápidos.
 */
public class CpBLCriarUsuarioIT {

    private CpBL cpBL;
    private CpIdentidade identidadeCadastrante;

    @Before
    public void setUp() throws Exception {
        cpBL = new CpBL();
        // Nota: identidadeCadastrante permanece null para testes de validação básica
        // Para testes completos com banco de dados, estender InMemoryDatabaseHibernateTestSupport
    }

    /**
     * Testa validação de órgão obrigatório.
     * 
     * Verifica se a exceção é lançada quando o órgão não é informado.
     */
    @Test
    public void testCriarUsuarioSemOrgao() {
        try {
            cpBL.criarUsuario(
                    null,  // id
                    identidadeCadastrante,
                    null,  // idOrgaoUsu - OBRIGATÓRIO
                    "00002",  // mnMatricula
                    1L,  // idCargo
                    null,  // idFuncao
                    1L,  // idLotacao
                    "João Silva",  // nmPessoa
                    "01/01/1990",  // dtNascimento
                    "123.456.789-01",  // cpf
                    "joao.silva@exemplo.com.br",  // email
                    "123456789",  // identidade
                    "TJRJ",  // orgaoIdentidade
                    "SP",  // ufIdentidade
                    "10/05/2015",  // dataExpedicaoIdentidade
                    "João Silva",  // nomeExibicao
                    "N"  // enviarEmail
            );
            fail("Deve lançar exceção quando órgão não informado");
        } catch (AplicacaoException e) {
            assertEquals("Órgão não informado", e.getMessage());
        }
    }

    /**
     * Testa validação de cargo obrigatório.
     * 
     * Verifica se a exceção é lançada quando o cargo não é informado.
     */
    @Test
    public void testCriarUsuarioSemCargo() {
        try {
            cpBL.criarUsuario(
                    null,  // id
                    identidadeCadastrante,
                    1L,  // idOrgaoUsu
                    "00002",  // mnMatricula
                    null,  // idCargo - OBRIGATÓRIO
                    null,  // idFuncao
                    1L,  // idLotacao
                    "João Silva",  // nmPessoa
                    "01/01/1990",  // dtNascimento
                    "123.456.789-01",  // cpf
                    "joao.silva@exemplo.com.br",  // email
                    "123456789",  // identidade
                    "TJRJ",  // orgaoIdentidade
                    "SP",  // ufIdentidade
                    "10/05/2015",  // dataExpedicaoIdentidade
                    "João Silva",  // nomeExibicao
                    "N"  // enviarEmail
            );
            fail("Deve lançar exceção quando cargo não informado");
        } catch (AplicacaoException e) {
            assertEquals("Cargo não informado", e.getMessage());
        }
    }

    /**
     * Testa validação de lotação obrigatória.
     * 
     * Verifica se a exceção é lançada quando a lotação não é informada.
     */
    @Test
    public void testCriarUsuarioSemLotacao() {
        try {
            cpBL.criarUsuario(
                    null,  // id
                    identidadeCadastrante,
                    1L,  // idOrgaoUsu
                    "00002",  // mnMatricula
                    1L,  // idCargo
                    null,  // idFuncao
                    null,  // idLotacao - OBRIGATÓRIO
                    "João Silva",  // nmPessoa
                    "01/01/1990",  // dtNascimento
                    "123.456.789-01",  // cpf
                    "joao.silva@exemplo.com.br",  // email
                    "123456789",  // identidade
                    "TJRJ",  // orgaoIdentidade
                    "SP",  // ufIdentidade
                    "10/05/2015",  // dataExpedicaoIdentidade
                    "João Silva",  // nomeExibicao
                    "N"  // enviarEmail
            );
            fail("Deve lançar exceção quando lotação não informada");
        } catch (AplicacaoException e) {
            assertEquals("Lotação não informado", e.getMessage());
        }
    }

    /**
     * Testa validação de nome obrigatório.
     * 
     * Verifica se a exceção é lançada quando o nome não é informado.
     */
    @Test
    public void testCriarUsuarioSemNome() {
        try {
            cpBL.criarUsuario(
                    null,  // id
                    identidadeCadastrante,
                    1L,  // idOrgaoUsu
                    "00002",  // mnMatricula
                    1L,  // idCargo
                    null,  // idFuncao
                    1L,  // idLotacao
                    "",  // nmPessoa - OBRIGATÓRIO
                    "01/01/1990",  // dtNascimento
                    "123.456.789-01",  // cpf
                    "joao.silva@exemplo.com.br",  // email
                    "123456789",  // identidade
                    "TJRJ",  // orgaoIdentidade
                    "SP",  // ufIdentidade
                    "10/05/2015",  // dataExpedicaoIdentidade
                    "João Silva",  // nomeExibicao
                    "N"  // enviarEmail
            );
            fail("Deve lançar exceção quando nome não informado");
        } catch (AplicacaoException e) {
            assertEquals("Nome não informado", e.getMessage());
        }
    }

    /**
     * Testa validação de CPF obrigatório.
     * 
     * Verifica se a exceção é lançada quando o CPF não é informado.
     */
    @Test
    public void testCriarUsuarioSemCPF() {
        try {
            cpBL.criarUsuario(
                    null,  // id
                    identidadeCadastrante,
                    1L,  // idOrgaoUsu
                    "00002",  // mnMatricula
                    1L,  // idCargo
                    null,  // idFuncao
                    1L,  // idLotacao
                    "João Silva",  // nmPessoa
                    "01/01/1990",  // dtNascimento
                    "",  // cpf - OBRIGATÓRIO
                    "joao.silva@exemplo.com.br",  // email
                    "123456789",  // identidade
                    "TJRJ",  // orgaoIdentidade
                    "SP",  // ufIdentidade
                    "10/05/2015",  // dataExpedicaoIdentidade
                    "João Silva",  // nomeExibicao
                    "N"  // enviarEmail
            );
            fail("Deve lançar exceção quando CPF não informado");
        } catch (AplicacaoException e) {
            assertEquals("CPF não informado", e.getMessage());
        }
    }

    /**
     * Testa validação de email obrigatório.
     * 
     * Verifica se a exceção é lançada quando o email não é informado.
     */
    @Test
    public void testCriarUsuarioSemEmail() {
        try {
            cpBL.criarUsuario(
                    null,  // id
                    identidadeCadastrante,
                    1L,  // idOrgaoUsu
                    "00002",  // mnMatricula
                    1L,  // idCargo
                    null,  // idFuncao
                    1L,  // idLotacao
                    "João Silva",  // nmPessoa
                    "01/01/1990",  // dtNascimento
                    "123.456.789-01",  // cpf
                    "",  // email - OBRIGATÓRIO
                    "123456789",  // identidade
                    "TJRJ",  // orgaoIdentidade
                    "SP",  // ufIdentidade
                    "10/05/2015",  // dataExpedicaoIdentidade
                    "João Silva",  // nomeExibicao
                    "N"  // enviarEmail
            );
            fail("Deve lançar exceção quando email não informado");
        } catch (AplicacaoException e) {
            assertEquals("E-mail não informado", e.getMessage());
        }
    }

    /**
     * Testa validação de formato de email.
     * 
     * Verifica se a exceção é lançada quando o email possui formato inválido.
     */
    @Test
    public void testCriarUsuarioComEmailInvalido() {
        try {
            cpBL.criarUsuario(
                    null,  // id
                    identidadeCadastrante,
                    1L,  // idOrgaoUsu
                    "00002",  // mnMatricula
                    1L,  // idCargo
                    null,  // idFuncao
                    1L,  // idLotacao
                    "João Silva",  // nmPessoa
                    "01/01/1990",  // dtNascimento
                    "123.456.789-01",  // cpf
                    "email_invalido",  // email - INVÁLIDO
                    "123456789",  // identidade
                    "TJRJ",  // orgaoIdentidade
                    "SP",  // ufIdentidade
                    "10/05/2015",  // dataExpedicaoIdentidade
                    "João Silva",  // nomeExibicao
                    "N"  // enviarEmail
            );
            fail("Deve lançar exceção quando email inválido");
        } catch (AplicacaoException e) {
            assertEquals("E-mail inválido", e.getMessage());
        }
    }

    /**
     * Testes de data de nascimento/expedição futura e nome inválido
     * requerem InMemoryDatabaseHibernateTestSupport para inicializar
     * a identidade cadastrante. Veja CpBLCriarUsuarioFullIT para testes completos.
     */
}
