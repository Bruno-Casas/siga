package br.gov.jfrj.siga.cp.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CpLogicTest {

    @Test
    public void testCpIgualEvalExplain() {
        CpIgual c = new CpIgual("a", "A", "a", "B");
        assertTrue(c.eval());
        String explain = c.explain(true);
        assertTrue(explain.contains("igual"));

        CpIgual c2 = new CpIgual(null, "X", null, "Y");
        assertTrue(c2.eval());
        assertEquals("X igual a Y", c2.explain(true));
    }

    @Test
    public void testCpDiferenteEvalExplain() {
        CpDiferente c = new CpDiferente("a", "A", "b", "B");
        assertTrue(c.eval());
        assertEquals("A diferente de B", c.explain(true));

        CpDiferente c2 = new CpDiferente(null, "X", null, "Y");
        assertFalse(c2.eval());
        assertTrue(c2.explain(false).length() > 0);
    }

    @Test
    public void testCpPodeBoolean() {
        CpPodeBoolean p = new CpPodeBoolean(true, "teste");
        assertTrue(p.eval());
        assertTrue(p.explain(true).contains("teste"));

        CpPodeBoolean p2 = new CpPodeBoolean(false, "outra");
        assertFalse(p2.eval());
        assertTrue(p2.explain(false).contains("outra"));
    }
}
