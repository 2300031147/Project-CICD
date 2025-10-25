# ----------------------------------------------------------------------
# STAGE 1: BUILD - Create optimized static assets
# ----------------------------------------------------------------------
FROM node:18-alpine AS build

# Ensure all dependencies install correctly on Alpine
RUN apk add --no-cache python3 make g++ bash

# Set working directory
WORKDIR /app

# Copy dependency files first (for Docker caching)
COPY package.json package-lock.json ./

# Clean install to avoid corrupt lock states or partial installs
RUN npm install --no-audit --no-fund

# Copy the rest of the source code
COPY . .

# Run production build
RUN npm run build

# ----------------------------------------------------------------------
# STAGE 2: SERVE - Minimal Nginx image to serve the static assets
# ----------------------------------------------------------------------
FROM nginx:alpine

# Copy build output from the previous stage
COPY --from=build /app/build /usr/share/nginx/html

# Copy your Nginx configuration (optional)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose the Nginx port
EXPOSE 80

# Run Nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]
