FROM nginx
RUN apt-get update && apt-get install -y procps
WORKDIR /usr/share/nginx/html
COPY web/wikipedia_jenkins.html /usr/share/nginx/html
CMD cd /usr/nginx/html && sed -e s/Docker/"$AUTHOR"/ wikipedia_jenkins.html > wikipedia_jenkins.html ; nginx -g 'daemon off;'
