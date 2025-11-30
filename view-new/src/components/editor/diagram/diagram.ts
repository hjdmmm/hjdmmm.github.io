import DiagramNodeView from "@/components/editor/diagram/DiagramNodeView.vue";
import {Node} from "@tiptap/core";
import {mergeAttributes, VueNodeViewRenderer} from "@tiptap/vue-3";
import {DiagramType} from "./utils.ts";

declare module '@tiptap/core' {
    interface Commands<ReturnType> {
        diagram: {
            setMermaid: (code: string) => ReturnType
        }
    }
}

export const Diagram = Node.create({
    name: 'diagram',
    group: 'block',
    atom: true,
    draggable: true,

    parseHTML() {
        return [{tag: 'diagram-block'}];
    },

    renderHTML({HTMLAttributes}) {
        return ['diagram-block', mergeAttributes(HTMLAttributes)];
    },

    addAttributes() {
        return {
            ratio: {
                default: null,
                parseHTML: (element) => element.getAttribute('diagram-ratio'),
                renderHTML: (attributes) => ({'diagram-ratio': attributes.ratio}),
            },
            type: {
                default: DiagramType.Mermaid,
                parseHTML: (element) => element.getAttribute('diagram-type'),
                renderHTML: (attributes) => ({'diagram-type': attributes.type}),
            },
            code: {
                default: '',
                parseHTML: (element) => element.getAttribute('diagram-code') || '',
                renderHTML: (attributes) => ({'diagram-code': attributes.code}),
            },
        };
    },

    addNodeView() {
        return VueNodeViewRenderer(DiagramNodeView);
    },

    addCommands() {
        return {
            setMermaid: (code) => ({commands}) => {
                return commands.insertContent({
                    type: this.name,
                    attrs: {
                        type: DiagramType.Mermaid,
                        code: code,
                    },
                });
            },
        };
    },
});
