import {
    type ArticleDetail,
    type ArticleNode,
    createArticle,
    createTag,
    deleteArticle,
    getArticleDetail,
    getArticleNodes,
    getHotArticleIds,
    getLatestArticleIds,
    increaseArticleViewCount,
    listCategoryArticleIds,
    listTags,
    type Tag,
    updateArticleContent,
    updateArticleParent,
    updateArticleTags,
    updateArticleTitle
} from '@/api/article.ts'
import {getSuccessData} from "@/lib/utils.ts";
import {user} from "@/store/userStore.ts";
import {ref, watch} from 'vue'

export const articleNodeMap = ref<Map<string, ArticleNode>>(new Map())

export const categoryArticleTree = ref<string[]>([]);
export const hotArticleTree = ref<string[]>([]);
export const latestArticleTree = ref<string[]>([]);

export const selectedArticleId = ref<string | null>(null)

export const selectedArticleDetail = ref<ArticleDetail | null>(null);
export const initArticleContent = ref<string>('');
export const contentChanged = ref(false);
export const titleChanged = ref(false);
export const loadingDetail = ref(false);
export const savingTitle = ref(false);
export const savingContent = ref(false);

export const categoryPageSize = 10;
export const categoryPageNum = ref(1)
export const categoryHasMore = ref(true)
export const loadingMoreArticle = ref(false)

export const availableTags = ref<Tag[]>([]);
export const tagsPageNum = ref(1);
export const tagsPageSize = 20;
export const tagsHasMore = ref(true);
export const loadingMoreTags = ref(false);

export const tagsChanged = ref(false);
export const savingTags = ref(false);

let saveInterval: number | null = null;

export const createNewArticle = async (draft: boolean, title: string, parentId: string | null) => {
    const newArticleId = getSuccessData(await createArticle(draft, title, parentId));
    const newArticleNodes = getSuccessData(await getArticleNodes([newArticleId]));
    if (newArticleNodes.length > 0) {
        const articleNode = newArticleNodes[0]!;
        articleNodeMap.value.set(newArticleId, articleNode);
    }
    if (!parentId) {
        await initAllArticles();
    }
}

export const removeArticle = async (articleId: string) => {
    getSuccessData(await deleteArticle(articleId));
    const node = articleNodeMap.value.get(articleId);
    if (node?.parentId) {
        const parentNode = articleNodeMap.value.get(node.parentId);
        if (parentNode) {
            parentNode.children = parentNode.children.filter(id => id !== articleId);
        }
    }
    if (selectedArticleId.value === articleId) {
        selectedArticleId.value = null;
    }
    articleNodeMap.value.delete(articleId);
    if (node && node.parentId === null) {
        await initAllArticles();
    }
}

export const loadCategoryArticles = async () => {
    if (!categoryHasMore.value || loadingMoreArticle.value) {
        return;
    }
    loadingMoreArticle.value = true;
    try {
        const page = getSuccessData(await listCategoryArticleIds(categoryPageNum.value, categoryPageSize));
        const newCategoryArticleIds = page.rows;
        if (newCategoryArticleIds.length > 0) {
            const newArticleNodes = getSuccessData(await getArticleNodes(newCategoryArticleIds));
            newArticleNodes.forEach(node => {
                articleNodeMap.value.set(node.id, node)
            })
            categoryArticleTree.value = [...categoryArticleTree.value, ...newCategoryArticleIds];
        }
        categoryHasMore.value = newCategoryArticleIds.length === categoryPageSize && categoryArticleTree.value.length < parseInt(page.total);
        categoryPageNum.value++;
    } finally {
        loadingMoreArticle.value = false;
    }
};

export const reloadChildNodes = async (childIds: string[]) => {
    if (childIds.length === 0) {
        return;
    }
    const articleNodes = getSuccessData(await getArticleNodes(childIds));
    articleNodes.forEach(childNode => {
        articleNodeMap.value.set(childNode.id, childNode);
    });
}

export const updateSelectedArticleId = async (newSelectedArticleId: string | null) => {
    selectedArticleId.value = newSelectedArticleId;
    await loadArticleDetail(newSelectedArticleId);
    if (newSelectedArticleId) {
        getSuccessData(await increaseArticleViewCount(newSelectedArticleId));
    }
}

export const updateTitle = (newTitle: string) => {
    if (!selectedArticleDetail.value) {
        return;
    }
    titleChanged.value = true;
    selectedArticleDetail.value.title = newTitle;
    if (selectedArticleId.value) {
        const currentNode = articleNodeMap.value.get(selectedArticleId.value);
        if (currentNode) {
            currentNode.title = newTitle;
        }
    }
};

export const updateContent = (newContent: string) => {
    if (!selectedArticleDetail.value) {
        return;
    }
    contentChanged.value = true;
    selectedArticleDetail.value.content = newContent;
};

export const initAllArticles = async () => {
    categoryArticleTree.value = []
    categoryPageNum.value = 1
    categoryHasMore.value = false
    loadingMoreArticle.value = true
    try {
        const hotArticleIds = getSuccessData(await getHotArticleIds());
        const latestArticleIds = getSuccessData(await getLatestArticleIds());
        const page = getSuccessData(await listCategoryArticleIds(categoryPageNum.value, categoryPageSize));
        const categoryArticleIds = page.rows;
        const articleIds = [...new Set([...hotArticleIds, ...latestArticleIds, ...categoryArticleIds])];
        if (articleIds.length > 0) {
            const articleNodes = getSuccessData(await getArticleNodes(articleIds));
            articleNodes.forEach(node => {
                articleNodeMap.value.set(node.id, node);
            });
        }
        hotArticleTree.value = hotArticleIds
        latestArticleTree.value = latestArticleIds
        categoryArticleTree.value = categoryArticleIds
        categoryHasMore.value = categoryArticleIds.length === categoryPageSize && categoryArticleIds.length < parseInt(page.total);
        categoryPageNum.value++;
    } finally {
        loadingMoreArticle.value = false;
    }
};

export const clearCurrentArticle = async () => {
    if (saveInterval) {
        clearInterval(saveInterval);
        saveInterval = null;
    }
    if (!selectedArticleDetail.value?.canEdit) {
        return;
    }
    if (!contentChanged.value && !titleChanged.value && !tagsChanged.value && !selectedArticleDetail.value.tags) {
        return;
    }
    const promises: Promise<void>[] = [];
    if (contentChanged.value) {
        promises.push(saveContent());
        contentChanged.value = false;
    }
    if (titleChanged.value) {
        promises.push(saveTitle());
        titleChanged.value = false;
    }
    if (tagsChanged.value) {
        promises.push(saveTags());
        tagsChanged.value = false;
    }
    await Promise.all(promises);
};

export const saveTitle = async () => {
    if (!selectedArticleDetail.value || !selectedArticleDetail.value?.canEdit) {
        return;
    }
    if (!titleChanged.value) {
        return;
    }
    titleChanged.value = false;
    savingTitle.value = true;
    try {
        getSuccessData(await updateArticleTitle(selectedArticleDetail.value.id, selectedArticleDetail.value.title));
    } finally {
        savingTitle.value = false;
    }
};

export const saveContent = async () => {
    if (!selectedArticleDetail.value || !selectedArticleDetail.value?.canEdit) {
        return;
    }
    if (!contentChanged.value) {
        return;
    }
    contentChanged.value = false;
    savingContent.value = true;
    try {
        getSuccessData(await updateArticleContent(selectedArticleDetail.value.id, selectedArticleDetail.value.content));
    } finally {
        savingContent.value = false;
    }
};

export const saveTags = async () => {
    if (!selectedArticleDetail.value || !selectedArticleDetail.value?.canEdit) {
        return;
    }
    if (!tagsChanged.value) {
        return;
    }
    tagsChanged.value = false;
    savingTags.value = true;
    try {
        getSuccessData(await updateArticleTags(selectedArticleDetail.value.id, selectedArticleDetail.value.tags.map(tag => tag.id)));
    } finally {
        savingTags.value = false;
    }
};

const loadArticleDetail = async (articleId: string | null) => {
    loadingDetail.value = true;
    try {
        await clearCurrentArticle();
        if (!articleId) {
            selectedArticleDetail.value = null;
            return;
        }
        const detail = getSuccessData(await getArticleDetail(articleId));
        selectedArticleDetail.value = detail;
        if (detail) {
            initArticleContent.value = detail.content;
            if (detail.canEdit) {
                if (saveInterval) {
                    clearInterval(saveInterval);
                }
                saveInterval = setInterval(async () => {
                    await saveTitle();
                    await saveContent();
                }, 10000);
            }
        }
    } finally {
        loadingDetail.value = false;
    }
};

export const changeParent = async (articleId: string, newParentId: string | null) => {
    getSuccessData(await updateArticleParent(articleId, newParentId));
    const node = articleNodeMap.value.get(articleId);
    if (!node) {
        return;
    }
    if (node.parentId) {
        const oldParentNode = articleNodeMap.value.get(node.parentId);
        if (oldParentNode) {
            oldParentNode.children = oldParentNode.children.filter(id => id !== articleId);
        }
    } else {
        categoryArticleTree.value = categoryArticleTree.value.filter(id => id !== articleId);
    }
    if (newParentId) {
        const newParentNode = articleNodeMap.value.get(newParentId);
        if (newParentNode) {
            newParentNode.children = [...newParentNode.children, articleId];
        }
    } else {
        categoryArticleTree.value.push(articleId);
    }
    node.parentId = newParentId;
}

export const loadTags = async () => {
    if (!tagsHasMore.value || loadingMoreTags.value) {
        return;
    }
    loadingMoreTags.value = true;
    try {
        const page = getSuccessData(await listTags(tagsPageNum.value, tagsPageSize));
        const newTags = page.rows;
        if (newTags.length > 0) {
            availableTags.value = [...availableTags.value, ...newTags];
        }
        tagsHasMore.value = newTags.length === categoryPageSize && availableTags.value.length < parseInt(page.total);
        tagsPageNum.value++;
    } finally {
        loadingMoreTags.value = false;
    }
};

export const resetTags = async () => {
    tagsPageNum.value = 1;
    tagsHasMore.value = true;
    availableTags.value = [];
};

export const createNewTag = async (name: string, remark: string) => {
    getSuccessData(await createTag(name, remark));
    await resetTags();
}

export const updateTags = (newTagIds: string[]) => {
    if (!selectedArticleDetail.value) {
        return;
    }
    tagsChanged.value = true;
    const currentTags = selectedArticleDetail.value.tags || [];
    const retainedTags = currentTags.filter(tag => newTagIds.includes(tag.id));
    const newTags = newTagIds
        .filter(tagId => !currentTags.some(tag => tag.id === tagId))
        .map(tagId => ({
            id: tagId,
            name: availableTags.value.find(tag => tag.id === tagId)?.name || null
        }));
    selectedArticleDetail.value.tags = [...retainedTags, ...newTags];
};

watch(user, async () => {
    await loadArticleDetail(selectedArticleId.value);
    await initAllArticles();
    await resetTags();
});
