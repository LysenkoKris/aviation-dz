# Авиаперевозки

Сущности: самолёт, рейс, билет, пассажир, бонусный счёт.

Самолёт и рейс связаны один-ко-многим. Пассажир и рейс — многие-ко-многим через билет (место и класс). У пассажира один бонусный счёт.

## Запуск

```bash
docker compose up -d
./mvnw spring-boot:run
```

Приложение: http://localhost:8080

База: `localhost:5432`, имя `aviation`, логин и пароль `aviation`.

Таблицы создаёт Hibernate при старте. `DemoDataLoader` добавляет примеры: два самолёта, рейсы SU100, SU200 и SU101, Анну и Бориса со счетами, билет Анны на место 12A.

Сбросить базу и залить данные заново:

```bash
docker compose down -v
docker compose up -d
```

## Как проверить

Postman: `postman/aviation.postman_collection.json` запускать сверху вниз.

Или скрипт:

```bash
python3 scripts/check.py
```

Запросы вручную:

```bash
# 1:N, рейс и самолёт
curl -s http://localhost:8080/api/flights/1

# N:M, билет
curl -s 'http://localhost:8080/api/tickets?seatClass=ECONOMY'

# 1:1, пассажир и бонусный счёт
curl -s http://localhost:8080/api/passengers/1

# запрос Spring Data по имени метода, FlightRepository.findByStatus
curl -s 'http://localhost:8080/api/flights/by-status?status=SCHEDULED'

# native SQL, AircraftRepository.findWithCapacityAtLeast
curl -s 'http://localhost:8080/api/aircraft/by-capacity?minCapacity=300'

# JPQL, TicketRepository.findBySeatClass
curl -s 'http://localhost:8080/api/tickets?seatClass=ECONOMY'

# Criteria API, FlightService.search. Любой параметр можно не передавать
curl -s 'http://localhost:8080/api/flights/search?departureAirport=svo&status=SCHEDULED'
```
