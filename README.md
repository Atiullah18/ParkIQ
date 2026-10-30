# ParkIQ – Smart Parking Application

## 1. Project Overview

**Project Title:** Smart Parking Management System  
**Application Name:** ParkIQ  
**Platform:** Android  
**Development Language:** Kotlin  

ParkIQ is a smart parking application designed to help users find, explore, and reserve parking spaces easily. It provides parking information, slot booking, booking management, parking rental services, and an AI-powered assistant.

## 2. Objective

The main objective of ParkIQ is to simplify parking management by connecting vehicle owners with available parking spaces.

The application helps users:
- Find nearby parking spaces.
- Explore parking locations and prices.
- Select parking slots.
- Book parking spaces for a selected date and time.
- Manage current, upcoming, and past bookings.
- Get assistance through an AI-powered chatbot.
- Allow parking owners to list their available spaces for rent.

## 3. Concept and Technology

ParkIQ uses Android application development technologies to provide a user-friendly parking experience.

### Technologies Used

- **Kotlin:** Main programming language.
- **Android Studio:** Development environment.
- **Jetpack Compose:** User interface development.
- **Firebase AI Logic:** AI assistant integration.
- **Google Maps Platform:** Location and map-related functionality.
- **Google Places API:** Place search and suggestions.
- **Gradle:** Project build and dependency management.

## 4. Activities

### Activity 1 – Smart Parking Application Development

This activity focuses on designing and developing the ParkIQ Android application.

**Main Features:**
1. Home screen with parking information.
2. Explore Parking with search and filters.
3. Parking details and slot selection.
4. Date and time selection.
5. Booking confirmation and payment flow.
6. My Bookings with Active, Upcoming, and Past sections.
7. Profile management and Saved Parking.
8. Parking rental section for parking owners.

### Activity 2 – AI Integration in ParkIQ

This activity focuses on integrating an AI-powered assistant into the ParkIQ application.

**AI Feature:** ParkIQ AI Assistant

The AI Assistant helps users with parking-related queries, bookings, and parking-owner services.

**How It Works:**
1. The user opens the AI Assistant.
2. The user enters a parking-related question.
3. The question is sent to Firebase AI Logic.
4. The Gemini model processes the question.
5. The generated response is displayed in the chat interface.

## 5. Scenario Demonstration

Consider a user travelling to a location and looking for a parking space.

1. The user opens the ParkIQ application.
2. The user explores available parking spaces.
3. The user selects a parking location.
4. The user chooses a parking slot, date, and time.
5. The user proceeds through the booking and payment screens.
6. The booking confirmation is displayed.
7. The user can view the reservation under My Bookings.
8. The user can ask the AI Assistant for parking-related help.

## 6. Project Folder Structure

```text
ParkIQ/
│
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/example/parkiq/
│   │       ├── res/
│   │       └── AndroidManifest.xml
│   │
│   └── build.gradle.kts
│
├── gradle/
├── Screenshots/
│   ├── 01_Home.png
│   ├── 05_My_Bookings.png
│   ├── 07_Profile.png
│   ├── 08_AI_Assistant.png
│   ├── 09_Explore_Parking.png
│   ├── 10_My_Bookings_Past.png
│   └── 11_Booking_Confirmed.png
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
