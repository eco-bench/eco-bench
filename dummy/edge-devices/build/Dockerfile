FROM --platform=linux/amd64 node:14-alpine

WORKDIR ~
COPY ../ ./

RUN npm i

# Since we will define entrypoint  by choosing a specific script
CMD ["node", "App.js"]
