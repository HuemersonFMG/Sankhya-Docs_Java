const ConfigApiSnk = {
    host: “endereço”,
    port: “porta”,
    session: Cookies.get(“JSESSIONID”)
    }

var session = ConfigApiSnk.session.replace(“.master”, “”);

class ApiSnk {

    saveRecord(table, fields_values, return_fields, keys) {

    var body = JSON.stringify({
    "serviceName": "CRUDServiceProvider.saveRecord",
    "requestBody": {
        "dataSet": {
        "rootEntity": table,
        "includePresentationFields": "N",
        "dataRow": {
            "localFields": fields_values,
            "key": keys,
        },
        "entity": {
            "fieldset": {
            "list": return_fields
            }
        }
        }
    }
})

var request = {
    method: "POST",
    url: "http://" + ConfigApiSnk.host + ":" + ConfigApiSnk.port + "/mge/service.sbr?serviceName=CRUDServiceProvider.saveRecord&sessionId=" + session + "&outputType=json",
    headers: {
        "Content-Type": "application/json;charset=ISO-8859-1"
    },
    data: body
};

var result = axios(request).then((response) => {

    console.log(response);

    return response;

})
.catch((error) => {
    console.log(error);
});

return result
}

}