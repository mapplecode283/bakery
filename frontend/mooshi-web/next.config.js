/** @type {import('next').NextConfig} */
const nextConfig = {
  images: {
    remotePatterns: [
      { protocol: 'https', hostname: '**' }
    ]
  },
  env: {
    NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api',
    NEXT_PUBLIC_ENABLE_DELIVERY: process.env.NEXT_PUBLIC_ENABLE_DELIVERY || 'false',
  }
}

module.exports = nextConfig
