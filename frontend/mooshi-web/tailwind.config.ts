import type { Config } from 'tailwindcss'

const config: Config = {
  content: [
    './src/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        mooshi: {
          50: '#fdf8f0',
          100: '#f9eddb',
          200: '#f2d7b0',
          300: '#e8bb79',
          400: '#dd9b4a',
          500: '#c17a33',
          600: '#a45f2a',
          700: '#864826',
          800: '#703b25',
          900: '#5e3222',
          950: '#351811',
        },
        coffee: {
          50: '#faf6f2',
          100: '#f3ebe1',
          200: '#e5d4c0',
          300: '#d3b798',
          400: '#c09971',
          500: '#b48457',
          600: '#a6724b',
          700: '#8a5c40',
          800: '#714b38',
          900: '#5c3e30',
          950: '#311f18',
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
        display: ['Playfair Display', 'Georgia', 'serif'],
      }
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
    require('@tailwindcss/typography'),
  ],
}
export default config
