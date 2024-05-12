package acoesPersonalizadas.GrupoLAM;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class ReplicarParcelas implements AcaoRotinaJava {
  public void doAction(ContextoAcao contextoAcao) throws Exception {
    contextoAcao.confirmar("Inserir Parcelas", "Deseja replicar as parcelas?", 1);
    Registro[] registro = contextoAcao.getLinhas();
    BigDecimal nuFin = (BigDecimal)registro[0].getCampo("NUFIN");
    BigDecimal valor = (BigDecimal)registro[0].getCampo("VLRDESDOB");
    int nroParcelas = ((Integer)contextoAcao.getParam("PARCELAS")).intValue();
    Date dataInicial = (Date)contextoAcao.getParam("DATAINICIAL");
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(dataInicial.getTime());
    for (int i = 0; i < nroParcelas; i++) {
      calendar.set(5, 1);
      Timestamp timestampAtualizado = new Timestamp(calendar.getTimeInMillis());
      InserirParcelas(nuFin, timestampAtualizado, new BigDecimal(valor.doubleValue() / nroParcelas));
      calendar.add(2, 1);
    }
    contextoAcao.setMensagemRetorno("Parcelas inseridas com sucesso!");
  }

  private void InserirParcelas(BigDecimal nuFin, Timestamp data, BigDecimal valor) throws Exception {
    JapeWrapper cpv = JapeFactory.dao("AD_PARCFIN");
    DynamicVO parcelaVO = ((FluidCreateVO)((FluidCreateVO)((FluidCreateVO)cpv.create().set("NUFIN", nuFin)).set("COMPETENCIA", data)).set("VLRDESDOB", valor)).save();
  }
}