for(var i = 0; i < linhas.length; i++){
    var linha = linhas[i];
    var NUNOTA = linha.getCampo("NUNOTA");
    var VLRTAXA= getParam("VLRTAXA");
    var query = getQuery();
      query.update("UPDATE TGFCAB SET AD_TXDIARIA = "+0+", AD_TXBOLETO = "+0+" WHERE NUNOTA IN "+NUNOTA+"");
	query.close();
}
mensagem = "Valor das Taxas alterados com sucesso!";