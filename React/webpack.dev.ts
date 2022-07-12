import { merge } from "webpack-merge";
import { defConfig, config, defPlugins, defEntry } from "./webpack.config";

export default merge(config, {
  mode: "development",
  ...defEntry,
  ...defConfig,
  devtool: "source-map",
  plugins: defPlugins,
});
