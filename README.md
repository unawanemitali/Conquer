<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

<div align="center">
  <h1>⚔️ Conquer</h1>
  <p><b>An active productivity workspace designed to eliminate procrastination through RPG mechanics.</b></p>
</div>

---

## 🚀 About The Project

**Conquer** moves beyond passive to-do lists by orchestrating your momentum. It utilizes psychological frameworks like the Eisenhower Matrix, "Eat the Frog" prioritization, and deep-work timers, all tied together with a strategic RPG economy where you earn XP for consistency.

### 🧠 The Engineering Experiment

This entire application was completely built using **Google AI Studio** via iterative prompt engineering ("vibe coding")—without writing a single line of code manually. This project serves as an exploration into leveraging AI for architectural logic, global state management, and complex UI/UX overhauls.

---

## ✨ Core Features

*   **Urgency & Importance Matrix:** Automatically categorize tasks to prioritize what truly matters.
*   **RPG Economy & XP:** Earn experience points by completing tasks and maintaining daily streaks.
*   **Dynamic Customization:** Purchase themes and character avatars that dynamically update the app's global state and UI.
*   **Active Analytics:** Track your focus through a daily study calendar heatmap and reflection archives.

---

## 🛡️ Credits & Ownership

**Engineered and Prompted by:** Mitali Unawane  
**Status:** Open to suggestions and modifications! Feel free to fork the repository and submit a pull request.

*This project is licensed under the MIT License - see the LICENSE file for details.*


# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/95ad9003-bee7-4660-976c-e69efb2dc808

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
