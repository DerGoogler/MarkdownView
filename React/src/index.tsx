import bash from "highlight.js/lib/languages/bash";
import { dom } from "googlers-tools";
import * as React from "react";
import Markdown from "marked-react";
import Lowlight from "react-lowlight";

interface MarkdownViewProps extends React.AllHTMLAttributes<HTMLElement> {}

class MarkdownView extends React.Component<MarkdownViewProps> {
  public constructor(props: MarkdownViewProps | Readonly<MarkdownViewProps>) {
    super(props);
  }

  public componentDidMount(): void {
    Lowlight.registerLanguage("bash", bash);
  }

  public render(): React.ReactNode {
    const {} = this.props;

    return (
      <div id="markdown-container" className="markdown-body">
        <Markdown
          value={"# lol\n\rLOL"}
          renderer={{
            code(snippet: any, lang: any) {
              return <Lowlight key={new Date().toString()} language={lang} value={snippet} />;
            },
          }}
        />
      </div>
    );
  }
}

// Render component into the DOM
dom.renderAuto(MarkdownView);

// Prevent context menu from appearing (there is no context menu lol)
dom.preventer(["contextmenu"]);
