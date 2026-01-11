# Tree API

RESTful API za upravljanje hijerarhijskim podacima s Web sučeljem za vizualno uređivanje stabla lokacija.

<table>
  <tr>
    <td><img width="466" height="602" alt="Screenshot 2026-01-11 at 20 35 55" src="https://github.com/user-attachments/assets/d691adb4-261c-41cd-82cd-caab1cc447a0"/>
</td>
    <td><img width="1076" height="710" alt="Screenshot 2026-01-11 at 20 53 28" src="https://github.com/user-attachments/assets/b80b008c-b211-419f-8e4f-a829add93455" /></td>
  </tr>
</table>

## Tehnologije

### Backend
- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Data JPA**
- **H2 Database**
- **Maven**
- **Lombok**

### Frontend
- **HTML5**
- **CSS3**
- **JavaScript (ES6)**

## Requirements

- **Java JDK 17 ili novije**
- **Maven 3.6+**

## Instalacija

### 1. Kloniranje projekta
```bash
git clone <repository-url>
cd tree-api/pincode-projekt
```

### 2. Instalacija dependencija
Dependencies se automatski preuzimaju tijekom build procesa.

## Pokretanje

```bash
# Čišćenje i build
./mvnw clean package

# Pokretanje aplikacije
./mvnw spring-boot:run
```

### ili s instaliranim Mavenom:

```bash
mvn spring-boot:run
```

## Pristup Aplikaciji

Nakon pokretanja, aplikacija je dostupna na:
- **Frontend**: http://localhost:8080
- **API**: http://localhost:8080/nodes


## API Endpoints

### GET - Dohvat stabla
```bash
GET http://localhost:8080/nodes
```
Vraća cijelo stablo lokacija počevši od root čvora.

**Response:**
```json
{
  "id": 1,
  "title": "Root",
  "parentNodeId": null,
  "children": [
    {
      "id": 2,
      "title": "Child 1",
      "parentNodeId": 1,
      "sortOrder": 1,
      "hasChildren": false
    }
  ]
}
```

### GET - Dohvat specifičnog čvora
```bash
GET http://localhost:8080/nodes/{id}
```

### POST - Stvaranje novog čvora
```bash
POST http://localhost:8080/nodes
Content-Type: application/json

{
  "title": "Nova lokacija",
  "parentNodeId": 1
}
```

### DELETE - Brisanje čvora
```bash
DELETE http://localhost:8080/nodes/{id}
```
**Root node (id=1) se ne može obrisati**

### POST - Premještanje čvora
```bash
POST http://localhost:8080/nodes/{id}/move
Content-Type: application/json

{
  "newParentId": 3
}
```

### POST - Promjena redoslijeda čvorova
```bash
POST http://localhost:8080/nodes/{id}/reorder
Content-Type: application/json

{
  "direction": "UP"
}
```
Moguće vrijednosti: `UP` ili `DOWN`

## Funkcionalnosti

### Čitanje Stabla
- Učitaj cijelo stablo lokacija s hijerarhijom
- Vizualni prikaz sa granama i vezama između čvorova

### Stvaranje Čvorova
- Dodaj novi čvor kao dijete postojećeg čvora
- Dodaj čvorove na bilo koju razinu stabla
- sortOrder

### Brisanje Čvorova
- Obriši bilo koji čvor osim root-a
- Potvrdna poruka prije brisanja

### Premještanje Čvorova
- Premjesti čvor pod drugog roditelja

### Promjena Redoslijeda
- Poredaj čvorove gore/dolje

## Frontend

### Buttons
Delete - za brisanje čvora (Nije moguće obrisati korijenski - javit će se pop up s porukom)
Add Child - Dodaje node za navedeni parent node
Change Parent - Moguće je naknadno promjeniti parent node 
Reorder - Moguće je promjeniti redoslijed child nodova.
Reload - osvježavanje

### Forma za Stvaranje
- Input polje za naslov čvora
- Polje za ID roditelja
- Tipke za potvrdu i otkazivanje

## Model

### LocationNode Entity
```java
@Entity
public class LocationNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Long parentNodeId;
    private Integer sortOrder;
}
```
