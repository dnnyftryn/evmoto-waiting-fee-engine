# Waiting Fee Engine

Technical assessment implementation for waiting fee calculation using Spring Boot 3 and Java 17.

## Tech Stack

- Java 17
- Spring Boot 3.5.5
- Maven
- JUnit 5
- Lombok

---

## Project Structure

```
src
├── main
│   ├── java/com/example/demo
│   │   ├── config
│   │   ├── controller
│   │   ├── domain
│   │   ├── dto
│   │   ├── service
│   │   └── util
│   └── resources
└── test
    └── java/com/example/demo
```

---

## Business Rules

### Waiting Fee

- First 5 minutes are free.
- After the free period, waiting fee is **Rp500 per started minute**.
- Waiting fee is capped at **Rp15,000**.

### Cancellation Fee

Customer cancellation:

```
Cancellation Fee =
Waiting Fee + Rp5,000
```

Maximum cancellation fee:

```
Rp20,000
```

Driver cancellation:

```
No cancellation fee
```

### GPS Pause

Waiting timer is paused whenever the driver is more than **100 meters** away from the pickup location.

Distance calculation uses the **Haversine Formula**.

---

## Run Application

```bash
./mvnw spring-boot:run
```

Application will run on:

```
http://localhost:8080
```

---

## Run Unit Tests

```bash
./mvnw test
```

---

## API

### Preview Waiting Fee

```
POST /v1/orders/{orderId}/fee-preview
```

Example:

```http
POST /v1/orders/ORD-88213/fee-preview
Content-Type: application/json
```

Request

```json
{
  "arrivedAt": "2026-08-10T09:00:00+07:00",
  "endedAt": "2026-08-10T09:21:40+07:00",
  "endReason": "CANCELLED_BY_CUSTOMER",
  "pickupPoint": {
    "lat": -6.21462,
    "lng": 106.84513
  },
  "driverPings": [
    {
      "at": "2026-08-10T09:00:00+07:00",
      "lat": -6.21462,
      "lng": 106.84513
    },
    {
      "at": "2026-08-10T09:08:00+07:00",
      "lat": -6.21980,
      "lng": 106.85110
    },
    {
      "at": "2026-08-10T09:14:00+07:00",
      "lat": -6.21470,
      "lng": 106.84520
    }
  ]
}
```

Response

```json
{
  "feeBreakdown": {
    "effectiveWaitingSeconds": 940,
    "pausedSeconds": 360,
    "chargeableMinutes": 11,
    "waitingFee": 5500,
    "cancellationFee": 10500,
    "totalFee": 10500,
    "waitingFeeCapped": false,
    "cancellationFeeCapped": false,
    "totalWaitingSeconds": 1300
  }
}
```

---

## Test Coverage

The project includes unit tests covering:

- Free waiting period
- Waiting fee calculation
- Minute rounding
- Waiting fee cap
- Customer cancellation fee
- Driver cancellation
- Cancellation fee cap
- GPS pause calculation

Total:

- 12 WaitingFeeCalculator unit tests
- 1 Spring Boot context test

```
Tests run: 13
Failures: 0
Errors: 0
BUILD SUCCESS
```

---

## Notes

- Waiting fee calculation is implemented in `WaitingFeeCalculator`.
- Distance calculation uses the Haversine Formula in `DistanceUtil`.
- Business rule constants are centralized in `FeeConstants`.