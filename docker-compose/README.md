login into git, to be able to pull images
```bash
docker login -u $GITHUB_USERNAME -p $GITHUB_TOKEN docker.pkg.github.com
```
show containers
```bash
$ docker ps
```
show logs of container
```bash
$ docker logs 877a6073623a
```
run docker compose up
```bash
$ docker-compose up -d
```
