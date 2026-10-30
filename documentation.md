# API Documentation

## Overview

This API is part of a Java application built with Javalin. It provides client authentication, registration, and quiz video generation.

The application runs on port `7070`.

## Base URL

    http://localhost:7070

## Authentication

The application supports two authentication methods:

- Form-based authentication using cookies.
- JSON-based authentication using JWT tokens.

Passwords are hashed using BCrypt.

### Authentication Cookie

When a client logs in through the website, the server creates an authentication cookie.

| Property | Value |
|---|---|
| Cookie name | `auth` |
| HTTP-only | `true` |
| SameSite | `Strict` |
| Max age | `3600` seconds |

### JWT Authentication

For API requests, JWT authentication can be provided through the `Authorization` header:

    Authorization: Bearer <token>

The JWT can also be provided through the `auth` cookie.

JWT configuration is defined in `config.properties`:

- `ISSUER`
- `TOKEN_EXPIRE_TIME`
- `SECRET_KEY`

The local token expiration time is configured as `1800000` milliseconds.

## User Roles

The application supports three roles:

| Role | Description |
|---|---|
| `ANYONE` | Accessible without authentication. |
| `STANDARD` | Accessible to authenticated standard and premium clients. |
| `PREMIUM` | Accessible only to premium clients. |

A premium client can also access routes requiring the `STANDARD` role.

---

# Endpoints

## 1. Website Routes

### GET /

Returns the application's main page.

**Method:** `GET`

**Endpoint:**

    /

### GET /client/login

Displays the client login page.

**Method:** `GET`

**Endpoint:**

    /client/login

### GET /client/register

Displays the client registration page.

**Method:** `GET`

**Endpoint:**

    /client/register

---

## 2. Client Authentication

These endpoints use form data and are intended for the website.

### POST /client/login

Authenticates a client using form data.

**Method:** `POST`

**Endpoint:**

    /client/login

**Content-Type:**

    application/x-www-form-urlencoded

**Request parameters:**

| Parameter | Type | Description |
|---|---|---|
| `clientName` | String | The client's username. |
| `clientPassword` | String | The client's password. |

**Example request:**

    POST /client/login
    Content-Type: application/x-www-form-urlencoded

    clientName=testuser&clientPassword=password123

If authentication succeeds, the server creates an authentication cookie.

### POST /client/register

Registers a new client using form data.

**Method:** `POST`

**Endpoint:**

    /client/register

**Content-Type:**

    application/x-www-form-urlencoded

**Request parameters:**

| Parameter | Type | Description |
|---|---|---|
| `clientName` | String | The desired username. |
| `clientPassword` | String | The desired password. |

**Example request:**

    POST /client/register
    Content-Type: application/x-www-form-urlencoded

    clientName=testuser&clientPassword=password123

The form-based registration implementation:

- Converts usernames to lowercase.
- Limits usernames to 15 characters.
- Limits passwords to 20 characters.
- Rejects usernames that already exist.

### POST /client/logout

Logs out the currently authenticated client.

**Method:** `POST`

**Endpoint:**

    /client/logout

The endpoint removes the authentication session by clearing the authentication cookie.

---

## 3. JSON Authentication

These endpoints are intended for clients that communicate with the application through JSON.

### GET /auth/login

Provides the login endpoint information.

**Method:** `GET`

**Endpoint:**

    /auth/login

### POST /auth/login

Authenticates a client using JSON data.

**Method:** `POST`

**Endpoint:**

    /auth/login

**Content-Type:**

    application/json

**Request body:**

    {
      "clientName": "testuser",
      "clientPassword": "password123"
    }

**ClientDTO:**

| Field | Type | Description |
|---|---|---|
| `clientName` | String | The client's username. |
| `clientPassword` | String | The client's password. |

If authentication succeeds, the server returns authentication information, including a JWT token, the client name, and the client's role.

### GET /auth/register

Provides the registration endpoint information.

**Method:** `GET`

**Endpoint:**

    /auth/register

### POST /auth/register

Registers a new client using JSON data.

**Method:** `POST`

**Endpoint:**

    /auth/register

**Content-Type:**

    application/json

**Request body:**

    {
      "clientName": "testuser",
      "clientPassword": "password123"
    }

The JSON-based registration implementation:

- Converts usernames to lowercase.
- Requires usernames to contain between 3 and 50 characters.
- Allows letters, numbers, dots, underscores, and hyphens in usernames.
- Requires passwords to contain at least 8 characters.
- Rejects usernames that already exist.

A successful registration returns HTTP status `201 Created`.

---

## 4. Video Generation

### POST /video/generate-video

Generates a quiz video using external services.

**Method:** `POST`

**Endpoint:**

    /video/generate-video

**Required role:** `STANDARD`

**Content-Type:**

    application/x-www-form-urlencoded

**Request parameters:**

| Parameter | Type | Description |
|---|---|---|
| `category` | String | The category used to generate the quiz question. |

**Example request:**

    POST /video/generate-video
    Content-Type: application/x-www-form-urlencoded
    Authorization: Bearer <token>

    category=Science

**Response:**

The endpoint returns the generated video as binary MP4 data.

**Content-Type:**

    video/mp4

### Video Generation Process

The video generation process consists of several steps:

1. The client submits a category.
2. The application sends a request to Ollama to generate a quiz question and answer.
3. The application processes the generated question and answer.
4. The question and answer are sent to the Kokoro text-to-speech service.
5. The application downloads the required images and audio resources.
6. Text overlays are generated for the question, countdown, and answer.
7. The application sends the required files and instructions to the FFmpeg service.
8. The FFmpeg service creates the final video.
9. The generated MP4 video is returned to the client.

### Video Specifications

| Property | Value |
|---|---|
| Resolution | 1080 × 1920 |
| Orientation | Vertical |
| Frame rate | 25 FPS |
| Question duration | 6 seconds |
| Countdown duration | 5 seconds |
| Answer duration | 4 seconds |
| Total duration | 15 seconds |
| Video codec | H.264 |
| Audio codec | AAC |
| Audio bitrate | 192 kbps |

### Required Video Files

The application sends eight files to the FFmpeg service:

| File | Description |
|---|---|
| Background image | The background image used in the video. |
| Question voice | Audio containing the generated question. |
| Countdown sound | Sound used during the countdown. |
| Answer voice | Audio containing the answer. |
| Background music | Music played in the background. |
| Question overlay | Text overlay containing the question. |
| Countdown overlay | Visual countdown overlay. |
| Answer overlay | Text overlay containing the answer. |

---

## 5. Video CRUD Endpoints

The following endpoints are registered in the application, but their handlers are currently empty.

They are therefore not implemented as functional CRUD operations.

### GET /video/read

**Method:** `GET`

**Endpoint:**

    /video/read

**Status:** Registered, but not implemented.

### PUT /video/update

**Method:** `PUT`

**Endpoint:**

    /video/update

**Status:** Registered, but not implemented.

### DELETE /video/delete

**Method:** `DELETE`

**Endpoint:**

    /video/delete

**Status:** Registered, but not implemented.

---

# External Services

The application communicates with several external services to generate videos.

## 1. Ollama

Ollama is used to generate quiz questions and answers.

**Endpoint:**

    POST http://192.168.0.45:11434/api/generate

**Request body:**

    {
      "model": "qwen2.5:3b",
      "prompt": "<generated prompt>",
      "stream": false
    }

**Response format:**

The application expects a response containing a `response` string.

The generated text is parsed to extract:

- The quiz question.
- The answer.
- The available options.

## 2. Kokoro Text-to-Speech

Kokoro converts the generated question and answer into audio.

**Endpoint:**

    POST http://192.168.0.35:5001/tts

**Request body:**

    {
      "text": "Example text to convert into speech."
    }

**Response:**

Raw audio bytes in WAV format.

## 3. Resource Server

The resource server provides images and audio files used during video generation.

**Base URL:**

    http://192.168.0.68:7070

**Available resources:**

| Resource | Endpoint |
|---|---|
| Background image | `GET /images/27.jpg` |
| Countdown sound | `GET /sounds/countdown.wav` |
| Background music | `GET /sounds/backgroundmusic.wav` |

The application also contains resource service methods for retrieving image, background music, countdown, and whoosh resources.

## 4. FFmpeg Video Service

The FFmpeg service combines the supplied files and instructions to create the final video.

**Endpoint:**

    POST http://192.168.0.87:7070/create

**Request body:**

The application sends a JSON `VideoRequest` containing a list of files and video instructions.

Each file is represented by a `VideoFile` object:

| Field | Type | Description |
|---|---|---|
| `name` | String | Name of the file. |
| `type` | String | File type. |
| `fileName` | String | Original filename. |
| `data` | byte array | File content encoded as Base64 in JSON. |

The `VideoRequest` contains:

| Field | Type | Description |
|---|---|---|
| `files` | List of VideoFile | Files required to generate the video. |
| `instructions` | VideoInstruction | Instructions for creating the video. |

**Response:**

The FFmpeg service returns the generated video as raw binary data. The application returns this data to the client as an MP4 response.

---

# Data Transfer Objects

The application uses the following DTOs to transfer data between services and application components.

## ClientDTO

| Field | Type | Description |
|---|---|---|
| `clientName` | String | Client username. |
| `clientPassword` | String | Client password. |

## OllamaDTO

| Field | Type | Description |
|---|---|---|
| `response` | String | Generated text returned by Ollama. |

## KokoroDTO

| Field | Type | Description |
|---|---|---|
| `wav` | byte array | Audio data returned by Kokoro. |

## ResourceDTO

| Field | Type | Description |
|---|---|---|
| `file` | byte array | Downloaded resource data. |

## VideoDTO

| Field | Type | Description |
|---|---|---|
| `mp4` | byte array | Generated MP4 video data. |

## VideoRequest

| Field | Type | Description |
|---|---|---|
| `files` | List of VideoFile | Files used to generate the video. |
| `instructions` | VideoInstruction | Video generation instructions. |

## VideoFile

| Field | Type | Description |
|---|---|---|
| `name` | String | File name or identifier. |
| `type` | String | File type. |
| `fileName` | String | Actual filename. |
| `data` | byte array | File data. |

---

# Validation

The application has different validation rules for form-based and JSON-based registration.

## Form-Based Registration

| Field | Rule |
|---|---|
| Username | Converted to lowercase. |
| Username | Maximum 15 characters. |
| Password | Maximum 20 characters. |
| Username | Must not already exist. |

## JSON-Based Registration

| Field | Rule |
|---|---|
| Username | Converted to lowercase. |
| Username | Between 3 and 50 characters. |
| Username | Letters, numbers, dots, underscores, and hyphens are allowed. |
| Password | Minimum 8 characters. |
| Username | Must not already exist. |

---

# HTTP Status Codes

The application uses HTTP status codes to communicate the result of requests.

| Status Code | Meaning |
|---|---|
| `200 OK` | Request completed successfully. |
| `201 Created` | A new client was successfully registered. |
| `400 Bad Request` | Request data is invalid. |
| `401 Unauthorized` | Authentication is missing or invalid. |
| `403 Forbidden` | The authenticated client does not have the required role. |
| `404 Not Found` | The requested endpoint or resource does not exist. |
| `500 Internal Server Error` | An unexpected server error occurred. |

The exact status code for individual validation and service failures depends on the relevant handler and exception handling.

---

# Notes

- The application uses Javalin as its web framework.
- The application listens on port `7070`.
- Passwords are hashed using BCrypt.
- JWT is used for API authentication.
- Authentication cookies are HTTP-only and use `SameSite=Strict`.
- Video generation requires Ollama, Kokoro, the resource server, and the FFmpeg service to be reachable.
- The video CRUD endpoints are registered but not implemented.
- External service addresses are currently configured as local network IP addresses.