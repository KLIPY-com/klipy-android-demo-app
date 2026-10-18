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
- Production returns `data.locale` and `data.categories` records with `category`,
  `query`, and `preview_url`. The cached demo key still returns `data` as a string
  list. `CategoriesMapper` handles these two verified formats at the response
  boundary, preserving the production query separately from its display label.
- The picker prepends its local Recent and Trending entries. Production preview
  images use `preview_url`; cached demo categories retain their legacy image URLs.
- Initial loading selects Trending and retains that query for pagination. Choosing
  a category uses its `query`, while typed search uses the entered text.

##### **API Call**
```kotlin
@GET("/categories")
suspend fun getCategories(): Response<CategoriesResponseDto>
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

The `AdsQueryParametersInterceptor` adds rendering dimensions, locale, device and
network metadata to requests annotated with `@AdsQueryParameters`. Minimize this
data according to the backend contract; do not add identifiers for targeting.

The security update removes advertising-ID collection and its dependency, phone
state permission, cleartext traffic, and automatic HTTP request logging. It
strips these retired parameters from annotated requests, even if supplied by a
caller: `ad-ifa`, `ad-didsha1`, `ad-didmd5`, `ad-dpidsha1`, `ad-dpidmd5`,
`ad-macsha1`, `ad-macmd5`, `ad-yob`, `ad-gender`, `ad-app-version`, `ad-position`.

`customer_id` is a random UUID generated on first use and persisted in the
private `klipy_installation` preferences file. Feeds, search, Recents, share/view,
report, and hide all use this same ID. An annotated request replaces any existing
`customer_id` query values instead of appending another one. No Android ID or
advertising ID is read. The initial write must succeed before the ID is returned.

The ID survives restarts and app updates. Clearing app data or a fresh reinstall
creates a new ID; the preferences file is excluded from cloud backup and device
transfer on both legacy and Android 12+ backup configurations. Existing Recents
under the previous Android ID are not migrated or linked to the new UUID. This
client change does not delete historical backend identifiers or verify other
partner clients' data handling.

The dependency catalog now targets **ads-android 1.1.4**, an unpublished security
release candidate. Its renderer removes native JavaScript bridges and custom
click tracking. Normal HTTPS links and supported new-window links require a
completed native interaction plus Chromium gesture evidence; supported Play
details links become HTTPS. Both main-frame and popup requests must match the
actual link selected by native hit testing. A popup's first validated destination
can open, but its content cannot execute or load in the SDK. JavaScript-only
navigation buttons or substituted URLs remain unsupported in either path.
Automatic navigation, blank-window
redirect flows, raw intents, HTTP and other custom schemes remain blocked.
JavaScript remains enabled for rendering. See the SDK README for the full
compatibility contract and partner migration notes.

### Local candidate verification

Until 1.1.4 is published, build the SDK release AAR and supply its absolute path:

```sh
# From the ads-android repository:
./gradlew :ads-android:assembleRelease
# From this repository:
./gradlew :app:testDebugUnitTest :app:lintRelease :app:assembleDebug \
  :app:assembleDebugAndroidTest -PklipyAdsAar=/absolute/path/to/ads-android-release.aar
ANDROID_SERIAL=<test-emulator> ./gradlew :app:connectedDebugAndroidTest \
  -PklipyAdsAar=/absolute/path/to/ads-android-release.aar
```

For a production-placement QA build, set `KLIPY_API_KEY` in the local build
environment. The value is compiled into the APK; do not commit the key or share
that QA artifact publicly. With the variable absent or empty, the existing cached
demo feed is used. Production targeting must be tested using the production key
and actual Android rendering parameters; cached demo ads can target other devices.

The AAR override is command-local. Without it, the build requires the published
1.1.4 artifact; it must not silently use the vulnerable 1.1.3 release.
Request tests verify retired parameter removal without making network calls.
`AdMediaItemIntegrationTest` exercises the actual Compose/WebView boundary,
including render, deliberate `_self`/`_blank` taps, removal and reopening, using an offline fixture
and intercepted native launches. The SDK owns the malicious-creative suite.

Historical verification on 2026-10-16: all 3 request tests plus the existing unit test
passed; debug APK/test APK builds and release lint passed (0 errors, 43 warnings,
including the unresolved Android-ID warning). The Compose integration test passed
on API 37 / WebView 153.0.8010.36 against the local candidate AAR. The merged
release manifest has cleartext disabled and neither phone-state nor advertising-ID
permissions; the release dependency tree has no advertising-ID dependency.
The debug APK was verified with the local Android debug certificate and has
SHA-256 `a8a813a31f331fa47248a2dfec65a13b5e6aaf8cda1377dd48572335c464635c`.
This is a local QA build, not a Play upload or a test of the published artifact.

### UUID update verification — 2026-10-18

- JVM tests: 9 passed, including real Retrofit request serialization for GIF,
  Clip, and Sticker feeds/search/Recents/share/view/report/hide with one UUID.
- Samsung SM-S921U1, Android 16 / API 36, WebView 154.0.8037.106: 5 identity
  instrumentation tests and the existing Compose ad integration test passed.
  Identity tests cover recreation, reset of isolated test storage, concurrent
  first use, persistence failure, and both backup-exclusion configurations.
- The installed UUID remained unchanged through an app update and process restart
  on the Samsung. GIFs, Clips, and Stickers now load categories, Trending media,
  and ads through the normal picker opening flow, without the search workaround.
- Debug APK/test APK builds and release lint passed: 0 errors, 44 warnings.
  The Android-ID warning is gone. The first UUID uses synchronous `commit()`
  intentionally so a failed initial write cannot return a temporary identity.
- Production QA debug APK SHA-256:
  `9d201320b0a31af168a00acb9a3d9d2d50f21a436c6f42c305a380ff707ef47f`.
  Its local debug signer SHA-256 is
  `22976ddf08afb895328e44769138bb42790d7836a790a6b490b09861b9bc959c`.
- The new production-placement key returned HTTP 200 for all three media feeds
  (20 items and 2 ads each), category reads, and a Recents read with the new UUID.
  The key is supplied only through the local build environment.
- Production categories returned `data: {locale, categories: [{category, query,
  preview_url}]}` while the cached demo still returned a string list. The mapper
  now supports both observed shapes. Regression coverage verifies initial
  Trending, Recents, category queries that differ from labels, search, caching,
  empty results, and category failures without fabricating missing fields.
- A live production-search swipe/tap check did not reproduce the earlier
  touch-start InMobi pattern and recorded no external activity starts. Different
  creatives were served; the original scroll-tracking issue is not resolved by
  this UUID change. Live WebView capture is partial, not a full network audit.

### Click-through update verification — 2026-10-18

- The SDK now supports native-verified `_blank` links and matching popup click
  handlers. A popup must match the actual anchor selected by native hit testing;
  JavaScript-only popup buttons, substituted URLs and blank-window flows remain
  unsupported. A completed tap and Chromium gesture evidence are both required.
- On Samsung SM-S921U1 / Android 16 / WebView 154.0.8037.106, all 12 SDK device
  tests and all 7 demo device tests passed. The demo's owning Compose test now
  exercises `_blank` click-through across removal and reopening. All 3 SDK and
  9 demo JVM tests passed. SDK/demo builds passed; lint had 0 errors (3 SDK and
  44 demo warnings). The existing installation UUID was unchanged by the update.
- Candidate AAR SHA-256:
  `1e161ccb14f2996a2e1b48d5f02256a44e8f03febce7f4ec3bc735a821ca7376`.
  Production QA debug APK SHA-256:
  `844ada4b0b5e37af6f91b915d89b2186f1175b3eb118268b7fa9a611996c7b5b`.
  Signer remains the local Android debug certificate recorded above.
- A live banner's exact `ad-iframe=1` creative URL was compared in the demo and
  Samsung Chrome. Both intentional taps reached the identical advertiser landing
  URL; the preceding demo swipe caused no external launch. Curl returned 200
  for the creative URL. Only that sample establishes browser/WebView parity.
- **Remaining production issue:** a fresh InMobi “Golden Whiskers Slot” creative
  sent its two declared `CLICK` trackers on a swipe, receiving HTTP 204 and 200,
  and attempted a `market:` popup with `userGesture=false`. The SDK blocked the
  external launch; creative-owned tracking still happened. The production key
  was in use. The app was stopped after capture to end tracking retries. This
  fix does not resolve that creative's scroll tracking or establish billed clicks.
- The updated navigation path still needs minimum-API, accessibility and actual
  partner-keyboard verification before publication. Changes remain unpublished.

### Main-frame destination binding verification — 2026-10-18

- The rebuilt demo uses an SDK candidate that applies the native-selected link
  check to both main-frame and popup requests. A completed tap and Chromium's
  gesture flag alone are insufficient: live testing had shown an independent
  timer could reuse a non-link tap through the old main-frame path.
- The regression failed before the fix. All 12 SDK and 7 demo device tests now
  pass on Samsung SM-S921U1 / Android 16 / WebView 154.0.8037.106. The Compose ad
  test covers both `_self` and `_blank` across removal/reopening. SDK/demo builds
  and lint passed, with 0 errors and 3/44 warnings respectively.
- Live testing of the installed demo confirmed that timers armed before a real
  tap on an inert area no longer launch, whether they request main-frame or
  popup navigation. Substituted destinations, rewritten anchors, JavaScript-only
  buttons, raw intents and HTTP links stayed blocked. JavaScript navigation
  without a matching real anchor is intentionally unsupported in both paths.
- A fresh production Chumba banner opened Chrome. Controlled same-window HTTPS
  and Play Store links opened their actual Android handlers once each; swiping
  the store link launched nothing. The crash buffer was empty. Runtime probes
  were temporary; no test content or debugging bridge was added to the app.
- Candidate AAR SHA-256:
  `3f979a83fd31802570c578cf648328ccfa3a289d5295770449831acddf33c456`.
  Installed production-placement QA debug APK SHA-256:
  `b7d03ecddfd84fa0173dc5b985673b9d501705454ee2f4411b18541bbee2a238`.
  Its signer remains the local Android debug certificate recorded above.
- This fix does not address the creative-owned InMobi scroll click tracking
  recorded above. Minimum-version, accessibility and partner-keyboard validation
  remain pending. The SDK candidate remains unpublished.

### Smoke Tests

1. Use a QA emulator and test API account. Build against the exact candidate AAR.
   Verify the APK signer with `apksigner verify --verbose --print-certs`, inspect
   package/version with `aapt dump badging`, and record `shasum -a 256` for both
   AAR and APK before installing. Use the selected emulator serial explicitly.
2. Inspect the merged release manifest: `usesCleartextTraffic=false`; no
   `READ_PHONE_STATE` or advertising-ID permission. Confirm the release runtime
   dependency tree contains no advertising-ID collection dependency.
3. Run the request and Compose integration tests above, then the SDK's security
   suite. No production ad impressions/clicks should be used for these tests.
4. Open KLIPY conversation → media picker, first with the cached demo key and
   then with a local production-placement key. Each media tab must show Recent,
   Trending, and fetched category previews; Trending loads automatically. Select
   a category and confirm its query drives results, then test typed search,
   clearing search, switching tabs, and pagination. Test offline/failed category
   requests and empty feeds: no crash or repeated request with a blank filter.
   Verify a controlled ad fits its grid cell.
   Leave it idle: no external launch. Tap an ordinary HTTPS link, a `_blank` link,
   or a supported click-time `window.open` CTA: one launch. Swipe across the ad or
   cancel the touch: no launch. Return, close and reopen the picker, and repeat.
5. Use controlled creatives for popups, timers, nested frames, forged bridge
   calls/messages, raw intents and invalid URLs. Expect no automatic external
   launch and no fallback for unsupported links. Test main-frame redirects and
   popups independently: schedule a timer before touching a non-clickable area,
   then tap it before the timer fires. Neither path may open a destination. Also
   check a click handler that changes its anchor or navigates to an unrelated URL;
   both must be blocked while matching real anchor links still work. Check network capture for
   retired ad fields and cleartext requests; inspect release logs for identifiers.
   For separately authorized live testing, save a returned `ad-iframe=1` creative
   URL privately and compare that same creative/CTA in the Samsung browser and
   the demo. Record idle, swipe and tap independently, including the actual ad
   and destination served in each surface. A browser-only success is a remaining
   compatibility gap. Do not log full creative URLs or the production key in
   source/release logs. Native launch blocking does not prevent a creative from
   sending its own click-tracking requests; inspect those separately.
6. Run `CustomerIdIntegrationTest` and `MediaCustomerIdIntegrationTest`. On a
   disposable QA installation, open the picker, restart the app, and confirm the
   stored UUID and outgoing `customer_id` remain unchanged. Check feeds, search,
   Recents, share/view, report, and hide requests use that same ID. Clear only the
   disposable installation's data and reopen: expect a new UUID and fresh Recents.
   Verify both backup XML files exclude `klipy_installation.xml`; restore/cloud
   and device-transfer smoke tests must not restore the prior installation ID.
   With disposable QA content, verify share then Recents, hide then Recents, and
   report/view behavior against the backend. Automated tests intercept these
   operations; they do not create live reports, shares, or deletes.
7. Before distribution, verify a published 1.1.4 build without the local override,
   and repeat with a representative partner keyboard host. Do not roll back to
   the earlier unsafe click bridge if a network creative is incompatible.

Passing these tests demonstrates specific safeguards, not Google Play approval
or a completed review of backend data processing and other SDKs.
