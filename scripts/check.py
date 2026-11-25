#!/usr/bin/env python3
"""Проверяет пункты задания на запущенном приложении."""

import json
import random
import sys
import urllib.error
import urllib.parse
import urllib.request

BASE = "http://localhost:8080"


def request(method, path, body=None, expected=200):
    data = None if body is None else json.dumps(body).encode()
    req = urllib.request.Request(BASE + path, data=data, method=method)
    if body is not None:
        req.add_header("Content-Type", "application/json")
    try:
        with urllib.request.urlopen(req) as response:
            raw = response.read().decode()
            parsed = json.loads(raw) if raw else None
            if response.status != expected:
                fail(f"{method} {path} -> {response.status}, ожидался {expected}: {raw}")
            return parsed
    except urllib.error.HTTPError as error:
        raw = error.read().decode()
        try:
            parsed = json.loads(raw) if raw else None
        except json.JSONDecodeError:
            parsed = raw
        if error.code != expected:
            fail(f"{method} {path} -> {error.code}, ожидался {expected}: {raw}")
        return parsed


def fail(message):
    print(f"FAIL {message}")
    sys.exit(1)


def ok(message):
    print(f"OK   {message}")


def main():
    wide = request("GET", "/api/aircraft/by-capacity?minCapacity=300")
    registrations = {item["registrationNumber"] for item in wide}
    if "RA-96002" not in registrations or "RA-73001" in registrations:
        fail(f"native query вернул {registrations}")
    ok("native SQL: самолёты с вместимостью от 300")

    scheduled = request("GET", "/api/flights/by-status?status=SCHEDULED")
    numbers = {item["flightNumber"] for item in scheduled}
    if "SU100" not in numbers or "SU200" not in numbers:
        fail(f"derived query вернул {numbers}")
    if any(item["aircraft"]["registrationNumber"] is None for item in scheduled):
        fail("у рейса нет самолёта в ответе")
    ok("метод по имени: рейсы в статусе SCHEDULED")

    led = request("GET", "/api/flights/search?arrivalAirport=led")
    led_numbers = {item["flightNumber"] for item in led}
    if led_numbers != {"SU100"} and "SU100" not in led_numbers:
        fail(f"criteria arrival вернул {led_numbers}")
    if any(item["arrivalAirport"] != "LED" for item in led):
        fail("фильтр аэропорта прилёта не применился")
    svo = request("GET", "/api/flights/search?departureAirport=svo&status=SCHEDULED")
    if not svo or any(item["departureAirport"] != "SVO" or item["status"] != "SCHEDULED" for item in svo):
        fail("составной criteria-фильтр не применился")
    morning = request("GET", "/api/flights/search?" + urllib.parse.urlencode({
        "departFrom": "2026-12-02T00:00:00",
        "departTo": "2026-12-02T23:59:59",
    }))
    morning_numbers = {item["flightNumber"] for item in morning}
    if "SU200" not in morning_numbers or "SU100" in morning_numbers:
        fail(f"фильтр по времени вернул {morning_numbers}")
    ok("Criteria API: необязательные фильтры статуса, аэропортов и времени")

    flight = request("GET", "/api/flights/1")
    if flight["aircraft"]["registrationNumber"] != "RA-73001" or flight["flightNumber"] != "SU100":
        fail(f"карточка рейса {flight}")
    ok("1:N: рейс ссылается на самолёт")

    economy = request("GET", "/api/tickets?seatClass=ECONOMY")
    seed = next((item for item in economy if item["seatNumber"] == "12A"), None)
    if seed is None or seed["passenger"]["fullName"] != "Анна Соколова" or seed["flight"]["flightNumber"] != "SU100":
        fail(f"JPQL не вернул билет 12A: {economy}")
    ok("JPQL: билеты эконом-класса")

    passenger = request("GET", "/api/passengers/1")
    bonus = passenger["bonusAccount"]
    if bonus["accountNumber"] != "BA-4510123456" or bonus["tier"] != "BASIC":
        fail(f"бонусный счёт {bonus}")
    same_bonus = request("GET", "/api/passengers/1/bonus-account")
    if same_bonus["id"] != bonus["id"]:
        fail("1:1 вернул другой счёт")
    ok("1:1 пассажир — бонусный счёт")

    suffix = random.randint(100000, 999999)
    aircraft = request("POST", "/api/aircraft", {
        "registrationNumber": f"ra-{suffix}",
        "model": "Sukhoi Superjet 100",
        "seatsCapacity": 98,
    }, expected=201)
    if aircraft["registrationNumber"] != f"RA-{suffix}":
        fail(f"бортовой номер не нормализован: {aircraft}")
    ok("создание самолёта")

    created_flight = request("POST", "/api/flights", {
        "flightNumber": f"dz{suffix}",
        "departureAirport": "svo",
        "arrivalAirport": "kzn",
        "departureTime": "2026-12-20T10:00:00",
        "arrivalTime": "2026-12-20T12:00:00",
        "aircraftId": aircraft["id"],
    }, expected=201)
    if created_flight["status"] != "SCHEDULED":
        fail(f"новый рейс {created_flight}")
    if created_flight["aircraft"]["id"] != aircraft["id"]:
        fail("рейс не привязан к самолёту")
    ok("1:N: новый рейс сохраняется с выбранным самолётом")

    person = request("POST", "/api/passengers", {
        "passportNumber": f"{suffix}1234",
        "firstName": "Денис",
        "lastName": "Тестов",
        "email": f"denis{suffix}@example.com",
    }, expected=201)
    if person["bonusAccount"]["milesBalance"] != 0 or not person["bonusAccount"]["accountNumber"].startswith("BA-"):
        fail(f"бонусный счёт при регистрации {person}")
    ok("1:1: вместе с пассажиром создаётся бонусный счёт")

    base_flight = request("GET", "/api/flights/2")
    seat = f"{20 + base_flight['bookedSeats']}A"
    ticket = request("POST", "/api/tickets", {
        "passengerId": person["id"],
        "flightId": 2,
        "seatNumber": seat.lower(),
        "seatClass": "BUSINESS",
        "price": 5000,
    }, expected=201)
    if ticket["seatNumber"] != seat or ticket["milesAccrued"] != 10000 or ticket["flight"]["flightNumber"] != "SU200":
        fail(f"билет {ticket}")
    updated = request("GET", f"/api/passengers/{person['id']}")
    if updated["bonusAccount"]["milesBalance"] != 10000 or updated["bonusAccount"]["tier"] != "SILVER":
        fail(f"мили после билета {updated['bonusAccount']}")
    ok("N:M через билет: место, класс, мили и уровень счёта")

    duplicate = request("POST", "/api/tickets", {
        "passengerId": person["id"],
        "flightId": 2,
        "seatNumber": "30F",
        "seatClass": "ECONOMY",
        "price": 1000,
    }, expected=409)
    if "уже зарегистрирован" not in duplicate["message"]:
        fail(f"повторная регистрация {duplicate}")
    occupied = request("POST", "/api/tickets", {
        "passengerId": 2,
        "flightId": 1,
        "seatNumber": "12A",
        "seatClass": "ECONOMY",
        "price": 1000,
    }, expected=409)
    if "Место уже занято" not in occupied["message"]:
        fail(f"занятое место {occupied}")
    ok("ограничения билета: одно место и один пассажир на рейс")

    invalid = request("POST", "/api/aircraft", {}, expected=400)
    if invalid["message"] != "Ошибка валидации" or "model" not in invalid["fieldErrors"]:
        fail(f"валидация {invalid}")
    ok("валидация входных данных")

    print("Все проверки прошли")


if __name__ == "__main__":
    main()
