import {type ResponseResult, SUCCESS_CODE} from "@/api/article.ts";
import type {ClassValue} from "clsx"
import {clsx} from "clsx"
import {twMerge} from "tailwind-merge"
import {toast} from "vue-sonner";

export function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs))
}

export const getSuccessData = <T>(response: ResponseResult<T>): T => {
    if (response.code !== SUCCESS_CODE) {
        toast.error('调用接口错误', {
            description: response.msg,
        });
        throw Error(response.msg);
    }
    return response.data;
}