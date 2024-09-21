function Pager(pageSize, url, callback) {
  this.pageNum = 1;
  this.maxPageNum = 1;
  this.pageSize = pageSize;
  this.url = url;
  this.callback = callback;
  this.argMap = new Map();

  this.changePageNum = function (newPageNum, argMap) {
    this.pageNum = newPageNum;
    let queryParam = {
      'pageNum': this.pageNum,
      'pageSize': this.pageSize,
    };

    if (argMap) {
      this.argMap = argMap;
    }

    for (let [key, value] of this.argMap) {
      if (value != null) {
        queryParam[key] = value;
      }
    }
    ajax.get(this.url, queryParam, (page) => {
      this.maxPageNum = Math.ceil(page.total / this.pageSize);
      this.callback(page)
    });
  }

  this.toPreviousPage = function () {
    if (this.pageNum <= 1) {
      window.alert("当前已经是第一页");
      return;
    }
    this.changePageNum(this.pageNum - 1);
  }

  this.toSelectedPageNum = function (selectedPageNum) {
    if (selectedPageNum < 1 || selectedPageNum > this.maxPageNum) {
      window.alert("页码不正确");
      return;
    }
    this.changePageNum(selectedPageNum);
  }

  this.toNextPage = function () {
    if (this.pageNum >= this.maxPageNum) {
      window.alert("当前已经是最后一页");
      return;
    }
    this.changePageNum(this.pageNum + 1);
  }
}