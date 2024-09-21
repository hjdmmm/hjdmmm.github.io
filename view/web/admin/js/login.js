const existToken = function () {
  const strings = document.cookie.split(';');
  for (let string of strings) {
    const pair = string.trim();
    if (pair.indexOf("token") === 0) {
      return pair.substring("token=".length, pair.length).length !== 0;
    }
  }

  return false;
}

const logout = function (loginRelativePath) {
  const date = new Date();
  date.setTime(date.getTime() - 1);
  ajax.post("system/logout", null, () => {
    document.cookie = "token=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";
    window.location.href = loginRelativePath;
  })
}