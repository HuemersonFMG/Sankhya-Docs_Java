

/*
    >>Tela personalizada Top Cestas: (Controle de Produção Diária)
    >>Atualizando campos da Tabela baseado no número do pedido
    >>Sankhya / Construtor de Telas / AD_CONTPRODD ->Categoria-Produção
*/

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.comercial.impostos.ImpostosHelpper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import entities.ItemNotaFiscal;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

public class AtualizaCampos implements AcaoRotinaJava, ScheduledAction
{
  private void atualizar(BigDecimal nunota) throws Exception
  {
    JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();

    // Inicia uma transação
    jdbc.beginTrans();

    try {
        NativeSql sqlConexaoDocs = new NativeSql(jdbc);
        sqlConexaoDocs.appendSql("UPDATE AD_CONTPRODD CPD ");
        sqlConexaoDocs.appendSql("SET CPD.CODPROD = PRO.CODPROD, ");
        sqlConexaoDocs.appendSql("CPD.AD_QTD = CAB.QTDVOL, ");
        sqlConexaoDocs.appendSql("CPD.AD_DT_PROD = TRUNC(SYSDATE), ");
        sqlConexaoDocs.appendSql("CPD.AD_USU_INC = STP_GET_CODUSULOGADO ");
        sqlConexaoDocs.appendSql("FROM AD_CONTPRODD CPD ");
        sqlConexaoDocs.appendSql("INNER JOIN TGFCAB CAB ON CAB.NUNOTA = CPD.AD_UN_ORD ");
        sqlConexaoDocs.appendSql("INNER JOIN TGFITE ITE ON ITE.NUNOTA = CPD.AD_UN_ORD ");
        sqlConexaoDocs.appendSql("INNER JOIN TGFPRO PRO ON PRO.CODPROD = ITE.CODPROD ");
        sqlConexaoDocs.appendSql("WHERE CPD.AD_UN_ORD = :NUNOTA");

        sqlConexaoDocs.setNamedParameter("NUNOTA", nunota);
        sqlConexaoDocs.executeUpdate();

        // Confirma a transação se tudo ocorreu bem
        jdbc.commit();
    } catch (Exception e) {
        // Reverte a transação em caso de erro
        jdbc.rollback();
        throw e; // Lança a exceção para ser tratada no nível superior
    } finally {
        // Certifique-se de fechar a conexão JDBC no final
        jdbc.close();
    }
}
}