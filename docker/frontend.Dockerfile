FROM node:22-alpine AS build
WORKDIR /app

COPY frontend/package*.json ./
RUN npm ci --no-audit

COPY frontend/ ./
ARG VITE_OPEN_ROUTE_API_KEY
ENV VITE_OPEN_ROUTE_API_KEY=$VITE_OPEN_ROUTE_API_KEY
RUN npm run build

FROM nginx:stable-alpine
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
