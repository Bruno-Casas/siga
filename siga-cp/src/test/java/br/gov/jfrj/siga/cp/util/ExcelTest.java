package br.gov.jfrj.siga.cp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExcelTest {

    @Test
    public void testMontarIsExternaLotacao() {
        Excel e = new Excel();
        assertEquals(Integer.valueOf(1), e.montarIsExternaLotacao("SIM"));
        assertEquals(Integer.valueOf(0), e.montarIsExternaLotacao("NAO"));
        assertEquals(Integer.valueOf(0), e.montarIsExternaLotacao("NÃO"));
        assertNull(e.montarIsExternaLotacao("XYZ"));
        assertEquals(Integer.valueOf(0), e.montarIsExternaLotacao(""));
    }

    @Test
    public void testValidarIsExternaLotacao() {
        Excel e = new Excel();
        assertEquals("", e.validarIsExternaLotacao("SIM", 1));
        assertNotEquals("", e.validarIsExternaLotacao("XYZ", 2));
    }

    @Test
    public void testValidarCaracteres() {
        Excel e = new Excel();
        assertTrue(e.validarCaracterEspecial("NomeValido"));
        assertFalse(e.validarCaracterEspecial("Nome$Invalido"));

        assertTrue(e.validarCaracter("Nome Valido"));
        assertFalse(e.validarCaracter("Nome-Invalido"));
    }
}
