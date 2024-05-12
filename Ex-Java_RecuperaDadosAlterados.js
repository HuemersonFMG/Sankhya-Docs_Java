package br.com.empresa.rh.listeners;


import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class Exemplo implements EventoProgramavelJava 
{

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {}

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeInsert(PersistenceEvent arg0) throws Exception {}

	@Override
	public void beforeUpdate(PersistenceEvent pev) throws Exception 
	{
		if (pev.getModifingFields().isModifing("CODEMP"))
		{
			DynamicVO newVO = (DynamicVO)pev.getVo();
			DynamicVO oldVO = (DynamicVO)pev.getOldVO();
			// se quiser fazer alguma analise no que mudou use as duas variaveis acima
			throw new Exception(String.format("Pensa que eu não vi que tu mudou o codigo da empresa de %s para %s", 
					oldVO.asBigDecimal("CODEMP").toString(),newVO.asBigDecimal("CODEMP").toString()));
			 // se nao disparar uma exceção descomente a linha abaixo para evitar que outros eventos nao vejam a mudança 
			 //pev.getModifingFields().setReavaliate(true);
		}
	}
}