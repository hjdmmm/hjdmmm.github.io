const getRadioValue = function (nodeList) {
  for (let node of nodeList) {
    if (node.checked) {
      return node.value;
    }
  }
  return null;
}

const setRadioValue = function (nodeList, expectedValue) {
  for (let node of nodeList) {
    if (node.value === expectedValue) {
      node.checked = true;
      return;
    }
  }
}

const getSelectValue = function (selectEle) {
  return selectEle.options[selectEle.selectedIndex].value;
}

const setSelectValue = function (selectEle, expectedValue) {
  for (let option of selectEle.options) {
    if (option.value === expectedValue) {
      option.selected = true;
      return;
    }
  }
}