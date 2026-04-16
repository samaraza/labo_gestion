FROM node:16 as build-stage
WORKDIR /app
# نسخ package.json من مجلد labo-gestion-front
COPY labo-gestion-front/package*.json ./
RUN npm install
# نسخ باقي ملفات Angular من نفس المجلد
COPY labo-gestion-front/ .
RUN npm run build --prod

FROM nginx:alpine
# نسخ ملف nginx.conf من مجلد labo-gestion-front
COPY labo-gestion-front/nginx.conf /etc/nginx/nginx.conf
# نسخ الملفات الثابتة الناتجة عن البناء
COPY --from=build-stage /app/dist/labo-gestion-front /usr/share/nginx/html
EXPOSE 80