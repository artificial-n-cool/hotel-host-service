build:
	docker-compose --env-file "/home/matija/programi/DevOps/Dockerfiles/.env" build
	docker-compose --env-file "/home/matija/programi/DevOps/Dockerfiles/.env" up  # -d
clean:
	docker stop host-app
	docker stop mongo-db
	docker rm host-app
	docker rm mongo-db
	docker rmi hostapp_host-app
init-repo:
	git init
	git add README.md
	git add src
	git add pom.xml
	git commit -m "feat(api)!: initialized model and database connection"
	git branch -M master
	git remote add origin https://github.com/MatijaMatovic/DevOpsProj.git
	git push -u origin master
