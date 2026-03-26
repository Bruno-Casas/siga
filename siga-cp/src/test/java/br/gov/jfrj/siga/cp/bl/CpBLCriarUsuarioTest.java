package br.gov.jfrj.siga.cp.bl;

import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.model.DataTestBase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class CpBLCriarUsuarioTest extends DataTestBase {

    @Test
    public void naoDevePassarPelasGuardsDeDadosInvalidos() {
        CpBL sut = new CpBL();
        AplicacaoException ex = assertThrows(AplicacaoException.class, () ->
                sut.criarUsuario(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null));
        assertEquals("Órgão não informado", ex.getMessage());

        ex = assertThrows(AplicacaoException.class, () ->
                sut.criarUsuario(null, null, 1L, null, null, null, null, null, null, null, null, null, null, null, null, null, null));
        assertEquals("Cargo não informado", ex.getMessage());

        ex = assertThrows(AplicacaoException.class, () ->
                sut.criarUsuario(null, null, 1L, null, 1L, null, null, null, null, null, null, null, null, null, null, null, null));
        assertEquals("Lotação não informado", ex.getMessage());

        ex = assertThrows(AplicacaoException.class, () ->
                sut.criarUsuario(null, null, 1L, null, 1L, null, 1L, null, null, null, null, null, null, null, null, null, null));
        assertEquals("Nome não informado", ex.getMessage());

        ex = assertThrows(AplicacaoException.class, () ->
                sut.criarUsuario(null, null, 1L, null, 1L, null, 1L, "Nom&", null, null, null, null, null, null, null, null, null));
        assertEquals("CPF não informado", ex.getMessage());

        ex = assertThrows(AplicacaoException.class, () ->
                sut.criarUsuario(null, null, 1L, null, 1L, null, 1L, "Nom&", null, "12345678901", null, null, null, null, null, null, null));
        assertEquals("E-mail não informado", ex.getMessage());

        ex = assertThrows(AplicacaoException.class, () ->
                sut.criarUsuario(null, null, 1L, null, 1L, null, 1L, "Nom&", null, "12345678901", "emaildominio.com", null, null, null, null, null, null));
        assertEquals("E-mail inválido", ex.getMessage());

        ex = assertThrows(AplicacaoException.class, () ->
                sut.criarUsuario(null, null, 1L, null, 1L, null, 1L, "Nom&", null, "12345678901", "email@dominio.com", null, null, null, null, null, null));
        assertEquals("Nome com caracteres não permitidos", ex.getMessage());
    }

    @Test
    public void testCriarUsuarioEEdicao() {
        // Arrange: cria usuário ZZ com permissões
        DpPessoa zz = criarUsuarioZZComPermissoes();

        CpIdentidade identidade = new CpIdentidade();
        identidade.setDpPessoa(zz);
        identidade.setCpOrgaoUsuario(zz.getOrgaoUsuario());
        em.persist(identidade);

        CpBL sut = new CpBL();
        // Parâmetros mínimos válidos para passar pelas guards
        Long idOrgaoUsu = zz.getOrgaoUsuario().getId();
        Long idCargo = zz.getIdCargo();
        Long idLotacao = zz.getIdLotacao();
        String nmPessoa = "Nome Teste";
        String cpf = "12345678901";
        String email = "email@dominio.com";

        // Inserção
        DpPessoa pessoaCriada = sut.criarUsuario(null, identidade, idOrgaoUsu, null, idCargo, null, idLotacao, nmPessoa, null, cpf, email, null, null, null, null, null, null);
        assertNotNull(pessoaCriada);
        assertNotNull(pessoaCriada.getId());
        assertEquals(nmPessoa, pessoaCriada.getNomePessoa());
        assertEquals(12345678901L, pessoaCriada.getCpfPessoa());


        CpDao dao = null;

        CpDao spy = Mockito.spy(dao);

        Mockito.doReturn(0)
                .when(spy)
                .quantidadeDocumentos(Mockito.any());

        // Edição
        String novoNome = "Nome Alterado";
        String novoEmail = "novoemail@dominio.com";
        DpPessoa pessoaAlterada = sut.criarUsuario(pessoaCriada.getId(), identidade, idOrgaoUsu, null, idCargo, null, idLotacao, novoNome, null, cpf, novoEmail, null, null, null, null, null, null);

        assertNotNull(pessoaAlterada);
        assertEquals(pessoaCriada.getIdInicial(), pessoaAlterada.getIdInicial());
        assertEquals(novoNome, pessoaAlterada.getNomePessoa());
        assertEquals(novoEmail, pessoaAlterada.getEmailPessoa());
    }
}
