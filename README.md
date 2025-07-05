# Dental Tracker Android App

A comprehensive Android application for tracking dental bracket progress through aligned photography and timelapse creation.

## Features

### 📸 Photo Capture
- **Guided Alignment**: Visual overlay guide helps ensure consistent photo positioning
- **Front Camera Optimized**: Specifically designed for selfie-style dental photos
- **Auto-save**: Photos are automatically saved and organized by date

### 📊 Progress Tracking
- **Timeline View**: Chronological display of all captured photos
- **Progress Summary**: Shows total photos and tracking duration
- **Photo Management**: Delete unwanted photos with confirmation dialog

### 🎬 Timelapse Creation
- **Automatic Generation**: Creates smooth timelapse videos from your photo sequence
- **Progress Indicator**: Real-time progress updates during video creation
- **High Quality Output**: 30fps MP4 videos with H.264 encoding

### 🎨 Modern UI
- **Material Design 3**: Clean, modern interface following Google's design guidelines
- **Dental Theme**: Custom color scheme optimized for dental applications
- **Dark/Light Mode**: Supports system theme preferences
- **Responsive Design**: Optimized for various Android screen sizes

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository pattern
- **Database**: Room (SQLite)
- **Camera**: CameraX
- **Image Processing**: Android Bitmap APIs with EXIF support
- **Video Processing**: FFmpeg Kit
- **Image Loading**: Coil
- **Navigation**: Navigation Compose
- **Permissions**: Accompanist Permissions

## Requirements

- **Android Version**: API 24 (Android 7.0) or higher
- **Camera**: Front-facing camera required
- **Storage**: External storage access for photo and video saving
- **Memory**: Minimum 2GB RAM recommended for video processing

## Setup Instructions

### 1. Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 34
- Kotlin 1.9.10 or newer

### 2. Clone and Build
```bash
git clone <repository-url>
cd DentalTracker
./gradlew build
```

### 3. Install Dependencies
The project uses Gradle dependency management. All dependencies will be automatically downloaded during the first build.

### 4. App Icons
Add your custom launcher icons to the following directories:
- `app/src/main/res/mipmap-mdpi/ic_launcher.png` (48x48)
- `app/src/main/res/mipmap-hdpi/ic_launcher.png` (72x72)
- `app/src/main/res/mipmap-xhdpi/ic_launcher.png` (96x96)
- `app/src/main/res/mipmap-xxhdpi/ic_launcher.png` (144x144)
- `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png` (192x192)

Round icons follow the same pattern with `ic_launcher_round.png`.

### 5. Run the App
Connect an Android device or start an emulator, then:
```bash
./gradlew installDebug
```

## Project Structure

```
app/src/main/java/com/dentaltracker/
├── data/                          # Database entities and DAOs
│   ├── DentalPhoto.kt            # Photo data model
│   ├── DentalPhotoDao.kt         # Database access object
│   ├── DentalDatabase.kt         # Room database configuration
│   └── DateConverters.kt         # Type converters for Room
├── repository/                    # Data repository layer
│   └── DentalPhotoRepository.kt  # Photo data repository
├── ui/                           # User interface components
│   ├── CameraScreen.kt           # Camera capture screen
│   ├── MainScreen.kt             # Main navigation screen
│   ├── ProgressScreen.kt         # Photo timeline screen
│   └── theme/                    # App theming
├── utils/                        # Utility classes
│   ├── CameraUtils.kt            # Camera and image utilities
│   └── TimelapseCreator.kt       # Video creation utilities
├── viewmodel/                    # ViewModels and factories
│   ├── DentalTrackerViewModel.kt # Main app ViewModel
│   └── DentalTrackerViewModelFactory.kt
└── MainActivity.kt               # Main activity and navigation
```

## Usage Guide

### Taking Your First Photo
1. Open the app and tap **"Take Photo"**
2. Grant camera permissions when prompted
3. Align your teeth with the oval guide overlay
4. Use the center crosshair for positioning
5. Tap the camera button to capture

### Viewing Progress
1. From the main screen, tap **"View Progress"**
2. Scroll through your photo timeline
3. Tap the delete icon to remove unwanted photos
4. Photos are automatically sorted by date

### Creating a Timelapse
1. Capture at least 2 photos
2. From the main screen, tap **"Create Timelapse"**
3. Wait for processing to complete
4. The video will be saved to your device's Movies folder

## File Storage

- **Photos**: Stored in `Android/data/com.dentaltracker/files/Pictures/DentalTracker/`
- **Videos**: Stored in `Android/data/com.dentaltracker/files/Movies/DentalTracker/`
- **Database**: Internal app storage (automatically managed)

## Permissions

The app requires the following permissions:
- **Camera**: To take progress photos
- **Storage**: To save photos and videos (API 28 and below)
- **Media Images/Video**: To access saved content (API 29+)

## Troubleshooting

### Camera Not Working
- Ensure camera permissions are granted
- Check if another app is using the camera
- Restart the app if camera preview is black

### Timelapse Creation Fails
- Ensure you have at least 2 photos
- Check available storage space
- Verify FFmpeg dependencies are properly installed

### Photos Not Saving
- Grant storage permissions
- Check available storage space
- Ensure the app can write to external storage

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is open source. Please add your preferred license here.

## Support

For issues, questions, or feature requests, please create an issue in the repository or contact the development team.

---

**Note**: This app is designed for personal progress tracking and should not replace professional dental consultations or advice.