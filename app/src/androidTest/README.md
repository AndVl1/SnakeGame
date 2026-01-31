# Snake Game - UI Test Suite

Comprehensive UI tests for Snake Game using Espresso, Compose Testing, and Ultron.

## Test Structure

```
androidTest/
├── main/
│   ├── BaseTest.kt              # Base test class with common setup
│   └── TestTags.kt              # Duplicate test tags for test package
├── pages/                        # Page Object Models
│   ├── ComposeLeaderboardPage.kt
│   ├── ComposeGamePage.kt
│   ├── ComposeSettingsPage.kt
│   ├── ComposeAchievementsPage.kt
│   └── ComposeStatisticsPage.kt
└── tests/                        # Test classes
    ├── LeaderboardScreenTest.kt
    ├── GameScreenTest.kt
    ├── SettingsScreenTest.kt
    ├── AchievementsScreenTest.kt
    ├── StatisticsScreenTest.kt
    └── NavigationTest.kt
```

## Test Coverage

### LeaderboardScreenTest
- ✅ App start displays leaderboard
- ✅ Navigation to Game screen
- ✅ Navigation to Settings screen
- ✅ Navigation to Achievements screen
- ✅ Navigation to Statistics screen
- ✅ Back navigation from Settings
- ✅ All buttons are clickable

### GameScreenTest
- ✅ Game screen displays all components
- ✅ Pause FAB functionality
- ✅ Pause/Resume via FAB
- ✅ Pause/Resume via bottom controls
- ✅ Restart button functionality
- ✅ Instructions button functionality
- ✅ All controls are accessible
- ✅ Score and controls visibility during gameplay

### SettingsScreenTest
- ✅ Settings screen displays all components
- ✅ Dark theme toggle functionality
- ✅ Theme toggle persistence
- ✅ Sound toggle functionality (if enabled)
- ✅ Vibration toggle functionality (if enabled)
- ✅ Difficulty selector functionality (if enabled)
- ✅ Swipe sensitivity slider functionality (if enabled)
- ✅ Back navigation
- ✅ Settings persistence across navigation

### AchievementsScreenTest
- ✅ Achievements screen display
- ✅ Achievement list display
- ✅ Loading states
- ✅ Locked/Unlocked achievement states
- ✅ Achievement progress display
- ✅ Back navigation
- ✅ Multiple navigation cycles

### StatisticsScreenTest
- ✅ Statistics screen display
- ✅ Statistics content display
- ✅ Loading states
- ✅ Error states
- ✅ Back navigation
- ✅ Multiple navigation cycles

### NavigationTest
- ✅ Full navigation flows between all screens
- ✅ Settings persistence across navigation
- ✅ Multiple screen navigation sequences
- ✅ Rapid navigation between screens
- ✅ Navigation state preservation

## Running Tests

### Run all tests
```bash
./gradlew connectedAndroidTest
```

### Run specific test class
```bash
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=ru.andvl.snakegame.tests.LeaderboardScreenTest
```

### Run specific test method
```bash
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=ru.andvl.snakegame.tests.LeaderboardScreenTest#whenAppStarts_shouldDisplayLeaderboardScreen
```

## Page Object Pattern

All tests use the Page Object pattern via Ultron framework:

```kotlin
ComposeLeaderboardPage
    .assertNoRecordsIsDisplayed()
    .clickPlayButton()

ComposeGamePage
    .assertGameScreenIsDisplayed()
```

## Test Tags

Test tags are defined in `/main/.../TestTags.kt` and must be added to UI components:

```kotlin
// In UI component
.testTag(TestTags.PLAY_BUTTON)

// In Page Object
private val playButton = hasTestTag(TestTags.PLAY_BUTTON)
```

## Dependencies

- **Ultron** (v2.5.6): Test framework wrapper
  - ultron-android: Android testing
  - ultron-compose: Compose testing
  - ultron-allure: Allure reporting integration
- **Espresso**: Android UI testing
- **Compose Testing**: Jetpack Compose testing

## Best Practices

1. **Use Page Objects**: All UI interactions through page objects
2. **Descriptive Names**: Test names describe what they test
3. **Setup/Teardown**: Use SetUpRule and TearDownRule for test lifecycle
4. **Wait Helpers**: Use waitShort(), waitMedium(), waitLong() for animations
5. **Independent Tests**: Each test should be independent
6. **Test Tags**: Always add test tags to new UI components

## Adding New Tests

1. Add test tags to `TestTags.kt` (main and androidTest)
2. Add UI test tags to Compose components
3. Create/Update Page Object in `pages/`
4. Create test class in `tests/` extending BaseTest
5. Write descriptive test methods
6. Run and verify tests pass

## Known Limitations

- **Game Logic**: Game logic tests are in unit tests, not UI tests
- **Back Navigation**: System back button requires special handling
- **Timing**: Some tests use sleep() for animations (not ideal but works)
- **Save Score Dialog**: Not all dialog flows are tested (needs more test tags)

## Future Improvements

- [ ] Add test tags to all dialogs
- [ ] Test save score flow end-to-end
- [ ] Test game over flow
- [ ] Test replay functionality (if added)
- [ ] Add screenshot tests
- [ ] Add performance benchmarks
- [ ] Reduce reliance on sleep() with IdlingResources
