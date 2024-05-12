/*
Exemplo de script de banco com modulo jar.
Básico de como implementar um CRUD completo, a fim de compreender uma das maneiras de criar um CRUD.
No geral entenda como a função execQuery e consultarBySQL está funcionando, melhore e use.

https://ajuda.sankhya.com.br/hc/pt-br/articles/360045111093-Configurando-A%C3%A7%C3%B5es-Personalizadas
*/ 

public class botaoDeAcaoExemplo implements AcaoRotinaJava{
	@Override
	public void doAction(ContextoAcao ctx) throws Exception {

	execQuery("UPDATE TGFPAR SET ATIVO = 'N' WHERE CODPARC = 15851");
	execQuery("INSERT INTO TGFPAR (CODPARC, NOMEPARC, ATIVO) VALUES (0, 'TESTE', 'S')");
    
    BigDecimal numeroInstancia = (BigDecimal) ctx.getLinhas()[0].getCampo("NUINSTANCIA");


     ResultSet camposRS = consultarBySQL("SELECT * FROM TDDCAM CAM WHERE NOMETAB IN (SELECT NOMETAB FROM TDDINS INS WHERE INS.NUINSTANCIA = "+numeroInstancia+") ORDER BY ORDEM, NUCAMPO");

     execQuery("DELETE FROM AD_H1APPCAM WHERE SEQUENCIA = 1 AND NUINSTANCIA = "+numeroInstancia);
    	
     int contador = 1;
     while (camposRS.next()) {
        	execQuery("INSERT INTO AD_H1APPCAM (NUINSTANCIA, SEQUENCIA, NUCAMPO, TIPOAPRESENTACAO, TIPOEVENTO, ORDEM, VISIVELGRID) VALUES "
        			+ "("+numeroInstancia+",1,"+camposRS.getBigDecimal("NUCAMPO")+",1,1,"+contador+",'S')");
        	contador++;
	}
        

      ctx.setMensagemRetorno("Comando executado");
	}
	
	 public static void execQuery(String query) throws Exception {
	        JdbcWrapper jdbc = null;
	        EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	        jdbc = dwfEntityFacade.getJdbcWrapper();
	        jdbc.openSession();
	        NativeSql sql = null;
	        try {
	            sql = new NativeSql(jdbc);
	            sql.appendSql(query);
	            sql.executeUpdate();
	        } catch (Throwable throwable) {
	            NativeSql.releaseResources(sql);
	            throw throwable;
	        }
	        NativeSql.releaseResources((NativeSql) sql);
	      	NativeSql.releaseResources(sql);
	    }


	 public static ResultSet consultarBySQL(String query) throws Exception {
	        JdbcWrapper jdbc = null;
	        EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	        jdbc = dwfEntityFacade.getJdbcWrapper();
	        jdbc.openSession();
	        NativeSql sql = null;
	        try {
	            sql = new NativeSql(jdbc);
	            sql.appendSql(query);
	            ResultSet result = sql.executeQuery();
	            return result;
	        } catch (Throwable throwable) {
	            NativeSql.releaseResources(sql);
	            throw throwable;
	        }
	    }
}