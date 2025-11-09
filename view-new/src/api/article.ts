import {user} from "@/store/userStore.ts";
import {BASE_URL, TIMEOUT} from './config';

const debugMode = false;

export interface ResponseResult<T> {
    code: number
    msg: string
    data: T
}

export interface PageResult<T> {
    rows: T
    total: string
}

export interface LoginResult {
    id: string
    token: string
    tokenMaxAgeSeconds: number
}

export interface ArticleNode {
    id: string
    title: string
    parentId: string | null
    canDelete: boolean
    canCreateChild: boolean
    canChangeParent: boolean
    children: string[]
}

export interface ArticleTag {
    id: string
    name: string | null
}

export interface ArticleDetail {
    id: string
    title: string
    content: string
    viewCount: string
    createTime: string
    updateTime: string
    author: string
    canEdit: boolean
    tags: ArticleTag[]
}

export interface Tag {
    id: string
    name: string
    remark: string
}

export const SUCCESS_CODE = 200;

const buildUrl = (url: string, requestParams: object) => {
    if (Object.keys(requestParams).length <= 0) {
        return url;
    }
    const params = new URLSearchParams();
    for (const [key, value] of Object.entries(requestParams)) {
        if (value !== undefined && value !== null) {
            params.append(key, String(value));
        }
    }
    const queryString = params.toString();
    if (queryString) {
        url = url.includes('?') ? `${url}&${queryString}` : `${url}?${queryString}`;
    }
    return url;
}

const checkResponse = (response: Response) => {
    if (!response.ok) {
        throw Error(`HTTP error! status: ${response.status}`);
    }
}

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

const mockSuccessResponse = <T>(data: T): ResponseResult<T> => ({
    code: SUCCESS_CODE,
    msg: 'success',
    data
});

const getHeader = (headers: HeadersInit | undefined) => {
    if (!user.value?.token) {
        return headers;
    }
    return {
        ...headers,
        'Authorization': `Bearer ${user.value?.token}`,
    };
}

const request = async <T>(url: string, options: RequestInit = {}, requestParams: object = {}): Promise<ResponseResult<T>> => {
    const finalUrl = buildUrl(url, requestParams);
    const headers = getHeader(options.headers);
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), TIMEOUT);
    try {
        const response = await fetch(finalUrl, {
            ...options,
            headers,
            signal: controller.signal,
        });
        checkResponse(response);
        return await response.json();
    } finally {
        clearTimeout(timeoutId);
    }
}

export const createArticle = async (draft: boolean, title: string, parentArticleId: string | null): Promise<ResponseResult<string>> => {
    if (debugMode) {
        await delay(300);
        return mockSuccessResponse('12345');
    }

    return await request<string>(`${BASE_URL}/article`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            title,
            draft,
            parentArticleId,
        }),
    });
}

export const createTag = async (name: string, remark: string): Promise<ResponseResult<string>> => {
    if (debugMode) {
        await delay(300);
        return mockSuccessResponse('12345');
    }

    return await request<string>(`${BASE_URL}/tag`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            name,
            remark,
        }),
    });
}

export const deleteArticle = async (id: string): Promise<ResponseResult<void>> => {
    if (debugMode) {
        await delay(300);
        return mockSuccessResponse(undefined);
    }

    return await request<void>(`${BASE_URL}/article/${id}`, {
        method: 'DELETE',
    });
}

export const increaseArticleViewCount = async (id: string): Promise<ResponseResult<void>> => {
    if (debugMode) {
        await delay(300);
        return mockSuccessResponse(undefined);
    }

    return await request<void>(`${BASE_URL}/article/${id}/viewCount`, {
        method: 'PUT'
    });
}

export const updateArticleTitle = async (id: string, title: string): Promise<ResponseResult<void>> => {
    if (debugMode) {
        await delay(300);
        return mockSuccessResponse(undefined);
    }

    return await request<void>(`${BASE_URL}/article/${id}/title`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            title
        }),
    });
}

export const updateArticleContent = async (id: string, content: string): Promise<ResponseResult<void>> => {
    if (debugMode) {
        await delay(300);
        return mockSuccessResponse(undefined);
    }

    return await request<void>(`${BASE_URL}/article/${id}/content`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            content
        }),
    });
}

export const updateArticleParent = async (id: string, newParentId: string | null): Promise<ResponseResult<void>> => {
    if (debugMode) {
        await delay(300);
        return mockSuccessResponse(undefined);
    }

    return await request<void>(`${BASE_URL}/article/${id}/parent`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            parentId: newParentId
        }),
    });
}

export const updateArticleTags = async (id: string, tagIds: string[]): Promise<ResponseResult<void>> => {
    if (debugMode) {
        await delay(300);
        return mockSuccessResponse(undefined);
    }

    return await request<void>(`${BASE_URL}/article/${id}/tags`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            tagIds
        }),
    });
}

export const getHotArticleIds = async (): Promise<ResponseResult<string[]>> => {
    if (debugMode) {
        await delay(300);
        return mockSuccessResponse(['article_1', 'article_2', 'article_3', 'article_4', 'article_5']);
    }

    return await request<string[]>(`${BASE_URL}/article/hotArticleIds`);
}

export const getLatestArticleIds = async (): Promise<ResponseResult<string[]>> => {
    if (debugMode) {
        await delay(300);
        return mockSuccessResponse(['article_6', 'article_7', 'article_8', 'article_9', 'article_10']);
    }

    return await request<string[]>(`${BASE_URL}/article/latestArticleIds`);
}

export const listCategoryArticleIds = async (pageNum: number, pageSize: number): Promise<ResponseResult<PageResult<string[]>>> => {
    if (debugMode) {
        await delay(300);
        return mockSuccessResponse({
            rows: ['article_1', 'article_2', 'article_3'],
            total: '10'
        });
    }

    return await request<PageResult<string[]>>(`${BASE_URL}/article/categoryArticleIds`, {}, {pageNum, pageSize});
}

export const getArticleNodes = async (articleIds: string[]): Promise<ResponseResult<ArticleNode[]>> => {
    if (debugMode) {
        await delay(300);
        return mockSuccessResponse([
            {
                id: 'article_1',
                title: '文章标题1',
                parentId: null,
                canDelete: true,
                canCreateChild: true,
                canChangeParent: true,
                children: ['article_1_1', 'article_1_2']
            },
            {
                id: 'article_2',
                title: '文章标题2',
                parentId: null,
                canDelete: false,
                canCreateChild: false,
                canChangeParent: false,
                children: []
            }
        ]);
    }

    return await request<ArticleNode[]>(`${BASE_URL}/article/articleNodes`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(articleIds),
    });
}

export const getArticleDetail = async (id: string): Promise<ResponseResult<ArticleDetail | null>> => {
    if (debugMode) {
        await delay(300);
        return mockSuccessResponse({
            id: 'article_1',
            title: '文章标题1',
            content: '# 标题\n这是一篇示例文章的内容。\n\n## 子标题\n这是子标题下的内容。',
            viewCount: "100",
            createTime: new Date().getTime().toString(),
            updateTime: new Date().getTime().toString(),
            author: '作者1',
            canEdit: true,
            tags: [],
        });
    }
    return await request<ArticleDetail | null>(`${BASE_URL}/article/${id}`);
}

export const listTags = async (pageNum: number, pageSize: number): Promise<ResponseResult<PageResult<Tag[]>>> => {
    return await request<PageResult<Tag[]>>(`${BASE_URL}/tag/list`, {}, {pageNum, pageSize});
}

export const login = async (username: string, password: string): Promise<ResponseResult<LoginResult>> => {
    if (debugMode) {
        await delay(300);
        return mockSuccessResponse({
            id: "1",
            token: "1",
            tokenMaxAgeSeconds: 300,
        });
    }
    return await request<LoginResult>(`${BASE_URL}/system/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            username,
            password,
        }),
    });
}

export const logout = async () => {
    if (debugMode) {
        await delay(300);
        return mockSuccessResponse(undefined);
    }
    return await request<void>(`${BASE_URL}/system/logout`, {
        method: 'POST',
    });
}
