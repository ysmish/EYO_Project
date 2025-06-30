FROM node:latest

WORKDIR /app
COPY package*.json .
RUN npm install
COPY . .
RUN npm install -g serve
RUN npm run build
EXPOSE 8080
CMD ["serve", "-s", "build", "-l", "8080"]