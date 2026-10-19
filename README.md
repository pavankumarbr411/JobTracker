# JobTracker — Android Job Application Tracker

A native Android application built with Java and XML to help job seekers organize and track their job applications in one place.

## Overview

JobTracker helps users record job opportunities, manage application statuses, and quickly find saved applications through search and filtering.

## Features

- **Add applications:** Save company name, job title, location, application status, and job posting URL.
- **Edit applications:** Update saved application details and statuses.
- **Delete applications:** Remove applications with a confirmation dialog.
- **Dashboard:** View total applications and interview counts.
- **Search:** Find applications by company name or job title.
- **Status filtering:** Filter applications by Applied, Assessment, Interview, Offer, or Rejected.
- **Local persistence:** Store application data on the device using Room and SQLite.
- **User interface:** Native Android screens built with XML layouts.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Application logic |
| XML | Android UI layouts |
| Android Studio | Development environment |
| Room | Local database abstraction |
| SQLite | On-device data storage |
| Gradle | Build and dependency management |

## Project Structure

```text
JobTracker/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/example/jobtracker/
│           │   ├── MainActivity.java
│           │   ├── AddJobActivity.java
│           │   ├── JobApplication.java
│           │   ├── JobApplicationDao.java
│           │   └── AppDatabase.java
│           ├── res/
│           │   ├── layout/
│           │   ├── drawable/
│           │   └── values/
│           └── AndroidManifest.xml
├── README.md
└── .gitignore
```

## Getting Started

### Prerequisites

- Android Studio
- A compatible Android SDK
- Java and Android build tools configured through Android Studio
- An Android device or emulator

### Run the project

1. Clone or download this repository.
2. Open the project in Android Studio.
3. Allow Gradle synchronization to finish.
4. Connect an Android device with USB debugging enabled, or start an emulator.
5. Select **Run** to build and launch JobTracker.

## How It Works

Job application records are stored locally using Room, which provides an abstraction over SQLite. The application uses a DAO (Data Access Object) to insert, retrieve, update, and delete records.

Search and status filtering are applied to the retrieved applications. Dashboard counters display the total number of applications and the number currently marked as Interview.

## Data and Privacy

This version stores job application data locally on the device. It does not currently provide cloud synchronization or multi-device access.

## Future Improvements

Possible future enhancements include interview reminders, application analytics, and optional cloud synchronization.


