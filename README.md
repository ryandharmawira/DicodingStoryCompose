# Dicoding Story With Jetpack Compose

## Introduction
This project is an example to review my knowledge about Jetpack Compose, shows how to do the
following:
* Create custom composable components and reuse them
* Handle network calls using Retrofit
* Handle JWT session token after authenticated using Preferences DataStore
* Handle multiple runtime permissions such as camera and location
* Use Google Maps SDK
* Handle Dependency Injection using Dagger Hilt 
* Apply Clean Architecture MVVM (Model-View-ViewModel) pattern

## Use Cases
This project has several use cases such as:
* Authentication 
  * Login
  * Registration
* Main
  * Home - shows lists of stories
  * Add Story - users can add/capture a photo, write a description and upload it.
  * Maps - shows Google Maps and marks stories location
  * About - shows a dummy page

## Images
![Registration Screen](/image/register.jpg "Registration Screen")
![Login Screen](/image/login.jpg "Login Screen")
![Navigation Drawer](/image/login.jpg "Navigation Drawer")
![Home Screen in Light Mode](/image/home_light.jpg "Home Screen in Light Mode")
![Home Screen in Dark Mode](/image/home_dark.jpg "Home Screen in Dark Mode")

## Dependency
* Navigation Compose
* Retrofit and OkHttp
* Preferences DataStore
* Dagger Hilt
* Coil Compose
* CameraX
* Compressor by zetbaitsu
* Google Location Services, Maps and Maps Compose

# Google Maps SDK for Android Setup
1. You'll need a billing account. Create one here https://console.cloud.google.com/billing.
2. Create a new project in cloud console https://console.cloud.google.com/. Once its created switch to that project.
3. Go to marketplace and enable google maps SDK for the new project https://console.cloud.google.com/marketplace.
    - "Maps SDK for Android"
    - Enable billing through the billing account your created
4. Enable google maps in cloud console
    - Specify "Android apps" and generate a SHA1
5. Add your API key to `local.properties`. **Note:** To reference the API key in `local.properties` you'll need the secrets plugin installed. See my [build.gradle (project)](https://github.com/ryandharmawira/DicodingStoryCompose/blob/master/build.gradle.kts). It's the `com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1` dependency.
```
# local.properties
GOOGLE_MAPS_API_KEY=<YOUR_KEY>
```
6. Update your `AndroidManifest.xml` to include the API key. Within the `application` tag.
```
<application
   ...
   ... >
   <meta-data
	  android:name="com.google.android.geo.API_KEY"
	  android:value="${GOOGLE_MAPS_API_KEY}"/>
</application>
```