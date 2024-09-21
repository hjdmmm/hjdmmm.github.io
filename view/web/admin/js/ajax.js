const prefixUrl = "http://localhost:8989/"

const ajax = {
    getXMLHttpRequest: function (callback) {
        let xmlHttp = new XMLHttpRequest();
        xmlHttp.withCredentials = true;
        xmlHttp.responseType = "json";
        xmlHttp.onreadystatechange = function () {
            if (xmlHttp.readyState === 4) {
                if (xmlHttp.status === 200) {
                    if (xmlHttp.response.code === 200) {
                        if (callback != null) {
                            callback(xmlHttp.response.data);
                        }
                    } else if (xmlHttp.response.code === 405) {
                        document.cookie = "token=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";
                        window.location.reload();
                    } else {
                        console.log(xmlHttp.response.code + " - " + xmlHttp.response.msg);
                        window.alert(xmlHttp.response.msg);
                    }
                } else {
                    console.log('http code = ' + xmlHttp.status + ' - ' + xmlHttp.statusText);
                    window.alert("网络异常，请稍后再试");
                }
            }
        }
        return xmlHttp;
    },
    get: function (resourceUrl, data, callback) {
        let query = [];
        for (let key in data) {
            query.push(encodeURIComponent(key) + '=' + encodeURIComponent(data[key]));
        }
        let queryUrl = query.length === 0 ? '' : ('?' + query.join('&'));
        let url = prefixUrl + resourceUrl + queryUrl;

        const xmlHttp = this.getXMLHttpRequest(callback);
        xmlHttp.open("GET", url, true);
        xmlHttp.send();
    },
    post: function (resourceUrl, data, callback) {
        let url = prefixUrl + resourceUrl;

        const xmlHttp = this.getXMLHttpRequest(callback);
        xmlHttp.open("POST", url, true);
        if (data instanceof FormData) {
            xmlHttp.send(data);
            return;
        }
        xmlHttp.setRequestHeader('Content-Type', 'application/json');
        const dataStr = JSON.stringify(data);
        xmlHttp.send(dataStr);
    },
    put: function (resourceUrl, data, callback) {
        const dataStr = JSON.stringify(data);

        let url = prefixUrl + resourceUrl;

        const xmlHttp = this.getXMLHttpRequest(callback);
        xmlHttp.open("PUT", url, true);
        xmlHttp.setRequestHeader('Content-Type', 'application/json');
        xmlHttp.send(dataStr);
    },
    delete: function (resourceUrl, data, callback) {
        const dataStr = JSON.stringify(data);

        let url = prefixUrl + resourceUrl;

        const xmlHttp = this.getXMLHttpRequest(callback);
        xmlHttp.open("DELETE", url, true);
        xmlHttp.setRequestHeader('Content-Type', 'application/json');
        xmlHttp.send(dataStr);
    },
};
