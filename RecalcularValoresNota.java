package acoesPersonalizadas.Impacto;

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

public class RecalcularValoresNota implements AcaoRotinaJava, ScheduledAction {
  public void onTime(ScheduledActionContext scheduledActionContext) {
    try {
      for (ItemNotaFiscal item : RetornaItensParaRecalcular())
        atualizaValorItem(item);
      for (BigDecimal nota : RetornaNotasParaRecalcular()) {
        RecalcularImpostos(nota);
        atualizarParaProcessado(nota);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void doAction(ContextoAcao contextoAcao) throws Exception {
    contextoAcao.confirmar("Recde notas", "Confirma o recdas notas de custo?", 1);
    for (ItemNotaFiscal item : RetornaItensParaRecalcular())
      atualizaValorItem(item);
    for (BigDecimal nota : RetornaNotasParaRecalcular()) {
      RecalcularImpostos(nota);
      atualizarParaProcessado(nota);
    }
    contextoAcao.setMensagemRetorno("Recrealizado com sucesso!");
  }

  private ArrayList<ItemNotaFiscal> RetornaItensParaRecalcular() throws Exception {
    JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
    NativeSql sql = new NativeSql(jdbc);
    sql.appendSql("SELECT CAB.NUNOTA, ITE.CODPROD, ITE.SEQUENCIA, ITE.QTDNEG, CAB.DTENTSAI");
    sql.appendSql(" FROM TGFCAB CAB");
    sql.appendSql(" INNER JOIN TGFITE ITE ON CAB.NUNOTA = ITE.NUNOTA");
    sql.appendSql(" WHERE (CAB.AD_PROCESSADOAUTOMACAO IS NULL OR CAB.AD_PROCESSADOAUTOMACAO = 'N')");
    sql.appendSql(" AND CAB.CODTIPOPER = 500");
    ResultSet rs = sql.executeQuery();
    ArrayList<ItemNotaFiscal> listaItens = new ArrayList<>();
    while (rs.next()) {
      ItemNotaFiscal item = new ItemNotaFiscal();
      item.setNumeroUnicoSankhya(rs.getBigDecimal("NUNOTA"));
      item.setCodigoSankhya(rs.getInt("CODPROD"));
      item.setSequenciaSankhya(rs.getInt("SEQUENCIA"));
      item.setQuantidade(rs.getInt("QTDNEG"));
      item.set_dtNeg(rs.getDate("DTENTSAI"));
      listaItens.add(item);
    }
    rs.close();
    return listaItens;
  }

  private ArrayList<BigDecimal> RetornaNotasParaRecalcular() throws Exception {
    JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
    NativeSql sql = new NativeSql(jdbc);
    sql.appendSql("SELECT CAB.NUNOTA");
    sql.appendSql(" FROM TGFCAB CAB");
    sql.appendSql(" WHERE (CAB.AD_PROCESSADOAUTOMACAO IS NULL OR CAB.AD_PROCESSADOAUTOMACAO = 'N')");
    sql.appendSql(" AND CAB.CODTIPOPER = 500");
    ResultSet rs = sql.executeQuery();
    ArrayList<BigDecimal> listaNotas = new ArrayList<>();
    while (rs.next())
      listaNotas.add(rs.getBigDecimal("NUNOTA"));
    rs.close();
    return listaNotas;
  }

  private void atualizaValorItem(ItemNotaFiscal item) throws Exception {
    JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
    NativeSql sql = new NativeSql(jdbc);
    sql.appendSql("UPDATE TGFITE SET VLRUNIT = :VALOR, VLRTOT = :TOTAL WHERE NUNOTA = :NUNOTA AND CODPROD = :CODPROD AND SEQUENCIA = :SEQUENCIA");
    sql.setNamedParameter("NUNOTA", item.getNumeroUnicoSankhya());
    sql.setNamedParameter("SEQUENCIA", Integer.valueOf(item.getSequenciaSankhya()));
    sql.setNamedParameter("CODPROD", Integer.valueOf(item.getCodigoSankhya()));
    sql.setNamedParameter("VALOR", RetornaUltimoCustoMedioComICMS(BigDecimal.valueOf(item.getCodigoSankhya()), RetornaCodigoCd(), item.get_dtNeg()));
    sql.setNamedParameter("TOTAL", Double.valueOf(RetornaUltimoCustoMedioComICMS(BigDecimal.valueOf(item.getCodigoSankhya()), RetornaCodigoCd(), item.get_dtNeg()).doubleValue() * item.getQuantidade()));
    sql.executeUpdate();
  }

  private BigDecimal RetornaUltimoCustoMedioComICMS(BigDecimal codProd, BigDecimal codEmp, Date dataCorte) throws Exception {
    JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
    NativeSql sql = new NativeSql(jdbc);
    sql.appendSql("SELECT CUS.CUSMEDICM");
    sql.appendSql(" FROM TGFCUS CUS ");
    sql.appendSql(" WHERE CUS.CODPROD = :CODPROD");
    sql.appendSql(" AND CUS.CODEMP = :CODEMP");
    sql.appendSql(" AND CUS.DTATUAL = (SELECT MAX(I.DTATUAL) FROM TGFCUS I WHERE I.CODPROD = CUS.CODPROD AND I.CODEMP = CUS.CODEMP AND I.DTATUAL <= :DATA)");
    sql.setNamedParameter("CODEMP", codEmp);
    sql.setNamedParameter("CODPROD", codProd);
    sql.setNamedParameter("DATA", dataCorte);
    ResultSet rs = sql.executeQuery();
    BigDecimal custo = BigDecimal.ZERO;
    while (rs.next())
      custo = rs.getBigDecimal("CUSMEDICM");
    rs.close();
    return custo;
  }

  private BigDecimal RetornaCodigoCd() throws Exception {
    JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
    NativeSql sql = new NativeSql(jdbc);
    sql.appendSql("SELECT CODEMP FROM TSIEMP WHERE AD_CD = 'S'");
    ResultSet rs = sql.executeQuery();
    BigDecimal codCd = null;
    while (rs.next())
      codCd = rs.getBigDecimal("CODEMP");
    rs.close();
    return codCd;
  }

  private void RecalcularImpostos(BigDecimal nuNota) throws Exception {
    ImpostosHelpper impostos = new ImpostosHelpper();
    impostos.calcularImpostos(nuNota);
  }

  private void atualizarParaProcessado(BigDecimal nunota) throws Exception {
    JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
    NativeSql sqlConexaoDocs = new NativeSql(jdbc);
    sqlConexaoDocs.appendSql("UPDATE TGFCAB SET AD_PROCESSADOAUTOMACAO = 'S' WHERE NUNOTA = :NUNOTA");
    sqlConexaoDocs.setNamedParameter("NUNOTA", nunota);
    sqlConexaoDocs.executeUpdate();
  }
}