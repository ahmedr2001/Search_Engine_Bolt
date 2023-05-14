/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        "primary": "var(--primary)", //bg
        "secondary": "var(--secondry)", //text
        "highlight": "var(--highlight)", //borders
        "overlay": "var(--overlay)", //second bg
        "res-border": "var(--res-border)",
        "res-color": "var(--res-color)",
        "res-bg": "var(--res-bg)"
      }
      /* light: {
        'primary': '#F8F8F8',
        'secondry': '#A3811A',
        'overlay': '#FFF5ED',
        'text': '#000'
      },
      blue: {
        'primary': '#000814',
        'secondry': '#003566',
        'overlay': '#0C1826',
        'text': '#FFC300'
      },
      dark: {
        'primary': '#1A1919',
        'secondry': '#232323',
        'overlay': '#1A1919',
        'text': '#FFC300'
      }, */
    },
  },
  plugins: [],
}
