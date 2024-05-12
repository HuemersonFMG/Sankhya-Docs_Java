public class executeQuery implements AcaoRotinaJava{
	@Override
	public void doAction(ContextoAcao ctx) throws Exception {
		String comando = (String) ctx.getParam("COMANDO");
		execQuery(comando);
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
}