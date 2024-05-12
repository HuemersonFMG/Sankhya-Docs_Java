var URL = java.net.URL;
var HttpURLConnection = java.net.HttpURLConnection;
var InputStreamReader = java.io.InputStreamReader;
var BufferedReader = java.io.BufferedReader;
var query = getQuery();

    var registroSelecionado = linhas[0];

    query.setParam("CODPARC", registroSelecionado.getCampo("CODPARC"));

    //Personalisar consulta para aprimorar o serviço da API
    query.nativeSelect("SELECT CID.NOMECID || '- ' || ENDR.NOMEEND || ' - ' || PAR.NUMEND AS endereco " +
                        "FROM tgfpar PAR " +
                        "INNER JOIN tsiend ENDR ON ENDR.CODEND = PAR.CODEND " +
                        "INNER JOIN TSICID CID ON CID.CODCID = PAR.CODCID " +
                        "WHERE PAR.CODPARC = {CODPARC}");

    while (query.next()) {

        var endereco = query.getString("endereco");

        //Formato aceito na URL
        var location = encodeURIComponent(endereco);

        var apiKey = "SUA_KEY";

        var apiUrl = "https://www.mapquestapi.com/geocoding/v1/address?key=" + apiKey + "&location=" + location;

        var url = new URL(apiUrl);

        var conexão = url.openConnection();

        conexão.setRequestMethod("GET");

        var codigoDeResposta = conexão.getResponseCode();

        if (codigoDeResposta === 200) {
            var leitor = new BufferedReader(new InputStreamReader(conexão.getInputStream()));
            var linha;
            var resposta = "";

            while ((linha = leitor.readLine()) !== null) {
                resposta += linha;
            }

            leitor.close();

            var respostaJSON = JSON.parse(resposta);

            //Existem mais chaves dentro da resposta JSON
            if (
                respostaJSON.results &&
                respostaJSON.results.length > 0 &&
                respostaJSON.results[0].locations &&
                respostaJSON.results[0].locations.length > 0 &&
                respostaJSON.results[0].locations[0].latLng
            ) {
                var lat = respostaJSON.results[0].locations[0].latLng.lat;
                var lng = respostaJSON.results[0].locations[0].latLng.lng;


                if (confirmarSimNao("Coordenadas encontradas", "Foram encontradas essas coordenadas para o endereço cadastrado (" + lat + " , " + lng + "). Deseja inseri-las?", 1)) {
                    mensagem = "Dados de Latitude: " + lat + ", Longitude: " + lng + " atualizados!";

                    var updateQuery = "UPDATE TGFPAR SET LATITUDE = " + lat + ", LONGITUDE = " + lng + " WHERE CODPARC = {CODPARC}";
            
                    query.update(updateQuery);
                } else {
                    mensagem = "Registro não atualizado";
                }           

            } else {
                mensagem = "Coordenadas não encontradas na resposta JSON.";
            }
        } else {
            mensagem = "A chamada à API retornou um código de resposta diferente de 200: " + codigoDeResposta ;
        }
    }
query.close();