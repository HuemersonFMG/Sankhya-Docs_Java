// Localiza o serviço de impressão de relatórios
var factory = javaStaticMethod(‘br.com.sankhya.modelcore.PlatformServiceFactory’, ‘getInstance’, null, null);

var reportService = factory.lookupService(’@core:report.service’);

// Atenção: trocar o ### pelo Nrº único do relatório real (‘Relatórios Formatados’)
reportService.set(‘nurfe’, ###);

reportService.set(‘printer.name’,’?’);

for (var i=0;i<linhas.length;i++) {
var linha = linhas[i];
// reportService.set(‘codEmp’, linha.getCampo(“CODEMP”));
// A empresa é importante para que o reteamento seja feito corretamente, caso exista.

// Caso o modelo do relatório esteja em um parâmetro do sistema
// var query = getQuery();
// query.nativeSelect("SELECT INTEIRO FROM TSIPAR WHERE CHAVE = 'CODRELRECONTA'");
// query.next();
// reportService.set('nurfe', query.getBigDecimal("INTEIRO"));

var params = {};
params['PK_NUNOTA'] = linha.getCampo("NUNOTA");

reportService.set('report.params', params);

reportService.execute();
}