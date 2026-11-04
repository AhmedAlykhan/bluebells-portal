/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // Official Bluebells Primary Brand Palette (White & Blue dominance)
        brand: {
          50: '#EAF3FC',   // Soft blue background tint
          100: '#D5E7FA',  // Light hover state
          200: '#AECFF5',  // Subtle border / accent
          300: '#74ADED',  // Secondary accent
          400: '#368CE5',  // Bright accent blue
          500: '#30BDFF',  // Bluebell blossom cyan
          600: '#2167B2',  // Primary interactive blue
          700: '#1A5392',  // Deep button hover
          800: '#123B70',  // Primary institutional navy
          900: '#0C2A52',  // Ultra dark navy header
          950: '#071A34',  // Midnight navy
        },
        // Semantic status colors with high legibility
        edu: {
          navy: '#123B70',
          blue: '#2167B2',
          sky: '#368CE5',
          cyan: '#30BDFF',
          ice: '#EAF3FC',
          canvas: '#F5F8FC',
          card: '#FFFFFF',
          darkText: '#172B45',
          mutedText: '#64748B',
          border: '#E2EAF3',
          success: '#16845B',
          warning: '#D99A28',
          danger: '#D94B55',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'sans-serif'],
      },
      boxShadow: {
        subtle: '0 1px 3px rgba(18, 59, 112, 0.05), 0 1px 2px rgba(18, 59, 112, 0.03)',
        card: '0 4px 12px -2px rgba(18, 59, 112, 0.06), 0 2px 6px -1px rgba(18, 59, 112, 0.03)',
        elevated: '0 12px 28px -4px rgba(18, 59, 112, 0.12), 0 4px 10px -2px rgba(18, 59, 112, 0.04)',
        modal: '0 24px 48px -12px rgba(18, 59, 112, 0.25)',
      },
    },
  },
  plugins: [],
}
