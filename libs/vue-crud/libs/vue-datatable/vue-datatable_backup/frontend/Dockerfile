# Build Stage
FROM node:20-alpine AS build

WORKDIR /app

# Dependencies installieren
COPY package*.json ./
RUN npm install

# Source kopieren und bauen
COPY . .
RUN npm run build

# Production Stage
FROM nginx:stable-alpine

# Nginx Konfiguration kopieren
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Build-Artefakte kopieren
COPY --from=build /app/dist /usr/share/nginx/html

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --no-verbose --tries=1 --spider http://localhost/ || exit 1

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
