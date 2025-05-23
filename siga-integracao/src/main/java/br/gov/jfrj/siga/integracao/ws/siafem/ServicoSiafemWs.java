package br.gov.jfrj.siga.integracao.ws.siafem;

import br.gov.jfrj.siga.base.AplicacaoException;

import java.util.Calendar;

public class ServicoSiafemWs {
    public static boolean enviarDocumento(String usuario, String senha, SiafDoc doc) throws AplicacaoException {
        RecebeMSG service = new RecebeMSG();

        try {
            String ano = Calendar.getInstance().get(Calendar.YEAR) + "";

            String ret = service.getRecebeMSGSoap().mensagem(usuario, senha, ano, "", doc.getSiafDoc());

            return verificarRetorno(ret);
        } catch (Exception e) {
            throw new AplicacaoException("Erro na integração com SIAFEM: " + e.getMessage(), 0, e);
        }

    }

    private static boolean verificarRetorno(String ret) throws Exception {
        if (ret == null || ret.isEmpty())
            throw new AplicacaoException("não retornou resposta.");

        String statusOperacao = extrairValor(ret, "StatusOperacao");
        String msgErro = extrairValor(ret, "MsgErro");
        String msgRetorno = extrairValor(ret, "MsgRetorno");
        String mensagemResult = extrairValor(ret, "MensagemResult");
        String msgRetornoSemPapel = extrairValor(ret, "MsgRetornoSemPapel");

        if (statusOperacao == null || statusOperacao.isEmpty()) {
            if ((msgErro == null || msgErro.isEmpty()) &&
                    (msgRetorno == null || msgRetorno.isEmpty()) &&
                    (mensagemResult == null || mensagemResult.isEmpty()) &&
                    (msgRetornoSemPapel == null || msgRetornoSemPapel.isEmpty()))
                msgErro = ret;
            throw new AplicacaoException(formatarRetorno(msgErro, msgRetorno, mensagemResult, msgRetornoSemPapel));
        } else if ((statusOperacao != null && statusOperacao.equals("false")) ||
                !msgErro.isEmpty() || !mensagemResult.isEmpty() || !msgRetornoSemPapel.isEmpty()) {
            throw new AplicacaoException(formatarRetorno(msgErro, msgRetorno, mensagemResult, msgRetornoSemPapel));
        }

        return true;
    }

    private static String formatarRetorno(String... mensagens) {
        StringBuilder ret = new StringBuilder();

        for (String mensagen : mensagens) {
            if (ret.length() == 0)
                ret = new StringBuilder(mensagen);
            else if (!mensagen.isEmpty())
                ret.append(" | ")
                        .append(mensagen);
        }

        return ret.toString();
    }

    private static String extrairValor(String xml, String atributo) {
        int inicio = xml.indexOf(atributo) + atributo.length() + 1;
        int fim = xml.indexOf("</" + atributo);

        if (inicio != -1 && fim > inicio)
            return xml.substring(inicio, fim);

        return "";
    }
}
