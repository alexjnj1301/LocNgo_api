# Locn'Go - Backend (Spring Boot)

## 🚀 Démarrage rapide

### Prérequis
- JDK 21
- Docker (PostgreSQL)
- Maven

### Installation et lancement
```bash
docker compose up -d db
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Profils
- `dev`
- `prod` (fichier `application-prod.yml`)

### Documentation API
- Swagger UI disponible sur `/swagger-ui`

## 🏗️ Architecture
- Spring Boot 3.x
- Spring Web, Data JPA, Validation, Security, Actuator
- Couches : controller → service → repository → domain
- DTO/Mapper : MapStruct
- Erreurs formatées RFC 7807 (Problem+JSON)
- Entités : `User`, `Listing`, `Booking`, `Amenity`, `Review`, `Availability`

## 🔐 Sécurité
- Authentification JWT/OIDC
- Rôles : RENTER, OWNER, ADMIN
- Validation côté serveur (Bean Validation)
- Rate limiting (Bucket4j)
- CORS restreint
- Logs d’audit

## 🗄️ Persistance & Migrations
- PostgreSQL
- Flyway pour migrations
- Index sur clés de recherche
- Soft-delete/status si besoin

## 🌐 API & Contrats
- RESTful + pagination, tri, filtre
- OpenAPI générée avec exemples de requêtes

## 📈 Observabilité & Performance
- Actuator (health, metrics)
- Export Prometheus
- Guard anti N+1 (fetch join)
- Pagination serveur stricte

## 🧪 Tests & Qualité
- Unitaires : JUnit5/Mockito
- Slice tests : @WebMvcTest, @DataJpaTest
- Intégration : Testcontainers
- Qualité : Checkstyle/SpotBugs, SonarLint local
- Couverture cible : >= 75%

## 🔁 CI/CD & Versionning
- GitHub Actions : build, tests, SCA, image Docker
- Tags semver + CHANGELOG.md
- Déploiement staging → prod, migrations Flyway auto

## 📚 Exploitation
- Manuel de déploiement (Docker, env, secrets)
- Manuel utilisateur (endpoints clés)
- Manuel de mise à jour (semver, migrations, rollback)

## 🛟 Supervision & Alertes
- Health checks, métriques (latence, throughput, erreurs 5xx)
- Alertes configurées (Slack/Email)

---

## 📚 Documentation & Références

### Documentation officielle
- [Apache Maven](https://maven.apache.org/guides/index.html)
- [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.3.5/maven-plugin)
- [Créer une image OCI avec Spring Boot](https://docs.spring.io/spring-boot/3.3.5/maven-plugin/build-image.html)
- [Spring Web](https://docs.spring.io/spring-boot/3.3.5/reference/web/servlet.html)
- [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.3.5/reference/using/devtools.html)

### Guides pratiques
- [Créer un service RESTful](https://spring.io/guides/gs/rest-service/)
- [Servir du contenu web avec Spring MVC](https://spring.io/guides/gs/serving-web-content/)
- [Construire des services REST avec Spring](https://spring.io/guides/tutorials/rest/)

### Remarque sur Maven Parent overrides
En raison de la conception de Maven, certains éléments comme `<license>` et `<developers>` sont hérités du parent POM.  
Si vous changez de parent et souhaitez réactiver ces éléments, supprimez les overrides dans le `pom.xml`.
