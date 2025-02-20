## Introduction

This is a demo Android application showcasing the integration of the [Klipy API](https://klipy.com/api) for fetching **GIFs, Clips, and Stickers**. The app serves as a reference implementation, demonstrating how to interact with the API, fetch media content, and display it in an Android application.

### Purpose
The goal of this demo is to provide developers with a simple and clear example of how to:
- Retrieve GIFs, Clips, and Stickers from the Klipy API.
- Display media content in a user-friendly UI.
- Implement basic API interactions within an Android project.

### API Structure
The Klipy API provides separate endpoints for **GIFs, Clips, and Stickers**. To manage these efficiently, the app follows a structured API implementation:
- A parent interface, **`MediaService`**, defines the core API methods.
- Three specific service classes inherit from it:
    - **`GifsService`** – Handles GIF-related API calls.
    - **`ClipsService`** – Handles video clip requests.
    - **`StickersService`** – Fetches stickers from the API.

This structure allows for clean and scalable API integration.

This implementation is **not mandatory** but serves as one possible approach. Developers can use this as a starting point and modify it as needed to fit their applications.
