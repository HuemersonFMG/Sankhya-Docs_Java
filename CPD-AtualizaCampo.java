

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
    NativeSql sqlConexaoDocs = new NativeSql(jdbc);
    sqlConexaoDocs.appendSql("UPDATE AD_CONTPRODD
        SET CPD.CODPROD = PRO.CODPROD, CPD.AD_QTD = CAB.QTDVOL, CPD.AD_DT_PROD = TRUNC(SYSDATE), CPD.AD_USU_INC = STP_GET_CODUSULOGADO
        FROM AD_CONTPRODD CPD
        INNER JOIN TGFCAB CAB ON CAB.NUNOTA = CPD.AD_UN_ORD
        INNER JOIN TGFITE ITE ON ITE.NUNOTA = CPD.AD_UN_ORD
        INNER JOIN TGFPRO PRO ON PRO.CODPROD = ITE.CODPROD
        WHERE CPD.AD_UN_ORD = :NUNOTA");
    sqlConexaoDocs.setNamedParameter("NUNOTA", nunota);
    sqlConexaoDocs.executeUpdate();
  }
}