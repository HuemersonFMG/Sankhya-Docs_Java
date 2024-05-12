package br.com.sankhya.bh.topcesta.buttons;

import br.com.lugh.bh.CentralNotasUtils;
import br.com.lugh.dsl.metadados.pojo.Pojo;
import br.com.sankhya.bh.genyx.dao.vo.ItemNotaVO;
import br.com.sankhya.bh.topcesta.dao.ItemNotaDAO;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv = {1, 4, 1}, bv = {1, 0, 3}, k = 1, d1 = {"\000\030\n\002\030\002\n\002\030\002\n\002\b\002\n\002\020\002\n\000\n\002\030\002\n\000\030\0002\0020\001B\005\006\002\020\002J\020\020\003\032\0020\0042\006\020\005\032\0020\006H\026\006\007"}, d2 = {"Lbr/com/sankhya/bh/topcesta/buttons/AjustaValorButton;", "Lbr/com/sankhya/extensions/actionbutton/AcaoRotinaJava;", "()V", "doAction", "", "contextoAcao", "Lbr/com/sankhya/extensions/actionbutton/ContextoAcao;", "topcesta"})
public final class AjustaValorButton implements AcaoRotinaJava {
  public void doAction(@NotNull ContextoAcao contextoAcao) {
    Intrinsics.checkNotNullParameter(contextoAcao, "contextoAcao");
    String mensagem = "";
    ItemNotaDAO itemNotaDAO = new ItemNotaDAO();
    Object tipo = contextoAcao.getParam("TIPO");
    String str1 = contextoAcao.getParam("VALOR").toString();
    null = 0;
    BigDecimal valor = new BigDecimal(str1);
    for (Registro linha : contextoAcao.getLinhas()) {
      String str2 = linha.getCampo("NUNOTA").toString();
      boolean bool1 = false;
      int nuNota = Integer.parseInt(str2);
      String str3 = linha.getCampo("SEQUENCIA").toString();
      boolean bool2 = false;
      int sequencia = Integer.parseInt(str3);
      Collection itensNotaVO = itemNotaDAO.findByNuNotaOrig(nuNota, sequencia);
      Collection collection1 = itensNotaVO;
      Object initial$iv = BigDecimal.ZERO;
      int $i$f$fold = 0;
      Object accumulator$iv = initial$iv;
      for (Object element$iv : collection1) {
        ItemNotaVO itemNotaVO = (ItemNotaVO)element$iv;
        Object object2 = accumulator$iv;
        int $i$a$-fold-AjustaValorButton$doAction$vlrUnitCesta$1 = 0;
        Intrinsics.checkNotNullExpressionValue(object2, "acc");
        Object object3 = object2;
        BigDecimal bigDecimal = itemNotaVO.getVlrTot();
        boolean bool = false;
        Intrinsics.checkNotNullExpressionValue(object3.add(bigDecimal), "this.add(other)");
        accumulator$iv = object3.add(bigDecimal);
      } 
      BigDecimal vlrUnitCesta = accumulator$iv.divide(new BigDecimal(linha.getCampo("QTDNEG").toString()), MathContext.DECIMAL128);
      BigDecimal indice = valor.divide(vlrUnitCesta, MathContext.DECIMAL128);
      mensagem = mensagem + "indice = " + indice + "<br>";
      Object object1 = tipo;
      accumulator$iv = new BigDecimal(linha.getCampo("VLRUNIT").toString());
      boolean bool3 = false;
      Intrinsics.checkNotNullExpressionValue(accumulator$iv.add(valor), "this.add(other)");
      accumulator$iv = new BigDecimal(linha.getCampo("VLRUNIT").toString());
      bool3 = false;
      Intrinsics.checkNotNullExpressionValue(accumulator$iv.subtract(valor), "this.subtract(other)");
      if (Intrinsics.areEqual(object1, "Definir")) {
      
      } else {
        throw new Exception("Opndefinida");
      } 
      BigDecimal vlrAjustado = Intrinsics.areEqual(object1, "Acrescentar") ? accumulator$iv.add(valor) : (Intrinsics.areEqual(object1, "Diminuir") ? accumulator$iv.subtract(valor) : (BigDecimal)"JD-Core does not support Kotlin");
      linha.setCampo("VLRUNIT", vlrAjustado);
      BigDecimal qtdKit = new BigDecimal(linha.getCampo("QTDNEG").toString());
      linha.setCampo("VLRTOT", vlrAjustado.multiply(qtdKit, MathContext.DECIMAL128));
      linha.save();
      int itemSize = itensNotaVO.size();
      BigDecimal valorAcumulado = BigDecimal.ZERO;
      byte b = 0;
      for (ItemNotaVO itemNotaVO : itensNotaVO) {
        BigDecimal qtdNeg = itemNotaVO.getQtdNeg();
        BigDecimal bigDecimal1 = valor;
        boolean bool5 = false;
        Intrinsics.checkNotNullExpressionValue(bigDecimal1.multiply(qtdKit), "this.multiply(other)");
        bigDecimal1 = bigDecimal1.multiply(qtdKit);
        Intrinsics.checkNotNullExpressionValue(valorAcumulado, "valorAcumulado");
        BigDecimal bigDecimal2 = valorAcumulado;
        boolean bool6 = false;
        Intrinsics.checkNotNullExpressionValue(bigDecimal1.subtract(bigDecimal2), "this.subtract(other)");
        BigDecimal vlrDescCom = (b < itemSize - 1) ? itemNotaVO.getVlrUnit().multiply(indice).setScale(2, RoundingMode.HALF_UP) : bigDecimal1.subtract(bigDecimal2).divide(qtdNeg, MathContext.DECIMAL128);
        if (Intrinsics.areEqual(tipo, "Acrescentar")) {
          BigDecimal valorCalc = vlrDescCom.add(itemNotaVO.getVlrUnit());
          itemNotaVO.getVo().setProperty("VLRUNIT", valorCalc);
          itemNotaVO.getVo().setProperty("VLRTOT", valorCalc.multiply(itemNotaVO.getVo().asBigDecimalOrZero("QTDNEG"), MathContext.DECIMAL128));
        } 
        if (Intrinsics.areEqual(tipo, "Diminuir")) {
          BigDecimal valorCalc = itemNotaVO.getVlrUnit().subtract(vlrDescCom);
          itemNotaVO.getVo().setProperty("VLRUNIT", valorCalc);
          itemNotaVO.getVo().setProperty("VLRTOT", valorCalc.multiply(itemNotaVO.getVo().asBigDecimalOrZero("QTDNEG"), MathContext.DECIMAL128));
        } 
        if (Intrinsics.areEqual(tipo, "Definir")) {
          itemNotaVO.getVo().setProperty("VLRUNIT", vlrDescCom);
          itemNotaVO.getVo().setProperty("VLRTOT", vlrDescCom.multiply(itemNotaVO.getVo().asBigDecimalOrZero("QTDNEG"), MathContext.DECIMAL128));
        } 
        bigDecimal1 = valorAcumulado;
        Intrinsics.checkNotNullExpressionValue(vlrDescCom.multiply(itemNotaVO.getVo().asBigDecimalOrZero("QTDNEG"), MathContext.DECIMAL128), "vlrDescCom.multiply(\n   \n                )");
        bigDecimal2 = vlrDescCom.multiply(itemNotaVO.getVo().asBigDecimalOrZero("QTDNEG"), MathContext.DECIMAL128);
        bool6 = false;
        Intrinsics.checkNotNullExpressionValue(bigDecimal1.add(bigDecimal2), "this.add(other)");
        valorAcumulado = bigDecimal1.add(bigDecimal2);
        mensagem = mensagem + "<br>vlrDescCom=" + vlrDescCom + ",valorAcumulado=" + valorAcumulado + ",valor=" + valor + ",itemNotaVO.vlrTot=" + itemNotaVO.getVlrTot() + ",itemSize=" + itemSize + ",i=" + b;
        itemNotaDAO.save((Pojo)itemNotaVO);
        b++;
      } 
      int i = nuNota;
      boolean bool4 = false;
      Intrinsics.checkNotNullExpressionValue(BigDecimal.valueOf(i), "BigDecimal.valueOf(this.toLong())");
      CentralNotasUtils.Companion.totalizarNota(BigDecimal.valueOf(i));
      i = nuNota;
      bool4 = false;
      Intrinsics.checkNotNullExpressionValue(BigDecimal.valueOf(i), "BigDecimal.valueOf(this.toLong())");
      CentralNotasUtils.Companion.recalcularImpostos(BigDecimal.valueOf(i));
      i = nuNota;
      bool4 = false;
      Intrinsics.checkNotNullExpressionValue(BigDecimal.valueOf(i), "BigDecimal.valueOf(this.toLong())");
      CentralNotasUtils.Companion.refazerFinanceiro(BigDecimal.valueOf(i));
    } 
    contextoAcao.setMensagemRetorno("O Valor da Cesta foi definido com sucesso!!");
  }
}