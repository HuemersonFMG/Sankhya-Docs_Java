package br.com.souzaroxo.edi;

import java.math.BigDecimal;
import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;
import br.com.sankhya.modelcore.PlatformService;
import br.com.sankhya.modelcore.PlatformServiceFactory;

public class ScheduleGeraEDI implements ScheduledAction {
	public void onTime(ScheduledActionContext contexto) {
		try {
			PlatformService ps = PlatformServiceFactory.getInstance().lookupService("@core:edi.comercial.service");

			try {
				ps.set("codLayout", new BigDecimal(27000000));
				ps.set("parametros", null);
				ps.set("caminhoRepositorio", "edi_gerados");
				ps.set("emails", "");
				ps.set("caminhoFTP", "/");
				ps.set("enderecoFTP", "ftp://integracoes.accera.com.br");
				ps.set("usuarioFTP", "dsi_souzaroxosc_dist");
				ps.set("senhaFTP", "suasenha");
				ps.execute();
				System.out.println("Acao Agendada - Executando EDI 270000");
			} catch (Exception e) {
				RuntimeException re = new RuntimeException(e);
				System.out.println("Erro ao Executar Ação Agendada - Executando EDI 270000");
				throw re;
			}


		} catch (Exception e) {
			RuntimeException re = new RuntimeException(e);
			throw re;
		}
	}
}