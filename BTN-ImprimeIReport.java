/*
 botão de ação com Javascript para chamar um iReport. Onde, 
 criei uma tela AD para que o usuário cadastre a etiqueta apontando qual 
 o relatório formatado vai ser utilizado e posteriormente ele 
 informa no parceiro qual etiqueta irá ser utilizada.
*/

var factory = javaStaticMethod(‘br.com.sankhya.modelcore.PlatformServiceFactory’, ‘getInstance’, null, null);
var reportService = factory.lookupService(‘@core:report.service’);

var query = getQuery();
var registroSelecionado = linhas[0];

var params = {};
params[‘PK_NUNOTA’] = registroSelecionado.getCampo(“NUNOTA”);
params[‘PK_SEQPED’] = registroSelecionado.getCampo(“SEQPED”);

query.setParam(“NRONOTA”, registroSelecionado.getCampo(“NUNOTA”));

query.setParam(“SEQPED”, registroSelecionado.getCampo(“SEQPED”));

query.setParam(“CODPARC”, registroSelecionado.getCampo(“CODPARC”));

//Select buscando o relatorio amarrado ao parceiro
query.nativeSelect(“SELECT ESP.CODREL AS RELATORIO, CAB.NUNOTA AS NRODANOTA 
    FROM TGFPAR PAR INNER JOIN AD_ETQESPELHO ESP ON ESP.CODIGO = PAR.AD_CODIGOETQESP 
    INNER JOIN TGFCAB CAB ON CAB.CODPARC = PAR.CODPARC 
    INNER JOIN TGFITE ITE ON ITE.NUNOTA = CAB.NUNOTA 
    WHERE PAR.CODPARC IN {CODPARC} 
    AND CAB.NUNOTA IN {NRONOTA} 
    AND ITE.SEQUENCIA IN {SEQPED}”);

while (query.next()) {
var relatorio = query.getString(“relatorio”);

reportService.set(‘nurfe’, relatorio);
}
reportService.set(‘report.params’, params);
reportService.set(‘printer.name’,‘?’);

reportService.execute();