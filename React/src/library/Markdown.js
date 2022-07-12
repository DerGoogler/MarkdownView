const { dom } = require("googlers-tools");
const { marked } = require("marked");

var Markdown = {
  content: (content) => {
    dom.findBy("markdown-container", (element) => {
      element.innerHTML = marked.parse(content);
    });
  },
};

module.exports = Markdown;
