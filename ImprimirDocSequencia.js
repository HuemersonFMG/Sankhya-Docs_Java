var factory = javaStaticMethod(‘br.com.sankhya.modelcore.PlatformServiceFactory’, ‘getInstance’, null, null);
var reportService = factory.lookupService(‘@core:report.service’);

// Relação de Entrega
reportService.set(‘nurfe’, 169);
reportService.set(‘printer.name’,‘?’);

for (var i=0;i<linhas.length;i++)
{
var linha = linhas[i];
var params = {};
params[‘PK_NUNOTA’] = linha.getCampo(“NUNOTA”);
params[‘ORDEMCARGA’] = linha.getCampo(“ORDEMCARGA”);
reportService.set(‘report.params’, params);
reportService.execute();
}

// MDF-e
reportService.set(‘nurfe’, 142);
reportService.set(‘printer.name’,‘?’);

for (var i=0;i<linhas.length;i++)
{
var linha = linhas[i];
var params = {};
params[‘NUVIAG’] = linha.getCampo(“AD_NUVIAG”);
params[‘SEQMDFE’] = linha.getCampo(“AD_SEQMDFE”);
reportService.set(‘report.params’, params);
reportService.execute();
}

// Espelho de NF
reportService.set(‘nurfe’, 170);
reportService.set(‘printer.name’,‘?’);

for (var i=0;i<linhas.length;i++)
{
var linha = linhas[i];
var params = {};
params[‘PK_NUNOTA’] = linha.getCampo(“NUNOTA”);
params[‘ORDEMCARGA’] = linha.getCampo(“ORDEMCARGA”);
reportService.set(‘report.params’, params);
reportService.execute();
}

// DANFE
reportService.set(‘nurfe’, 11);
reportService.set(‘printer.name’,‘?’);

for (var i=0;i<linhas.length;i++)
{
var linha = linhas[i];
var params = {};
params[‘NUNOTA’] = linha.getCampo(“NUNOTA”);
reportService.set(‘report.params’, params);
reportService.execute();
}