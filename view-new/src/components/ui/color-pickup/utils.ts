import type {Ref} from "vue";

export const colorPickerKey = Symbol();

export type Context = {
    hue: Ref<number>;
    saturation: Ref<number>;
    lightness: Ref<number>;
    alpha: Ref<number>;
    mode: Ref<string>;
};
