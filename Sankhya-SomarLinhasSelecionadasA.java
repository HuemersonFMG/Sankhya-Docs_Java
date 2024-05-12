@Override
    public void doAction(ContextoAcao contexto) throws Exception {

        QueryExecutor query = contexto.getQuery();

        // Parametros
        String pCodCtaBco = contexto.getParam("CODCTABCO").toString();
        String pCodBco = contexto.getParam("CODBCO").toString();
        String pCodTipOper = contexto.getParam("CODTIPOPER").toString();
        String pHistorico = contexto.getParam("HISTORICO").toString();
        String pCodNat = contexto.getParam("CODNAT").toString();
        String pCodCenCus = contexto.getParam("CODCENCUS").toString();
        String pProjeto = contexto.getParam("PROJETO").toString();
        String pCodemp = contexto.getParam("CODEMP").toString();
        Date pDtVenc = (Date) contexto.getParam("DTVENC");
        //String dtVenc = (String) Diversos.converterDataRegex(pDtVenc);

        Registro[] linhasSelecionadas = contexto.getLinhas();

        BigDecimal vlrTotal = BigDecimal.ZERO;

        for (Registro linha : linhasSelecionadas) {
            //Variaveis para pegar os valores da tela de Previsão
            BigDecimal pk = (BigDecimal) linha.getCampo("NUCONFCOM");
            BigDecimal codEmp = (BigDecimal) linha.getCampo("CODEMP");
            BigDecimal codVend = (BigDecimal) linha.getCampo("CODVEND");

            String qry = "";
            qry += " SELECT CODVEND, CODPARC FROM TGFVEN WHERE CODVEND = " + codVend;
            query.nativeSelect(qry);
            if (!query.next()) {
                contexto.setMensagemRetorno("Query Buscar Vendedor não retornou nada");
            }
            BigDecimal codParcVend = query.getBigDecimal("CODPARC");
            String tipCalc = (String) linha.getCampo("TIPOCALC");
            BigDecimal vlrPGCom = (BigDecimal) linha.getCampo("VLRPG");
            String comissaoPG = (String) linha.getCampo("COMISSAOPG");           

            if (comissaoPG.equals("N")){
                //Solicitamos a inclusão de uma linha no financeiro
                Registro financeiro = contexto.novaLinha("TGFFIN");

                //Informamos os campos desejados para incluir o financeiro
                financeiro.setCampo("VLRDESDOB", vlrPGCom);
                financeiro.setCampo("RECDESP", -1);
                financeiro.setCampo("CODEMP", pCodemp);
                financeiro.setCampo("NUMNOTA", 0);
                financeiro.setCampo("DTNEG", getDhAtual());
                financeiro.setCampo("CODPARC", codParcVend);
                financeiro.setCampo("CODNAT", pCodNat);
                financeiro.setCampo("CODBCO", pCodBco);
                financeiro.setCampo("CODCTABCOINT", pCodCtaBco);
                if (tipCalc.equals("C")) {
                    financeiro.setCampo("CODTIPTIT", 28); //Deposito em Conta Barrane
                } else {
                    financeiro.setCampo("CODTIPTIT", 29); //Deposito em Conta
                }
                financeiro.setCampo("CODCENCUS", pCodCenCus);
                financeiro.setCampo("CODTIPOPER", pCodTipOper);
                financeiro.setCampo("CODPROJ", pProjeto);
                financeiro.setCampo("DTVENC", pDtVenc);
                financeiro.setCampo("HISTORICO", pHistorico);

                financeiro.save();

                BigDecimal nufinTgfFin = (BigDecimal) financeiro.getCampo("NUFIN");

                qry = " SELECT ";
                qry += "NUNOTA, CODTIPTIT, CODEMP, NUFIN, CODCENCUS, CODNAT, DTNEG, DTVENC, HISTORICO, CODBCO, CODCTABCOINT FROM TGFFIN WHERE NUFIN = " + nufinTgfFin;
                query.nativeSelect(qry);
                if (!query.next()) {
                    contexto.setMensagemRetorno("Query Buscar Tipo de Titulo não retornou nada");
                }
                BigDecimal codTipTitFin = query.getBigDecimal("CODTIPTIT");
                BigDecimal codEmpFin = query.getBigDecimal("CODEMP");
                BigDecimal codcencusFin = query.getBigDecimal("CODCENCUS");
                BigDecimal codnatFin = query.getBigDecimal("CODNAT");
                Date dtnegFin = query.getDate("DTNEG");
                Date dtvencFin = query.getDate("DTVENC");
                Date historicoFin = query.getDate("HISTORICO");
                Date codbcoFin = query.getDate("CODBCO");
                Date codctabcointFin = query.getDate("CODCTABCOINT");

                linha.setCampo("NUFINCONF", nufinTgfFin);
                linha.setCampo("COMISSAOPG", 'S');
                linha.setCampo("CODTIPTIT", codTipTitFin);
                linha.setCampo("CODEMP", codEmpFin);
                linha.setCampo("CODCENCUS", codcencusFin);
                linha.setCampo("CODNAT", codnatFin);
                linha.setCampo("DTNEG", dtnegFin);
                linha.setCampo("DTVENC", dtvencFin);
                linha.setCampo("HISTORICO", historicoFin);
                linha.setCampo("CODBCO", codbcoFin);
                linha.setCampo("CODCTABCOINT", codctabcointFin);
                linha.save();

                query.close();
            } else {
                contexto.mostraErro("Já existe Financeiro Gerado.");
            }