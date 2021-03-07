mvnw package
docker build -t messageboard .
docker run -d --name messageboard -p 8080:8080 messageboard