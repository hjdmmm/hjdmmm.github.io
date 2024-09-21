function getUrlParam(search, requiredKey) {
    const paramsStr = search.slice(1);
    const params = paramsStr.split('&');

    for (let param of params) {
        const splitStr = param.split('=');
        if (splitStr[0] === requiredKey) {
            return splitStr[1];
        }
    }
    return null;
}