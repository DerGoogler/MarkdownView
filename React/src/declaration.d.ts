declare module "react-dom";
declare module 'highlight.js/lib/languages/bash';
declare module 'react-lowlight';

declare module "*.css" {
  const content: Record<string, string>;
  export default content;
}

declare module "*.scss" {
  const content: Record<string, string>;
  export default content;
}

declare module "*.sass" {
  const content: Record<string, string>;
  export default content;
}

declare module "*.txt" {
  const content: Record<string, string>;
  export default content;
}
