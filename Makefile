build:
	docker-compose build
	docker-compose up  # -d
clean:
	docker stop host-app
	docker stop mongo-db-host
	docker rm host-app
	docker rm mongo-db-host
	docker rmi hotel-host-service_host-app
init-repo:
	git init
	git add README.md
	git add src
	git add pom.xml
	git commit -m "feat(api)!: initialized model and database connection"
	git branch -M master
	git remote add origin https://github.com/MatijaMatovic/DevOpsProj.git
	git push -u origin master
dockerize:
	docker build -t host-app .
kube-cleanup:
	kubectl delete deployment host-app
	docker rm host-app
	docker rmi host-app
	
start-trace:
	minikube start
	eval $(minikube docker-env)
	docker build -t host-app .
	cd ~/programi/DevOps/kubernetes-config/project-chart && helm install -f host_values.yaml host-backend .

