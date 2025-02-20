## Introduction

This is a demo Android application showcasing the integration of the [Klipy API](https://klipy.com/api) for fetching **GIFs, Clips, and Stickers**. The app serves as a reference implementation, demonstrating how to interact with the API, fetch media content, and display it in an Android application.

### Purpose
The goal of this demo is to provide developers with a simple and clear example of how to:
- Retrieve GIFs, Clips, and Stickers from the Klipy API.
- Display media content in a user-friendly UI.
- Implement basic API interactions within an Android project.

This implementation is **not mandatory** but serves as one possible approach. Developers can use this as a starting point and modify it as needed to fit their applications.

## Project Structure & Architecture

This demo app follows the **MVVM (Model-View-ViewModel) architecture** with the **Repository Pattern** to ensure a clean and maintainable codebase. It also adopts **ViewState** for state management and separates concerns using a **Presentation/Domain/Data** layer approach.

### **Architecture Overview**
The project is structured into three main layers:

- **Presentation Layer**: Handles UI and user interactions.
  - Built with **Jetpack Compose** for modern UI development.
  - Uses **ViewModel** to manage UI-related data.
  - Implements **ViewState** to handle UI state efficiently.

- **Domain Layer**: Business logic layer.
  - Defines repository interface and domain models
  - Ensures separation between UI and data sources.

- **Data Layer**: Manages API calls and data storage.
  - Uses **Retrofit** for network requests.
  - Implements **Repository Pattern** to manage data fetching.
  - Supports **Coroutines** for asynchronous tasks.


### **Technologies Used**
- **Jetpack Compose** – For modern UI development.
- **Retrofit** – For API communication.
- **Coroutines** – For asynchronous operations.
- **MVVM + ViewState** – For structured UI and state management.
- **Repository Pattern** – For data handling and separation of concerns.
- **Coil** - For image/GIF processing
- **ExoPlayer** - For displaying Clips

## Features

This demo app demonstrates the core functionalities of integrating the **Klipy API** for GIFs, Clips, and Stickers. Below are the key features:

### **1. Fetch GIFs, Clips, and Stickers along with their categories**
The app retrieves media content from the **Klipy API**, where each media type (GIFs, Clips, Stickers) has its own API service implementation.
- A parent Retrofit interface, **`MediaService`**, defines the core API methods.
- Each media type has its own service that extends `MediaService`:
  - **`GifsService`** – Fetches GIFs.
  - **`ClipsService`** – Fetches video clips.
  - **`StickersService`** – Fetches stickers.

These services are responsible for making network calls and handling API responses efficiently.

### **2. Display results in a user-friendly UI (Jetpack Compose)**
The app uses **Jetpack Compose** to render media items in an adaptive and visually appealing layout **MasonryLayout**. The algorithm that arranges the media content follows a **masonry layout** approach, implemented in the **`MasonryMeasurementsCalculator`** class.

#### **Masonry Layout Algorithm (`MasonryMeasurementsCalculator`)**
- The algorithm organizes media items into rows, ensuring optimal spacing and visual balance.
- It determines the best-fit width for each media item based on its aspect ratio.
- Ads are positioned strategically within the grid, ensuring a smooth user experience.
- The method **`createRows()`** dynamically adjusts row sizes and handles updates when new items are added.
- The method **`precalculateSingleRow()`** ensures that images in a row align properly and scale proportionally.

This approach allows the UI to maintain a fluid and well-structured display, adapting seamlessly to different screen sizes.

### **3. Ads Query Parameters Interceptor**
In order to receive the best-fit advertisements, it is crucial to pass as much relevant data as possible. The **`AdsQueryParametersInterceptor`** automatically appends important query parameters to media requests that include the `@AdsQueryParameters` annotation.

- This interceptor gathers **device information, screen measurements, advertising ID, user locale, and network details** to optimize ad selection.
- It ensures that ads are dynamically tailored to the user's environment.
- All header parameters used in this interceptor and their meanings can be found in the **[Klipy API Advertisement Documentation](https://docs.klipy.com/advertisements/receiving-an-ad)** and also within the **code comments**.

### **4. Additional Features**
The app also includes:
- **Categories** – Organizes media into different sections.
- **Search Functionality** – Allows users to find specific media content.
- **Send Media** – Enables sharing of media items.
- **Report Feature** – Allows users to report inappropriate content.
- **Service Health Check (`/healthCheck`)** – Before displaying media types (GIFs, Clips, Stickers) in the selector tabs, the app calls the **`/healthCheck`** endpoint to determine which services are operational. Only media types that are marked as **healthy** will be shown in the UI.

These features are implemented in their respective classes and can be explored within the codebase for further details.
