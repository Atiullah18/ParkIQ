# Implementation Plan - AI Assistant Intent Detection & Options Button Placement Fixes

Refine the AI Assistant in `ProfileScreen.kt` so that greetings ("hi", "hello", "hey") receive natural conversational replies from Gemini without triggering parking searches, and ensure the "Options" button appears ONLY directly below the single latest AI response.

## User Review Required

> [!IMPORTANT]
> - **Greetings vs Parking Intent**: Greetings ("hi", "hello", "hey", "how are you?", "good morning") will bypass the database search and route to Gemini for natural greetings. They will NEVER return *"No parking areas found in this location in ParkIQ."*.
> - **Single Options Button Below Latest AI Message**:
>   - Removed the fixed "Options" button from the top subheader.
>   - The `[ Options ▾ ]` button appears ONLY below the single LATEST AI message, and ONLY after the user has sent at least one message (`messages.size > 1`).
>   - Does not appear before the first user message or after older messages.
> - **Options Menu**: Tapping `[ Options ▾ ]` expands the menu showing 📍 Location, 📅 Date, and 🕒 Time.

## Proposed Changes

### AI Assistant Screen (`ui/screens/ProfileScreen.kt`)

#### [MODIFY] [ProfileScreen.kt](file:///C:/Users/md%20atiullah%20ansari/AndroidStudioProjects/ParkIQ/app/src/main/java/ui/screens/ProfileScreen.kt)
- Update `queryParkIQDatabase`: Add greeting filtering at the start (`listOf("hi", "hello", "hey", "how are you", "good morning", "good evening")`). If a message is a greeting, return `null` so Gemini handles it naturally.
- Update `AiAssistantScreen`:
  - Remove top-fixed "Options" button from the subheader.
  - In `LazyColumn` message rendering, display the `[ Options ▾ ]` button ONLY if `index == messages.lastIndex` AND `!chatMessage.isUser` AND `messages.size > 1`.
  - Maintain persistent chat history, loading state ("🤖 AI is thinking..."), retry handling, and 4 bottom navigation items.

## Verification Plan

### Automated Tests
- Run Gradle build (`app:assembleDebug`) to verify zero compilation errors.

### Manual Verification
1. Open AI Assistant -> verify NO "Options" button appears at the top or before the first message.
2. Send `"hi"` -> verify Gemini replies with a natural greeting like `"Hello! 👋 How can I help you with your parking today?"` (NO "No parking areas found..." error).
3. Verify ONE `[ Options ▾ ]` button appears directly below Gemini's greeting.
4. Tap `[ Options ▾ ]` -> select Location → "BTM Layout" -> verify parking availability results are shown.
5. Verify the `[ Options ▾ ]` button moves to the bottom of the new latest AI response and does not repeat after older messages.
