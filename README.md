# MeteoAktiv

MVP web aplikacija koja govori korisnicima je li vrijeme u sljedeća 3 dana pogodno za
njihove vanjske aktivnosti (planinarenje, trčanje, ribolov, astronomija), po gradu.
Bez prijave, bez e-maila, bez admin sučelja.

## Arhitektura

- **Backend**: Java 21, Spring Boot 3, Spring Data JPA, Flyway, PostgreSQL
- **Frontend**: React + Vite + TypeScript, čisti CSS
- Podaci o prognozi dolaze s [Open-Meteo](https://open-meteo.com/) (ECMWF, ICON, GFS),
  kombinirani u aritmetičku sredinu po satu i parametru. Dohvat se pokreće pri
  pokretanju aplikacije i zatim svaka 3 sata.

## Pokretanje (docker compose)

Preduvjet: Docker i Docker Compose.

```bash
docker compose up --build
```

Nakon pokretanja:
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080/api/cities
- PostgreSQL: localhost:5432 (meteoaktiv/meteoaktiv)

Flyway migracije (shema + seed podaci za gradove i aktivnosti) pokreću se
automatski pri startu backenda. Prvi dohvat prognoze za sve gradove kreće
odmah nakon pokretanja backenda (zakazani posao s `initialDelay=0`), tako da
će se stvarni podaci pojaviti u sučelju nekoliko sekundi nakon pokretanja.

## Lokalni razvoj (bez Dockera za backend/frontend)

Pokrenite samo bazu podataka u Dockeru:

```bash
docker compose up db
```

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

Backend sluša na `http://localhost:8080` i koristi iste env-var
default vrijednosti (`DB_HOST=localhost`, `DB_USER=meteoaktiv`, ...) pa je
spojiv na bazu iz `docker compose up db` bez dodatne konfiguracije.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend (Vite dev server, http://localhost:5173) proksira `/api/*` pozive na
`http://localhost:8080`.

## Testovi

```bash
cd backend
./mvnw test
```

Testovi pokrivaju jedinicu koja odlučuje je li dan pogodan za aktivnost
(`SuitabilityServiceTest`): normalan dan, prozor koji prelazi ponoć
(astronomija), niz dugačak točno koliko je minimalno potrebno, niz koji je
prekratak (s objašnjenjem razloga), nedostajući podaci o satu, i prag bez
donje/gornje granice.

## API

- `GET /api/cities` — popis gradova
- `GET /api/activities` — popis aktivnosti
- `GET /api/suitability?cityId=1&activityIds=1,2,3` — za svaku aktivnost:
  pogodnost za "sada" te za svaki od 3 dana (pogodno/ne, najbolji vremenski
  blok, razlozi ako nije pogodno, satni pregled)
- `GET /api/status` — vrijeme zadnjeg uspješnog dohvata prognoze

## Gradovi i aktivnosti

15 hrvatskih gradova (Zagreb, Split, Rijeka, Osijek, Zadar, Pula, Slavonski
Brod, Karlovac, Varaždin, Šibenik, Dubrovnik, Velika Gorica, Sisak, Gospić,
Čakovec) i 4 aktivnosti (Planinarenje, Trčanje, Ribolov, Astronomija) sa
pragovima temperature, oborina, vjetra i naoblake sjemenuju se Flyway
migracijama (`backend/src/main/resources/db/migration`).

## Napomena o greškama dohvata

Ako Open-Meteo API ne odgovori ili vrati grešku za neki grad, posao to
zabilježi u log i zadrži postojeće podatke za taj grad — stari podaci ne
brišu se dok novi uspješno ne stignu.
