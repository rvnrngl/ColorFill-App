<div style="float: left; margin-right: 15px;">
  <img src="https://github.com/rvnrngl/ColorFill-App/blob/master/app/src/main/ic_colorfill-playstore.png" alt="ColorFill Icon" height="75" />
  <h1>Color Fill - Android App</h1>
</div>

This is a thesis project that I developed to simulate various types of colorblindness and provide tests to help identify color vision deficiencies.

## Features

### Colorblindness Simulation
The app can simulate the following types of colorblindness:
- **Deuteranomaly**: A reduced sensitivity to green light
- **Protanomaly**: A reduced sensitivity to red light
- **Tritanomaly**: A reduced sensitivity to blue light
- **Deuteranopia**: Complete inability to perceive green light
- **Protanopia**: Complete inability to perceive red light
- **Tritanopia**: Complete inability to perceive blue light
- **Monochromacy**: Complete color blindness
  
#### Sample Images
<img src="https://drive.google.com/uc?id=1FI2-C4LRVONjfdwD_vY-7u-DprP5Zty3" alt="Sample 1" height="200" />
<img src="https://drive.google.com/uc?id=1JRjnlmcAbgkcPXuE3QKHt6kXGUQju_ep" alt="Sample 2" height="200" />
<img src="https://drive.google.com/uc?id=1qI2mwm78g9CvvIJCegwg3nSTb-mD99oc" alt="Sample 3" height="200" />


### Tests
The app includes the following tests:
- **Ishihara Test**: A test using a series of colored dot patterns to detect colorblindness.
- **Tap the Color**: A test where users choose the one different shade of color among four options.

## Tech Stack

- **Java**: Programming language used for Android app development.
- **Android Studio**: IDE used for development.
- **Python**: Used for backend colorblindness simulation and processing.
  - **Chaquopy**: Python-to-Java bridge used for integrating Python code with Android.
  - **OpenCV**: Library used for computer vision tasks.
  - **Fillow**: Library used for color-related calculations.
  - **NumPy**: Library used for numerical operations.
  - **Dalonization Algorithm**: Algorithm used for colorblindness simulation.

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/rvnrngl/ColorFill-App.git
    ```
2. Open the project in Android Studio.
3. Build and run the project on your Android device or emulator.

## Usage

1. Open the app on your Android device.
2. Navigate to the simulate section and select the type of colorblindness you want to simulate.
3. Navigate to the tests section to take the Ishihara test or the "Tap the Color" test.

## Disclaimer

Please note that this research and simulation may not be completely accurate and should be used primarily for educational purposes. It should not be considered a substitute for professional medical advice, diagnosis, or treatment.

---

Developed by Ringel et al.
