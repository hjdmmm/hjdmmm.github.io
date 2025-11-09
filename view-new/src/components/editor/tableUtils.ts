import type {Node} from '@tiptap/pm/model'
import {
    addColumn,
    addRow,
    columnIsHeader,
    removeColumn,
    removeRow,
    rowIsHeader,
    selectedRect,
    TableMap
} from '@tiptap/pm/tables';
import type {Editor} from "@tiptap/vue-3";

const getTableNode = (editor: Editor) => {
    const state = editor.state;
    const {from, to} = state.selection;
    const rangeNodes: Node[] = [];
    state.doc.nodesBetween(from, to, (node) => {
        if (node.isText || node.type.name !== 'table') {
            return;
        }
        rangeNodes.push(node);
    });
    return rangeNodes[0] ?? null;
}

export const isTableActive = (editor: Editor) => {
    return editor.isActive('table');
}

export const isRowHeader = (editor: Editor) => {
    const tableNode = getTableNode(editor);
    if (!tableNode) {
        return false;
    }
    const tableMap = TableMap.get(tableNode);
    return rowIsHeader(tableMap, tableNode, 0);
}

export const isColHeader = (editor: Editor) => {
    const tableNode = getTableNode(editor);
    if (!tableNode) {
        return false;
    }
    const tableMap = TableMap.get(tableNode);
    return columnIsHeader(tableMap, tableNode, 0);
}

export const getTableSize = (editor: Editor) => {
    const tableNode = getTableNode(editor);
    if (!tableNode) {
        return {row: 0, col: 0};
    }
    const tableMap = TableMap.get(tableNode);
    return {row: tableMap.height, col: tableMap.width};
}

export const resizeTable = (editor: Editor, newRows: number, newCols: number) => {
    const tableNode = getTableNode(editor);
    if (!tableNode) {
        return;
    }
    const tableMap = TableMap.get(tableNode);
    const currentRows = tableMap.height;
    const currentCols = tableMap.width;
    const rect = {
        ...selectedRect(editor.state),
        top: currentRows - 1,
        left: currentCols - 1,
        bottom: currentRows,
        right: currentCols,
    };
    {
        const tr = editor.state.tr;
        if (currentRows < newRows) {
            for (let i = 0; i < newRows - currentRows; i++) {
                addRow(tr, rect, rect.bottom);
            }
        } else if (currentRows > newRows) {
            for (let i = 0; i < currentRows - newRows; i++) {
                removeRow(tr, rect, rect.bottom - 1 - i);
            }
        }
        editor.view.dispatch(tr);
    }
    {
        const tr = editor.state.tr;
        const table = rect.tableStart ? tr.doc.nodeAt(rect.tableStart - 1) : tr.doc;
        if (!table) {
            return;
        }
        rect.table = table;
        rect.map = TableMap.get(rect.table);
        if (currentCols < newCols) {
            for (let i = 0; i < newCols - currentCols; i++) {
                addColumn(tr, rect, rect.right);
            }
        } else if (currentCols > newCols) {
            for (let i = 0; i < currentCols - newCols; i++) {
                removeColumn(tr, rect, rect.right - 1 - i);
                const table = rect.tableStart ? tr.doc.nodeAt(rect.tableStart - 1) : tr.doc;
                if (!table) {
                    return;
                }
                rect.table = table;
                rect.map = TableMap.get(rect.table);
            }
        }
        editor.view.dispatch(tr);
    }
    editor.chain().focus().run();
}
