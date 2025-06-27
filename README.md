## KLIPY GIF API Android Demo Application

<p align="center">
   <img src="https://github.com/user-attachments/assets/db5e9322-f06f-4508-9ff8-9445fa2a975b" alt="Klipy iOS Demo Screenshot" width="100%">
</p>

This is a demo Android application that showcases the integration of the [Klipy API](https://klipy.com/api) for getting GIFs, Clips, and Stickers from our library. The app is a reference integration that demonstrates how to interact with our API, fetch media content, and display it in an Android application.

### Purpose
The goal of this demo is to provide developers with a simple and clear example of how to:
- Retrieve GIFs, Clips, and Stickers from the Klipy API.
- Display media content in a user-friendly UI.
- Implement basic API interactions within an Android project.

This implementation is not mandatory, but we provided it as one possible approach. Developers can use this as a starting point and modify it as needed to fit their applications.

## Table of Contents

- [Project Structure & Architecture](#project-structure--architecture)
  - [Architecture Overview](#architecture-overview)
  - [Technologies Used](#technologies-used)
- [Application Flow](#application-flow)
  - [1. Health Check - Determining Available Media Types](#1-health-check---determining-available-media-types)
  - [2. Fetching Categories & Trending Media](#2-fetching-categories--trending-media)
  - [3. Handling Media Interactions](#3-handling-media-interactions)
  - [4. Algorithm](#algorithm)
  - [5. Ads](#ads)

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

## Application Flow

The app follows a structured flow to determine available media types, fetch data, and display it in the UI. Below is the step-by-step breakdown of how the app initializes and makes API calls.

### **1. Health Check - Determining Available Media Types**
Before displaying media types in the tab selector, the app performs a health check to determine which services are operational.

#### **Health Check API Call**
- The app calls the **`healthCheck()`** method inside the **`HealthCheckService`** interface.
- This method sends a request to the `/healthCheck` endpoint.
- The response is mapped to the **`HealthCheckResponseDto`**, which contains the status of each media type.

#### **Health Check Response DTO**
The response contains the health status of **GIFs, Clips, and Stickers**, represented by `isAlive` values.

```kotlin
data class HealthCheckResponseDto(
    @SerializedName("gifs")
    val gifs: HealthCheckDataDto? = null,
    @SerializedName("clips")
    val clips: HealthCheckDataDto? = null,
    @SerializedName("stickers")
    val stickers: HealthCheckDataDto? = null,
)

data class HealthCheckDataDto(
    @SerializedName("is_alive")
    val isAlive: Boolean? = null
)
```
#### **Filtering Healthy Media Types**
- Once the response is received, the app filters out all media types where **`isAlive = false`**.
- Only the media types that are operational are displayed in the UI.
- This ensures that users only see available media services in the media type tab selector.

#### **Flow Summary**
1. **Call `healthCheck()`** → Request `/healthCheck` API.
2. **Parse Response** → Convert API response into **`HealthCheckResponseDto`**.
3. **Filter Available Media Types** → Keep only media types where **`isAlive = true`**.
4. **Update UI** → Display available media types in the tab selector.

<br>
<img src="https://github.com/user-attachments/assets/5f029f19-7488-4b11-9ac3-71353416fd1b" width="500"/>
<br>

---

### **2. Fetching Categories & Trending Media**
Once the available media types are determined through the **Health Check**, the app proceeds to fetch and display **categories** and **trending media**.

#### **Step 1: Selecting the First Available Media Type**
- After filtering out unavailable media types, the **first available media type** (GIFs, Clips, or Stickers) is automatically selected.
- The app then requests **categories** for this media type.

#### **Step 2: Fetching Categories**
- The app calls the **`getCategories()`** method inside the **`MediaService`** interface.
- This method fetches a list of **categories** for the selected media type.

##### **API Call**
```kotlin
@GET("/categories")
suspend fun getCategories(): List<CategoryDto>
```
<br>
<img src="https://github.com/user-attachments/assets/f7a19ca4-deef-40a1-bbc0-32aa05dc208c" width="500"/>
<br>

#### **Step 3: Fetching Trending Media**
- Once categories are displayed, the app automatically fetches **trending media** for the selected media type.
- The app calls the **`getTrending()`** method inside the **`MediaService`** interface.

##### **API Call**
```kotlin
@GET("/trending")
suspend fun getTrending(
    @Query("page") page: Int,
    @Query("per_page") perPage: Int
): Response<MediaItemResponseDto>
```

##### **Flow Summary**
1. **Call `getTrending()`** → Fetch trending media for the selected media type.
2. **Update UI** → Display trending media in the grid layout.

#### **Step 4: Handling Category Changes & Searching Media**
Every time the user selects a **different category**, the app makes the corresponding API calls to fetch media based on the selected category.

##### **API Methods in `MediaService`**
- **`getRecent()`** – Fetches recently uploaded media for the selected category.
- **`getTrending()`** – Fetches trending media for the selected category.
- **`search(query: String)`** – Searches media based on the user’s query or selected category (apart from recent and trending).

##### **Flow Summary**
1. **User selects a category** → Fetch media based on selected category.
2. **Call `getRecent()` or `getTrending()`** → Fetch recent/trending media.
3. **User enters a search query** → Call `search(query)`.
4. **Update UI** → Display fetched media items.

#### **Step 5: Handling Pagination**
- The **`MediaDataSourceImpl`** class is responsible for **handling pagination**.
- All API calls (`getTrending()`, `getRecent()`, `search()`) are managed through this class to ensure smooth scrolling and data loading.

##### **Key Responsibilities of `MediaDataSourceImpl`**
- Fetches data in **pages** instead of loading everything at once.
- Calls the appropriate API based on user selection (trending, recent, or search).
- Ensures seamless scrolling by appending new data when the user scrolls.

#### **Visual Flow**
1. **Select first available media type** → Fetch & display **categories**.
2. **Fetch & display trending media** for the selected category.
3. **User changes category** → Fetch recent/trending media for the new category.
4. **User searches media** → Fetch & display **search results**.
5. **Pagination (`MediaDataSourceImpl`)** → Handles pagination logic and state for fetching additional media data.

---

### **3. Handling Media Interactions**

Once a user interacts with a media item, various actions are triggered to notify the backend and update the UI accordingly.

#### **1. Sending Media (`triggerShare()`)**
When a user **selects a media item to send**, the app must notify the backend to ensure it appears in the **"Recent"** category.

##### **API Call**
```kotlin
suspend fun triggerShare(
    @Path("slug") slug: String,
    @Body request: TriggerViewRequestDto
): Response<Any>
```

##### **Flow Summary**
1. **User selects a media item** → Triggers media sharing.
2. **Call `triggerShare()`** → Notify backend that media was sent.
3. **Update "Recent" category** → The shared media item appears in **Recent**.

#### **2. Viewing a Media Item (`triggerView()`)**
When a user **long-clicks** a media item, a preview is shown. At this point, the app sends a request to the backend to inform it that the user **viewed** the media.

<br>
<img src="https://github.com/user-attachments/assets/b7ac4e39-7bf9-44c2-8aed-d637704c0291" width="250"/>
<br>

##### **Flow Summary**
1. **User long-clicks a media item** → Opens preview.
2. **Call `triggerView()`** from `MediaService` → Notify backend that media was viewed.

#### **3. Additional Actions**
While previewing a media item, the user can perform additional actions:

##### **"Send" (Same as Above)**
- This performs the same operation as `triggerShare()` and notifies the backend that the media was sent.

##### **"Hide from Recents" (`hideFromRecent()`)**
- This option is available **only if the media is in the "Recent" category**.
- If selected, the app notifies the backend to remove the media from the "Recent" list.

##### **Flow Summary**
1. **User selects "Hide from Recents"** → Triggers hide action.
2. **Call `hideFromRecent()`** → Remove media from **Recent** category.
3. **Update UI** → The media is removed from the **Recent** list.

##### **"Report" (`report()`)**
- If the user selects **"Report"**, a list of predefined **static reasons** is shown in the app.
- After choosing a reason, a request is sent to the backend.

##### **Flow Summary**
1. **User selects "Report"** → Displays list of report reasons.
2. **User chooses a reason** → Sends report request.
3. **Call `report()`** → Notify backend that media was reported.

#### **Visual Flow**
1. **User selects media** → `triggerShare()` → Media appears in **"Recent"**.
2. **User previews media** → `triggerView()` → Backend logs media view.
3. **User selects "Hide from Recents"** → `hideFromRecent()` → Media is removed from **Recent**.
4. **User reports media** → `report()` → Media is reported to the backend.

## Algorithm

The app uses **Jetpack Compose** to render media items in an adaptive and visually appealing layout **MasonryLayout**. The algorithm that arranges the media content follows a **masonry layout** approach, implemented in the **`MasonryMeasurementsCalculator`** class.

#### **Masonry Layout Algorithm (`MasonryMeasurementsCalculator`)**
- The algorithm organizes media items into rows, ensuring optimal spacing and visual balance.
- It determines the best-fit width for each media item based on its aspect ratio.
- Ads are positioned strategically within the grid, ensuring a smooth user experience.
- The method **`createRows()`** dynamically adjusts row sizes and handles updates when new items are added.
- The method **`precalculateSingleRow()`** ensures that images in a row align properly and scale proportionally.

This approach allows the UI to maintain a fluid and well-structured display, adapting seamlessly to different screen sizes.

## ADs

In order to receive the best-fit advertisements, it is crucial to pass as much relevant data as possible. The **`AdsQueryParametersInterceptor`** automatically appends important query parameters to media requests that include the `@AdsQueryParameters` annotation.

- This interceptor gathers **device information, screen measurements, advertising ID, user locale, and network details** to optimize ad selection.
- It ensures that ads are dynamically tailored to the user's environment.
- All header parameters used in this interceptor and their meanings can be found in the **[Klipy API Advertisement Documentation](https://docs.klipy.com/advertisements/receiving-an-ad)** and also within the **code comments**.
