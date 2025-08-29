import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    // 로컬 개발용 포트
    port: 5173,
    // 배포용 포트 (필요시)
    // port: 80,
    proxy: {
      // '/api'로 시작하는 요청을 localhost:8080으로 전달
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      // 배포용 프록시 설정 (필요시)
      // '/api': {
      //   target: 'http://tftshare.com:8080',
      //   changeOrigin: true,
      //   secure: false,
      // },
    },
  },
})
