# Accès au back deployé (état de santé)
- https://api-locngo.com/actuator/health
# 🧰 À propos du projet (Backend)

Ce dépôt correspond à l’**API Locn’Go** (backend) développée avec **Spring Boot 3.x**, **Java 21** et **Maven**.

Il expose des endpoints REST pour notamment :
- **Lieux** (création, liste, détails, images)
- **Réservations**
- **Utilisateurs** & **rôles** (USER / PROPRIETAIRE / ADMIN)
- **Authentification JWT** (login → token, interceptor côté front)
- **Santé & supervision** via **Spring Boot Actuator** (`/actuator/health`, métriques essentielles)

Caractéristiques techniques :
- **Spring Web**, **Spring Data JPA/Hibernate**, **Spring Security (JWT)**
- Base de données : **MySQL** (dev & prod)
- Configuration par **profils** (`dev`, `prod`), variables d’environnement et `application.yml`
- **CORS** configuré pour autoriser le front
- **Docker** : image applicative prête pour déploiement (ECR/Cloud)

Qualité :
- Tests **JUnit 5 / Spring Boot Test** (unitaires & intégration)
- **Actuator** pour la supervision de base (health, info)
- Conformité aux **bonnes pratiques Spring** (stratification Controller / Service / Repository, DTO, validations)

---
# ⚙️ Installation & exécution en local (Backend)

## Prérequis
- **Java 21** (JDK 21)
- **Maven 3.9+** (ou le wrapper `./mvnw`)
- **MySQL 8+** (local, Docker, ou MAMP sur macOS)
- (Optionnel) **Docker** si vous préférez tout lancer en conteneurs

---

## Cloner le dépôt
```bash
git clone <url-du-repo-backend>
cd <repo-backend>
```

## Configuration des environnements

Le projet utilise des **profils Spring** (`dev`, `prod`).  
En local, le profil **dev** est recommandé.

### Variables d’environnement (recommandé)
Définissez ces variables dans votre shell (ou IDE) :

- `DB_HOST` (ex: `localhost`)
- `DB_PORT` (ex: `3306` — **`8889` si MAMP**)
- `DB_NAME` (ex: `locngo`)
- `DB_USER` (ex: `root`)
- `DB_PASSWORD` (ex: `root`)
- `JWT_SECRET` (ex: `change-me-in-prod`)
- `CORS_ALLOWED_ORIGINS` (ex: `http://localhost:4200`)

**Exemple macOS/Linux :**
```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=locngo
export DB_USER=root
export DB_PASSWORD=root
export JWT_SECRET=change-me-in-prod
export CORS_ALLOWED_ORIGINS=http://localhost:4200
```

**Note MAMP (macOS) :**
- Port MySQL par défaut **8889** → `export DB_PORT=8889`
- Utilisateur/MDP : souvent `root` / `root`

### `application-dev.yml` (exemple)
Le fichier `src/main/resources/application-dev.yml` peut ressembler à :
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:${MYSQL_HOST:localhost}}:${DB_PORT:${MYSQL_PORT:3306}}/${DB_NAME:locngo}?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:root}
  jpa:
    hibernate:
      ddl-auto: update
    open-in-view: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect

# CORS (autoriser le front en dev)
app:
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:4200}

# JWT (ne jamais commiter un vrai secret)
security:
  jwt:
    secret: ${JWT_SECRET:change-me}
    expiration-ms: 3600000

# Actuator (santé & infos)
management:
  endpoints:
    web:
      exposure:
        include: "health,info"
  endpoint:
    health:
      show-details: always
```

> 💡 Si vous utilisez un autre nom de base, créez-la au préalable :  
> `CREATE DATABASE locngo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`

---

## Lancer la base de données (au choix)

### A) MySQL local (déjà installé)
Assurez-vous que MySQL tourne et que l’utilisateur a les droits.

### B) Docker (MySQL)
```bash
docker run -d --name mysql-locngo   -e MYSQL_ROOT_PASSWORD=root   -e MYSQL_DATABASE=locngo   -p 3306:3306   mysql:8
```

### C) MAMP (macOS)
- Démarrez MAMP → MySQL port **8889**
- Configurez `DB_PORT=8889` (cf. plus haut)

---

## Démarrer l’application en **dev**
```bash
# Avec Maven installé
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Ou avec le wrapper
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

L’API est disponible sur **http://localhost:8080**.

### Vérifier la santé (Actuator)
```bash
curl http://localhost:8080/actuator/health
```
Réponse attendue (ex.) :
```json
{"status":"UP"}
```

---

## Build & exécution (jar)

### Build (avec tests)
```bash
mvn clean package -Dspring.profiles.active=dev
```

### Build (sans tests)
```bash
mvn clean package -DskipTests -Dspring.profiles.active=dev
```

### Lancer le jar
```bash
java -jar target/locngo-backend-*.jar --spring.profiles.active=dev
```

---

## Tests
```bash
# Unitaires & intégration
mvn test
```

---

## Dépannage (FAQ rapide)

- **Port 8080 déjà utilisé**  
  → Changez le port via `server.port=8081` (profil dev) ou libérez 8080.

- **Connexion MySQL refusée**  
  → Vérifiez `DB_HOST`, `DB_PORT`, `DB_USER`, `DB_PASSWORD`, et que la DB `locngo` existe.  
  → MAMP : utilisez le port **8889**.

- **Erreur JWT / démarrage**  
  → Définissez `JWT_SECRET` (variable d’environnement).

- **CORS depuis le front**  
  → Vérifiez `CORS_ALLOWED_ORIGINS` (ex : `http://localhost:4200`) et la config CORS Spring.

---
# 📂 Contenu du repository & conventions (Backend)

## Structure du code (Spring Boot)
```
src/
├─ main/
│  ├─ java/com/locngo/
│  │  ├─ security/            # JWT, filtres, SecurityConfig
│  │  ├─ controller/          # REST controllers (API)
│  │  ├─ service/             # Logique métier (@Service, @Transactional)
│  │  ├─ repository/          # Spring Data JPA
│  │  ├─ entity/              # Entités JPA/Hibernate
│  │  ├─ dto/                 # DTO d’E/S API
│  │  └─ exceptions/          # GlobalExceptionHandler, erreurs personnalisées
│  └─ resources/
│     ├─ application.yml
│     ├─ application-test.yml
│     └─ db/liquibase/        # scripts liquibase update BDD
└─ test/java/com/locngo/      # Tests unitaires
```

## Conventions & standards
- **Stratification** claire : *Controller → Service → Repository* ; DTO isolent l’API des entités JPA.
- **Nommage REST** : ressources au pluriel, chemins stables.
- **Validation** : `jakarta.validation` (`@NotBlank`, `@Email`, `@Size`…), `@Validated` en contrôleurs.
- **Réponses HTTP** : `201` à la création, `204` sur suppression, `400/401/403/404/409/422/500` selon cas.
- **Filtre** : Filtres par query params.
- **Sérialisation** : Jackson configuré (dates ISO-8601, `WRITE_DATES_AS_TIMESTAMPS=false`).

## Contrats d’API (exemples d’endpoints)
- `GET /api/lieux` – lister les lieux (pagination/tri/filtres)
- `GET /api/lieux/{id}` – détail d’un lieu (+ images)
- `POST /api/lieux` *(PROPRIETAIRE/ADMIN)* – créer un lieu
- `GET /api/reservations?userId=...` – réservations d’un utilisateur
- `POST /api/reservations` – créer une réservation
- `POST /api/auth/login` – authentifier, renvoyer un JWT
- `GET /actuator/health` – statut de santé (Actuator)

> Les noms exacts peuvent varier ; la règle est **REST + statuts HTTP cohérents**.

## Sécurité (JWT, rôles, CORS)
- **JWT** :
    - Authentification via `POST /api/auth/login` → renvoie un **access token**.
    - Toutes les requêtes protégées utilisent `Authorization: Bearer <token>`.
    - Durée de vie configurable (env `security.jwt.expiration-ms`).
- **Rôles** : `USER`, `PROPRIETAIRE`, `ADMIN`
    - Exemples d’accès :
        - `USER` : consulter lieux, créer réservation (ses données).
        - `PROPRIETAIRE` : CRUD sur **ses** lieux, consulter réservations associées.
        - `ADMIN` : accès élargi (modération/gestion).
    - **Contrôles** : `HttpSecurity` (antMatchers) et/ou annotations `@PreAuthorize("hasRole('ADMIN')")`.
- **Mots de passe** : hachage **BCrypt** (pas de stockage en clair).
- **CORS** :
    - Origines autorisées via env `CORS_ALLOWED_ORIGINS` (ex. `http://localhost:4200` en dev).
    - Méthodes/headers explicitement autorisés (GET/POST/PUT/PATCH/DELETE, `Authorization`, `Content-Type`).
- **Secrets** :
    - `JWT_SECRET`, accès DB, etc. **jamais commités** ; injectés par variables d’environnement.

## Gestion des erreurs & logs
- **Handler global** : `@ControllerAdvice` + `@ExceptionHandler` → format d’erreur unifié.
- **Problem Details (RFC 7807)** : possible via `ProblemDetail` (Spring 6) :
  ```json
  {
    "type": "https://locngo.app/errors/validation",
    "title": "Validation failed",
    "status": 422,
    "detail": "email must be valid",
    "instance": "/api/users"
  }
  ```
- **Logging** : SLF4J/Logback, niveaux par package (`application.yml`), pas de données sensibles en logs.

## Supervision (Actuator)
- **Endpoints** principaux :
    - `GET /actuator/health` → **UP**/**DOWN** (détails activables en dev).
    - (Optionnel) `metrics`, `prometheus` → **uniquement** si nécessaire et sécurisés.
- **Exposition** :
    - Dev : `management.endpoints.web.exposure.include=health,info` (détails `health.show-details=always`).
    - Prod : limiter aux endpoints strictement requis + sécurité réseau.

## Données & persistance
- **JPA/Hibernate** avec `open-in-view=false` (préférer le pattern service transactionnel).
- **Transactions** : `@Transactional` au niveau service pour les écritures multiples.

## DTO & mapping
- **ObjectMapper (Jackson)** pour conversions simples DTO↔Entity (actuel).
    - Exemple : `objectMapper.convertValue(entity, Dto.class)`.
- **MapStruct** recommandé si la cartographie devient complexe (perf + type-safety).

## Fichiers & qualité
- `.gitignore` (exclure `target/`, `/logs`, secrets locaux).
- `.editorconfig` (optionnel) pour homogénéiser les styles multi-IDE.
- **Tests** : viser des tests d’intégration sur routes critiques (auth, réservation).

## Environnements & configuration
- **Profils Spring** : `dev` (DX), `prod` (sécurisé, logs sobres).
- **Variables d’environnement** (voir section Installation) : DB, JWT, CORS…
- **Actuator** activé en dev, restreint en prod.

---
# 🔁 Intégration Continue & Déploiement (CI/CD) — Backend

Les workflows **GitHub Actions** sont définis dans `.github/workflows/` et se composent de **2 workflows** :

- `java-tests.yml` – **Tests unitaires Maven (Java 21)** sur push/PR
- `publish-ecr-on-tag.yml` – **Build Docker & push vers AWS ECR** à la création d’un **tag** (`v*`)

---

## 1) Tests unitaires — `java-tests.yml`

**Déclencheurs**
- `pull_request` sur `develop`, `master`, `main`
- `push` sur `develop`, `master`, `main`, et sur les branches `feature/**`

**Environnement**
- `ubuntu-latest`æ
- **JDK 21 (Temurin)** via `actions/setup-java@v4` (cache Maven activé)

**Étapes**
1. **Checkout** du code (`actions/checkout@v4`)
2. **Setup JDK 21** (Temurin) + cache Maven
3. **Exécution des tests unitaires** uniquement :
   ```bash
   mvn -B -ntp test -Dspring.profiles.active=test
   ```

**Objectif**
- Garantir la **qualité** du code par l’exécution systématique des tests unitaires avant merge et à chaque push.

---

## 2) Publication Docker vers AWS ECR — `publish-ecr-on-tag.yml`

**Déclencheur**
- `push` sur des **tags** de forme `v*` (ex. `v1.0.0`, `v1.1.1`)

**Concurrence**
- `concurrency: publish-ecr-${{ github.ref }}` (ne **pas** annuler les releases en cours)

**Variables d’environnement (injectées depuis secrets)**
- `AWS_REGION`, `AWS_ACCOUNT_ID`, `ECR_REPOSITORY`
- `IMAGE_TAG` (déduit de `github.ref_name`, ex. `v1.0.5`)
- `ECR_REGISTRY` (`<account>.dkr.ecr.<region>.amazonaws.com`)

**Étapes**
1. **Checkout** (`actions/checkout@v4`)
2. **Configurer les credentials AWS** (`aws-actions/configure-aws-credentials@v4`)
3. **Login ECR** (`aws-actions/amazon-ecr-login@v2`)
4. **Set up QEMU** (`docker/setup-qemu-action@v3`) — multi-arch prêt
5. **Set up Buildx** (`docker/setup-buildx-action@v3`)
6. **Build & Push** image (linux/amd64) via `docker/build-push-action@v6` :
    - Contexte `.` et `Dockerfile` racine
    - **Push** activé
    - **Tags** poussés :
        - `${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}`
        - `${ECR_REGISTRY}/${ECR_REPOSITORY}:latest`
    - Cache GHA activé (`cache-from/to: type=gha`)

**Objectif**
- Produire une **image Docker versionnée** du backend et la publier dans **AWS ECR** pour déploiement.

> 💡 *Recommandations optionnelles* : ajouter une validation SemVer du tag et vérifier que le tag pointe bien sur `main` avant release (garde-fous qualité).

---

## 🔑 Gestion des secrets

Ces secrets sont gérés via **GitHub Secrets** et **ne sont pas commités** :
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_ACCOUNT_ID`
- `AWS_REGION`
- `ECR_REPOSITORY`

Les secrets d’exécution applicative (ex. `JWT_SECRET`, accès DB) doivent être fournis **au runtime** (variables d’environnement du conteneur/orchestrateur) et non dans les workflows.

---

## 📍 Où consulter les résultats
- Onglet **Actions** du repository → détails des jobs, logs et artefacts.
- **AWS ECR** → dépôt `${ECR_REPOSITORY}` avec les images taguées `:vX.Y.Z` et `:latest`.

---
**PS**: Concernant l'évaluation de ce projet, il est important de noter que des modifications peuvent être apportées à ce projet après la date butoir de l'évaluation.
En conséquences, il est important de se fier au tag `v1.1.2` pour l'évaluation, les changements apportés après cette date ne doivent pas être pris en compte dans l'évaluation.
