const prefixUrl = "http://localhost:8989/"

const ajax = {
    getXMLHttpRequest: function (callback) {
        let xmlHttp = new XMLHttpRequest();
        xmlHttp.responseType = "json";
        xmlHttp.onreadystatechange = function () {
            if (xmlHttp.readyState === 4) {
                if (xmlHttp.status === 200) {
                    if (xmlHttp.response.code === 200) {
                        if (callback != null) {
                            callback(xmlHttp.response.data);
                        }
                    } else {
                        console.log(xmlHttp.response.code + " - " + xmlHttp.response.msg);
                        window.alert(xmlHttp.response.msg);
                        document.cookie = "token=; expires=" + date.toUTCString() + "; path=/";
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
        const dataStr = JSON.stringify(data);

        let url = prefixUrl + resourceUrl;

        const xmlHttp = this.getXMLHttpRequest(callback);
        xmlHttp.open("POST", url, true);
        xmlHttp.setRequestHeader('Content-Type', 'application/json');
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
