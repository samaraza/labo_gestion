FROM node:16 as build-stage
WORKDIR /app

COPY package*.json ./
RUN npm install --include=dev

COPY . .
RUN npx ng build --configuration production

FROM nginx:alpine
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build-stage /app/dist/labo-gestion-front /usr/share/nginx/html
EXPOSE 80
