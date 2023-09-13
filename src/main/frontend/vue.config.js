module.exports = {
    devServer: {
        publicPath: '/',
        proxy: {
            '/api': {
                target: 'http://localhost:8081',
                pathRewrite: {
                    '^/api': ''
                },
                changeOrigin: true,
                secure: false
            }
        }
    }
}