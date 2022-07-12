import { merge } from "webpack-merge";
import { defConfig, config, defPlugins, defEntry } from "./webpack.config";
import UglifyJsPlugin from "uglifyjs-webpack-plugin";

export default merge(config, {
  mode: "production",
  ...defEntry,
  ...defConfig,
  devtool: false,
  optimization: {
    //@ts-ignore
    minimizer: [new UglifyJsPlugin()],
  },
  plugins: defPlugins,
});
