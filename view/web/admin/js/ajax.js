const prefixUrl = "http://localhost:8989/"

const ajax = {
    getXMLHttpRequest: function (onSuccess, onFailed) {
        let xmlHttp = new XMLHttpRequest();
        xmlHttp.withCredentials = true;
        xmlHttp.responseType = "json";
        xmlHttp.onreadystatechange = function () {
            if (xmlHttp.readyState === 4) {
                if (xmlHttp.status === 200) {
                    if (xmlHttp.response.code === 200) {
                        if (onSuccess != null) {
                            onSuccess(xmlHttp.response.data);
                        }
                    } else if (xmlHttp.response.code === 405) {
                        if (onFailed != null) {
                            onFailed();
                        } else {
                            document.cookie = "token=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";
                            window.location.reload();
                        }
                    } else {
                        if (onFailed != null) {
                            onFailed();
                        } else {
                            console.log(xmlHttp.response.code + " - " + xmlHttp.response.msg);
                            window.alert(xmlHttp.response.msg);
                        }
                    }
                } else {
                    if (onFailed != null) {
                        onFailed();
                    } else {
                        console.log('http code = ' + xmlHttp.status + ' - ' + xmlHttp.statusText);
                        window.alert("网络异常，请稍后再试");
                    }
                }
            }
        }
        return xmlHttp;
    },
    get: function (resourceUrl, data, onSuccess, onFailed) {
        let query = [];
        for (let key in data) {
            query.push(encodeURIComponent(key) + '=' + encodeURIComponent(data[key]));
        }
        let queryUrl = query.length === 0 ? '' : ('?' + query.join('&'));
        let url = prefixUrl + resourceUrl + queryUrl;

        const xmlHttp = this.getXMLHttpRequest(onSuccess, onFailed);
        xmlHttp.open("GET", url, true);
        xmlHttp.send();
    },
    post: function (resourceUrl, data, onSuccess, onFailed) {
        let url = prefixUrl + resourceUrl;

        const xmlHttp = this.getXMLHttpRequest(onSuccess, onFailed);
        xmlHttp.open("POST", url, true);
        if (data instanceof FormData) {
            xmlHttp.send(data);
            return;
        }
        xmlHttp.setRequestHeader('Content-Type', 'application/json');
        const dataStr = JSON.stringify(data);
        xmlHttp.send(dataStr);
    },
    put: function (resourceUrl, data, onSuccess, onFailed) {
        const dataStr = JSON.stringify(data);

        let url = prefixUrl + resourceUrl;

        const xmlHttp = this.getXMLHttpRequest(onSuccess, onFailed);
        xmlHttp.open("PUT", url, true);
        xmlHttp.setRequestHeader('Content-Type', 'application/json');
        xmlHttp.send(dataStr);
    },
    delete: function (resourceUrl, data, onSuccess, onFailed) {
        const dataStr = JSON.stringify(data);

        let url = prefixUrl + resourceUrl;

        const xmlHttp = this.getXMLHttpRequest(onSuccess, onFailed);
        xmlHttp.open("DELETE", url, true);
        xmlHttp.setRequestHeader('Content-Type', 'application/json');
        xmlHttp.send(dataStr);
    },
};
