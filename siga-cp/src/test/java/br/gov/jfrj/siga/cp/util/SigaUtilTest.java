package br.gov.jfrj.siga.cp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SigaUtilTest {

    @Test
    public void testRandomAlfanumericoLength() {
        String s = SigaUtil.randomAlfanumerico(10);
        assertNotNull(s);
        assertEquals(10, s.length());
        assertTrue(s.matches("[A-Za-z0-9]{10}"));
    }

    @Test
    public void testRandomNumerico() {
        String s = SigaUtil.randomNumerico(6);
        assertNotNull(s);
        assertEquals(6, s.length());
        assertTrue(s.matches("[0-9]{6}"));
    }

    @Test
    public void testIsNumericAndEmpty() {
        assertFalse(SigaUtil.isNumeric(null));
        assertTrue(SigaUtil.isNumeric("")); // implementation treats empty string as numeric
        assertTrue(SigaUtil.isNumeric("12345"));
        assertFalse(SigaUtil.isNumeric("12a34"));
    }

    @Test
    public void testValidacaoFormatoSenha() {
        assertTrue(SigaUtil.validacaoFormatoSenha("Aa1bbb2"));
        assertFalse(SigaUtil.validacaoFormatoSenha("short"));
        assertFalse(SigaUtil.validacaoFormatoSenha(null));
        assertFalse(SigaUtil.validacaoFormatoSenha("NOLOWERCASE1"));
        assertFalse(SigaUtil.validacaoFormatoSenha("nouppercase1"));
    }

    @Test
    public void testValidaEmailAndOculta() {
        String email = "fulano.sobrenome@example.gov.br";
        assertTrue(SigaUtil.validaEmail(email));
        String oculto = SigaUtil.ocultaParcialmenteEmail(email);
        assertNotNull(oculto);
        assertTrue(oculto.startsWith(email.substring(0, 4)));
        assertTrue(oculto.endsWith(email.substring(email.length() - 6)));

        assertFalse(SigaUtil.validaEmail("invalido@@example"));
        assertNull(SigaUtil.ocultaParcialmenteEmail("invalido@@example"));
    }
}
