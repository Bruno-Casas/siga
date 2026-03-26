package br.gov.jfrj.siga.model;

import java.util.*;

/**
 * Utilitário genérico para reconciliar listas de objetos (Diff).
 * Substitui o antigo Sincronizador da sinc-lib de forma simplificada.
 */
public class SincronizadorSimples<T extends SincronizavelSimples> {

    public enum Operacao { incluir, excluir, alterar }

    public static class Item<T> {
        public T novo;
        public T antigo;
        public Operacao operacao;

        public Item(T novo, T antigo, Operacao operacao) {
            this.novo = novo;
            this.antigo = antigo;
            this.operacao = operacao;
        }
    }

    /**
     * Compara duas coleções e retorna a lista de operações necessárias para 
     * transformar a coleção antiga na nova.
     */
    public List<Item<T>> encaixar(Collection<T> antigos, Collection<T> novos) {
        Map<String, T> mapaAntes = new LinkedHashMap<>();
        Map<String, T> mapaDepois = new LinkedHashMap<>();

        if (antigos != null) {
            for (T a : antigos) {
                if (a != null) mapaAntes.put(a.getIdSincronizacao(), a);
            }
        }

        if (novos != null) {
            for (T n : novos) {
                if (n != null) mapaDepois.put(n.getIdSincronizacao(), n);
            }
        }

        List<Item<T>> resultado = new ArrayList<>();

        // Identifica Inclusões e Alterações
        for (Map.Entry<String, T> entry : mapaDepois.entrySet()) {
            T novo = entry.getValue();
            T antigo = mapaAntes.get(entry.getKey());
            if (antigo == null) {
                resultado.add(new Item<>(novo, null, Operacao.incluir));
            } else {
                resultado.add(new Item<>(novo, antigo, Operacao.alterar));
            }
        }

        // Identifica Exclusões
        for (Map.Entry<String, T> entry : mapaAntes.entrySet()) {
            if (!mapaDepois.containsKey(entry.getKey())) {
                resultado.add(new Item<>(null, entry.getValue(), Operacao.excluir));
            }
        }

        return resultado;
    }
}
