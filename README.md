# SWEN2-project

## Für Development:

### Datenbank starten:<br>
``docker-compose up db -d ``

datenbank wird automatisch mit test daten initialisiert mit `/resoures/data.sql`


### Backend Starten:
``Swen2TourPlannerApplication``

### Frontend Starten:
```
cd /frontend
npm ci
npm run dev
```


## Alles ausführen (production)

### Docker-compose starten:
```
docker-compose up
```

führt datenbank, backend und frontent aus (aber dann kein hot-reload)