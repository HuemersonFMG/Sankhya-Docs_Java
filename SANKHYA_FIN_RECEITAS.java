package src.net.vtecno.vanity.requsicao.actionbuttons;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.core.JapeSession.TXBlock;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.AtributosRegras;
import br.com.sankhya.modelcore.comercial.BarramentoRegra;
import br.com.sankhya.modelcore.comercial.CentralFaturamento;
import br.com.sankhya.modelcore.comercial.ComercialUtils;
import br.com.sankhya.modelcore.comercial.ConfirmacaoNotaHelper;
import br.com.sankhya.modelcore.comercial.LiberacaoSolicitada;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.ListenerParameters;
import br.com.sankhya.modelcore.util.LockTableUtils;
import org.cuckoo.core.JobMetadata;
import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;
import com.sankhya.util.CollectionUtils;
import com.sankhya.util.JdbcUtils;
import com.sankhya.util.TimeUtils;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class Geradocumento implements ScheduledAction {
		JdbcWrapper jdbc = null;
		JobMetadata jbm;
		BigDecimal numNotaAtual = null;
		String numNotasGeradas = "";
		SessionHandle hnd = null;
		
		
		final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		
	@Override
	public void onTime(ScheduledActionContext contexto) {
		try {
			hnd = JapeSession.getCurrentSession().getTopMostHandle();
			jdbc = dwfFacade.getJdbcWrapper();
			jdbc.openSession();
			setupContext();	
			
				hnd.execWithTX(new TXBlock() {
					@Override
					public void doWithTx() throws Exception {

						NativeSql sqlCab = new NativeSql(jdbc);
						sqlCab.appendSql(" select * " 
						               + " from V_NOTA_INTEGRADO_VTEC "
									   + " order by  CAB_NUMREG DESC ");
						// itera sobre o select
						
						ResultSet rsetCab = sqlCab.executeQuery();
						
						while (rsetCab.next()) {
							
							BigDecimal nunotamodelo = null;
							BigDecimal CabNumReg = null;
							//define Modelo de Nota
							nunotamodelo = rsetCab.getBigDecimal("MODELONOTA");
							CabNumReg = rsetCab.getBigDecimal("CAB_NUMREG");
							LockTableUtils.lockNotaBD(CabNumReg);
							DynamicVO nunotaModeloVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO(DynamicEntityNames.CABECALHO_NOTA, nunotamodelo);
							DynamicVO cabVO = nunotaModeloVO.buildClone();
							cabVO.clearReferences();
							try {
								cabVO.setProperty("NUNOTA", null);
	
								cabVO.setProperty("CODPARC", nunotaModeloVO.asBigDecimal("CODPARC"));
								//gravando NUMPED
				                cabVO.setProperty("AD_NUMPED", rsetCab.getBigDecimal("NUMPED"));
	
								DynamicVO tpvVO = ComercialUtils.getTipoNegociacao(nunotaModeloVO.asBigDecimal("CODTIPVENDA"));
								cabVO.setProperty("CODTIPVENDA", tpvVO.asBigDecimal("CODTIPVENDA"));
								cabVO.setProperty("DHTIPVENDA", tpvVO.asTimestamp("DHALTER"));
	
								DynamicVO topVO = ComercialUtils.getTipoOperacao(nunotaModeloVO.asBigDecimal("CODTIPOPER"));
								cabVO.setProperty("CODTIPOPER", topVO.asBigDecimal("CODTIPOPER"));
								cabVO.setProperty("DHTIPOPER", topVO.asTimestamp("DHALTER"));
								cabVO.setProperty("TIPMOV", topVO.asString("TIPMOV"));
	
								cabVO.setProperty("OBSERVACAO", "Pedido de Venda");
								cabVO.setProperty("DTNEG", TimeUtils.getNow());
								cabVO.setProperty("DTMOV", TimeUtils.getNow());
	
								DocumentoHelper helper = new DocumentoHelper();
								BarramentoRegra bregra = helper.incluirCabecalho(cabVO);
								DynamicVO newcabVO = bregra.getState().getNewVO();
	
								// criacao de collection para enviar os itens uma unica vez.
								Collection<PrePersistEntityState> itensNota = new ArrayList<PrePersistEntityState>();
	
								// gerando itens através de select
								NativeSql sql = new NativeSql(jdbc);
	
								sql.appendSql(" SELECT CAB_NUMREG, CODPROD, VLRUNIT, QTDNEG, CODVOL, CONTROLE, CODLOCAL \n" 
								            + " FROM V_NOTA_INTEGRADO_VTEC " 
										    + " WHERE CAB_NUMREG = :P_CABNUMREG ");
	
								sql.setNamedParameter("P_CABNUMREG", CabNumReg);
	
								ResultSet rset = sql.executeQuery();
								// itera sobre o select
								while (rset.next()) {
	
									DynamicVO itemVO = (DynamicVO) dwfFacade.getDefaultValueObjectInstance("ItemNota");
									itemVO.setProperty("CODPROD", rset.getBigDecimal("CODPROD"));
									itemVO.setProperty("VLRUNIT", rset.getBigDecimal("VLRUNIT"));
									itemVO.setProperty("QTDNEG", rset.getBigDecimal("QTDNEG"));
									itemVO.setProperty("CODVOL", rset.getString("CODVOL"));
									itemVO.setProperty("CONTROLE", rset.getString("CONTROLE"));
									itemVO.setProperty("CODLOCALORIG", rset.getBigDecimal("CODLOCAL"));
	
									PrePersistEntityState itePreState = PrePersistEntityState.build(dwfFacade, "ItemNota", itemVO);
									itePreState.getNewVO();
									itensNota.add(itePreState);
									sql.setNamedParameter("NUNOTA", newcabVO.asBigDecimal("NUNOTA"));
	
								}
	
								//insere num nota na ad_tcabvtec
				                sql.setNamedParameter("P_NUMREG", CabNumReg);
				                sql.executeUpdate("UPDATE AD_TCABVTEC SET NUNOTAP = :NUNOTA WHERE NUMREG = :P_NUMREG ");
	
								//atualiza status da ad_tcabvtec
								String status = "T";
								sql.setNamedParameter("STATUS", status);
								sql.setNamedParameter("P_NUMREGG", CabNumReg);
								String TIPO = "P";
				                sql.setNamedParameter("TIPO", TIPO);
				                //ATUALIZANDO OPCAO
				                sql.executeUpdate("UPDATE AD_TCABVTEC SET OPCAO = :TIPO WHERE NUMREG = :P_NUMREGG ");
				                //ATUALIZANDO STATUS
								sql.executeUpdate("UPDATE AD_TCABVTEC SET STATUS = :STATUS WHERE NUMREG = :P_NUMREGG ");
	
								sql.resetSqlBuf();
	
								if (CollectionUtils.isEmpty(itensNota)) {
									throw new Exception("nao foram localizados dados no select");
								}
	
								// chama o helper para incluir todos os itens do select.
								helper.incluirAlterarItem(newcabVO, itensNota);
	
								// Processa Confirmação
								BarramentoRegra bRegrasCab = BarramentoRegra.build(CentralFaturamento.class, "regrasConfirmacaoSilenciosa.xml", AuthenticationInfo.getCurrent());
								try {
									JapeSession.putProperty("ignorar.liberacao.alcada", Boolean.FALSE);
									bRegrasCab.setValidarSilencioso(true);
									ConfirmacaoNotaHelper.confirmarNota(newcabVO.asBigDecimal("NUNOTA"), bRegrasCab);
	
									StringBuffer sb = new StringBuffer();
	
									Collection<LiberacaoSolicitada> collLibs = bRegrasCab.getLiberacoesSolicitadas();
	
									if (CollectionUtils.isNotEmpty(collLibs)) {
										sb.append("<b>Lista de Liberações Pendentes</b>");
										sb.append("\n");
	
									}
	
									for (LiberacaoSolicitada liberacaoSolicitada : collLibs) {
										sb.append("Aguardando liberação do evento ");
										sb.append(liberacaoSolicitada.getEvento());
										sb.append(".");
										sb.append("\n");
									}
	
								} finally {
									JapeSession.putProperty("ignorar.liberacao.alcada", Boolean.FALSE);
									NativeSql.releaseResources(sql);
									JdbcUtils.closeResultSet(rset);
								}
	
								//ANOTANDO NUMEROS DE NOTA
								numNotaAtual = newcabVO.asBigDecimal("NUNOTA");
								if (numNotaAtual != null) {
									// Converte numNota para String e concatena em numNotas com um espaço
									numNotasGeradas += numNotaAtual.toString() + " ";
								}
								contexto.log(numNotasGeradas.trim());
								
								
							}catch(Exception e) {
								e.printStackTrace();
							}
							Thread.sleep(1000);
					   }
					}
				});
				

		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
		}
	}
	
	public void log(String msg) {
		System.out.println("[job: " + this.jbm.getDescription() + "] " + msg);
	}
	
	private void setupContext() {
		AuthenticationInfo auth = AuthenticationInfo.getCurrent();
		JapeSessionContext.putProperty("usuario_logado", auth.getUserID());
		JapeSessionContext.putProperty("authInfo", auth);
		JapeSessionContext.putProperty("br.com.sankhya.com.CentralCompraVenda", Boolean.TRUE);
		JapeSessionContext.putProperty(ListenerParameters.CENTRAIS, Boolean.TRUE);
		JapeSession.putProperty(AtributosRegras.INC_UPD_ITEM_CENTRAL, Boolean.TRUE);
	}
}
