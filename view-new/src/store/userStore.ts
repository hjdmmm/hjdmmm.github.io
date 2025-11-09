import {ref} from 'vue'

export const user = ref<{
    token: string,
    id: string,
} | null>(null);
