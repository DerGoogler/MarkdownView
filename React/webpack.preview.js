var webpack = require("webpack");
var path = require("path");

var config = {
  entry: {
    "markdown-tools": ["./src/library/Markdown.js"],
  },
  devtool: "source-map",
  output: {
    path: path.resolve(__dirname, "./../library/src/main/assets"),
    filename: "bundle/[name].bundle.js",
    library: "Markdown",
    libraryTarget: "umd",
    umdNamedDefine: true,
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        use: "babel-loader",
        exclude: /node_modules/,
      },
    ],
  },
  resolveLoader: {
    modules: ["node_modules", path.join(process.env.NPM_CONFIG_PREFIX || __dirname, "lib/node_modules")],
  },
  resolve: {
    symlinks: false,
    cacheWithContext: false,
    modules: ["node_modules", path.join(process.env.NPM_CONFIG_PREFIX || __dirname, "lib/node_modules")],
    extensions: ["", ".js"],
  },
};

module.exports = config;
