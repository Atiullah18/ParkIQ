# Task List - AI Assistant Intent Classification & Options Button Placement Fixes

- `[/]` Create/Update task tracking
- `[/]` Update `ProfileScreen.kt` (`queryParkIQDatabase`):
  - `[x]` Add explicit greeting filtering ("hi", "hello", "hey", "how are you", "good morning", "good evening") so greetings bypass database search and route directly to Gemini
  - `[x]` Ensure "No parking areas found" is never returned for greetings
- `[/]` Update `ProfileScreen.kt` (`AiAssistantScreen`):
  - `[x]` Remove fixed "Options" button from top subheader
  - `[x]` Render `[ Options ▾ ]` button ONLY directly below the single LATEST AI response (`index == messages.lastIndex && !chatMessage.isUser && messages.size > 1`)
  - `[x]` Ensure Options button does NOT appear before user sends a message or under older messages
  - `[x]` Ensure Options menu expands to show Location, Date, and Time
- `[ ]` Verify build via Gradle build (`app:assembleDebug`)
- `[ ]` Create walkthrough artifact (`walkthrough.artifact.md`)
