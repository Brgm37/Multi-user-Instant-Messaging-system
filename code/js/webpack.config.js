const path = require('path');

module.exports = {
  entry: './src/index.ts',
  mode: 'development',
  devServer: {
    port: 8000,
    historyApiFallback: true,
    compress: false,
    proxy: [
      {
        context: ['/api'],
        target: 'http://localhost:8080',
        onProxyRes: (proxyRes, _, res) => {
          console.log('onProxyRes');
          proxyRes.on('close', () => {
            console.log('on proxyRes close');
            if (!res.writableEnded) {
              res.end();
            }
          });
          res.on('close', () => {
            console.log('on res close');
            proxyRes.destroy();
          });
        },
      },
    ],
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        use: 'ts-loader',
        exclude: /node_modules/,
      },
      {
          test: /\.css$/,
          use: ['style-loader', 'css-loader', 'postcss-loader'],
      }
    ],
  },
  resolve: {
    extensions: ['.tsx', '.ts', '.js'],
  },
  output: {
    filename: 'bundle.js',
    path: path.resolve(__dirname, 'dist'),
  },
};