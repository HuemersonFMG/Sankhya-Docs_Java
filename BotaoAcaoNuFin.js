private void insereFinanceiro(Registro registro) throws Exception {
    System.out.println(“[ID=” + id + “, Gerando Financeiro]”);
    JapeSession.SessionHandle hnd = null;
    try {
    hnd = JapeSession.open();
    hnd.execWithTX(new JapeSession.TXBlock() {
    @Override
    public void doWithTx() throws Exception {
    DynamicVO topVO = ComercialUtils.getTipoOperacao((BigDecimal) registro.getCampo(“CODTIPOPER”));
    BigDecimal numNota = (BigDecimal) registro.getCampo(“NUMNOTA”);
    String serieNota = (String) registro.getCampo(“SERIENOTA”);
    Timestamp dtVencInic = (Timestamp) registro.getCampo(“DTVENC”);
    Timestamp dtEntSai = (Timestamp) registro.getCampo(“DTENTSAI”);
    Timestamp dtNege = (Timestamp) registro.getCampo(“DTNEG”);
    
                    JapeWrapper financeiroDAO = JapeFactory.dao("Financeiro");
                    DynamicVO finVO = financeiroDAO.create()
                            .set("CODEMP", codEmp)
                            .set("NUMNOTA", numNota)
                            .set("SERIENOTA", serieNota)
                            .set("DTNEG", dtNege)
                            .set("DESDOBRAMENTO", "1")
                            .set("DHMOV", TimeUtils.getNow())
                            .set("DTVENCINIC", dtVencInic)
                            .set("DTVENC", dtVencInic)
                            .set("DTENTSAI", dtEntSai)
                            .set("CODPARC", codParc)
                            .set("CODTIPOPER", topVO.asBigDecimal("CODTIPOPER"))
                            .set("DHTIPOPER", topVO.asTimestamp("DHALTER"))
                            .set("CODNAT", codNat)
                            .set("CODCENCUS", codCencus)
                            .set("CODPROJ", codProj)
                            .set("AD_CENTROCUSTO", centroCusto)
                            .set("CODVEND", BigDecimal.ZERO)
                            .set("CODMOEDA", (BigDecimal) registro.getCampo("CODMOEDA"))
                            .set("VLRMOEDA", vlrMoeda)
                            .set("CODTIPTIT", BigDecimal.ZERO)
                            .set("DESDOBDUPL", "T")
                            .set("VLRDESDOB", BigDecimalUtil.getRounded(vlrDesdob.multiply(vlrMoeda), 2))
                            .set("VLRJURO", BigDecimal.ZERO)
                            .set("RECDESP", new BigDecimal(-1))
                            .set("PROVISAO", "S")
                            .set("ORIGEM", "F")
                            .set("NUMCONTRATO", BigDecimal.ZERO)
                            .set("DTALTER", TimeUtils.getNow())
                            .set("CODBCO", BigDecimal.ZERO)
                            .set("CODCTABCOINT", BigDecimal.ZERO)
                            .set("CODUSU", contexto.getUsuarioLogado())
                            .set("CHAVECTE", chaveCte)
                            .set("CHAVECTEREF", StringUtils.getNullAsEmpty(chaveCTeRef))
                            .set("CODCFO", codCFO)
                            .set("CODCIDINICTE", codCidOrig)
                            .set("CODCIDFIMCTE", codCidDest)
                            .set("AD_IDPROFDESP", id)
                            .set("AD_APROVADORGESTOR", (BigDecimal) registro.getCampo("APROVADORGESTOR"))
                            .set("AD_APROVADORGERENTE", (BigDecimal) registro.getCampo("APROVADORGERENTE"))
                            .set("AD_STATUSLANC", "P")
                            .set("CODTRIB", getCodTrib((String) registro.getCampo("CHAVECTE")))
                            .set("HISTORICO", (String) registro.getCampo("OBS"))
                            .save();
    
                    nufin = finVO.asBigDecimal("NUFIN");
    
                    inserirLiberacao(nufin, BigDecimalUtil.getRounded(vlrDesdob.multiply(vlrMoeda), 2), (BigDecimal) registro.getCampo("APROVADORGESTOR"), (String) registro.getCampo("OBS"));
                    
                    if ("C".equals(tipo)) {
                    updateCteTgfixn(chaveCte);
                    }
    
                    System.out.println("[ID=" + id + ", Financeiro Gerado]");
                }
            });
        } catch (Exception e) {
            throw e;
        } finally {
            JapeSession.close(hnd);
        }
    }
    
    private void updateCteTgfixn(String chaveCte)throws Exception{
        System.out.println("[ID=" + id + ", Caiu no meu UPDATE]");
        try {
            
        
            NativeSql sql = new NativeSql(jdbc);
            sql.resetSqlBuf();
            sql.cleanParameters();
    
            sql.setReuseStatements(true);
            sql.setNamedParameter("NUNICO", nufin);
            sql.setNamedParameter("CHAVECTE", chaveCte);
            sql.executeUpdate("UPDATE TGFIXN SET NUFIN = :NUNICO WHERE CHAVEACESSO = :CHAVECTE");
      
            
        } catch (Exception e) {
            throw e;
        } 
    }