# ElintOmAT Android Design Rules

Applies to every screen of the Android app. Code source of truth: `app/src/main/java/com/example/ui/theme/` (`ElintColor.kt`, `ElintType.kt`, `ElintShape.kt`, `ElintDimens.kt`, `ElintTheme.kt`). Reference screens: Home, Records, Profile, Face enrolment.
Web screens follow `ElintOmAT_Web_Design_Rules.md`; the brand teal is the same on both.

## 1. Principles
1. One screen = one job. The first thing the user sees is the answer or the primary action (Check In, Register face).
2. Cream background, white cards, one teal accent. No gradients, no second accent colour except status colours.
3. Calm and compact: fit the main content without scrolling where possible; never leave a large empty area.
4. State is always visible: loading, success, error and empty each have a designed state.
5. Every user-facing string comes from `AppStrings` in all four languages (EN, MR, HI, GU). No hard-coded text in new code.
6. Same pattern, same place: top bar, bottom bar, toasts and dialogs look and behave identically everywhere.

## 2. Tokens (use the theme, never raw hex in a screen)
| Token | Value | Use |
|---|---|---|
| `ElintBrandPrimary` | #0E6B5C | primary buttons, active nav, links, progress, selected chips |
| `ElintBrandPrimaryContainer` | #D7E9E4 | done steps, icon tiles, progress track |
| `ElintBrandTintSurface` / `TintPill` | #EEF5F2 / #F1F7F5 | thought card, location pill, hint cards |
| `ElintBackground` | #F7F6F2 | screen background |
| `ElintSurface` | #FFFFFF | cards, top bar, bottom bar, sheets, round icon buttons |
| `HomeInk` / `HomeMuted` | dark green-grey / grey | text / secondary text |
| Status | Amber `ElintAmberIcon` (late, pending), Red `ElintAbsentIcon` (error, rejected), Blue `#2F7CF6` (info), Purple `#7C3AED` (overtime, WFH) | chips, icon tints at 12 % |

Rules: icon tiles and chips use the colour at 12 % alpha with the full colour for the icon / text. Text on white is `HomeInk` or `HomeMuted` only.

## 3. Typography (Plus Jakarta Sans; Fraunces only for the daily line and quote)
`ElintText`: dailyLine (Fraunces), userName, sheetTitle 19/ExtraBold, cardTitle 16/ExtraBold, body, label 13/SemiBold, caption 12, overline 11/Bold caps, navLabel 11, micro 10.
- Titles bold or extra-bold, body regular, never more than 3 sizes on a card.
- One line for names and places (`maxLines = 1`, ellipsis); long free text may wrap to 2.

## 4. Shape, spacing, size
- Grid: 4 dp. Screen side padding 16. Gap between cards 12 (Home 8 to 16, never more). Card inner padding 14 to 16.
- Radius: card 20 (`ElintShapes.card`), small card and tile 16, button 14 (pill for chips and primary Home actions), input 14, round icon button 40 dp circle.
- Touch target at least 40 dp. Primary button 52 dp, secondary 48 dp.
- Elevation: none on cards; toasts and the top bar 3 to 8 dp.

## 5. Screen anatomy
```
Top bar (full width, white, status-bar padded)   avatar, greeting, bell, calendar   (all tabs except Capture and sub-flows)
Content                                          cream background, white cards
Bottom bar (white, 4 tabs)                       Home, My day, Records, Profile
```
- Sub-flows (Capture, Face enrolment) replace the top bar with their own: round white back button, title (`sheetTitle`) + one muted line, optional round close button. They hide the bottom bar.
- Never show the old logo header on a themed screen.

## 6. Components
- **Card**: white, radius 20, padding 14 to 16, optional title row (`cardTitle`) with a muted count on the right. Divide rows inside a card with a 1 dp line, not extra cards.
- **Primary button**: teal fill, white `cardTitle` text, 52 dp, icon 20 dp at the left. One primary per screen or card.
- **Secondary button**: outlined with `ElintBrandPrimaryRingEdge`, teal text, 48 dp.
- **Destructive**: red text or fill only for Log out and End visit.
- **Chip / pill**: radius full, colour at 12 % background, 11 sp bold text, 9 dp horizontal padding.
- **Progress**: segments or a bar 6 to 8 dp high, teal fill on `ElintBrandPrimaryContainer`.
- **Stat cell**: round 34 dp icon tile, value `ExtraBold` 16 to 20, label 11 to 12 muted.
- **Toast** (`ToastBanner`): white, radius 16, round status icon, status-bar padded, one or two lines, X to dismiss.
- **Dialogs**: `AlertDialog` with `sheetTitle`, text in `body`, teal confirm, muted cancel. Prefer a bottom sheet for forms.
- **Text fields**: `BasicTextField` on `#F4F3EF` with radius 14 and 12 dp padding, or the themed Material field. Never leave a field without a visible label or hint.
- **Empty state**: tinted round icon (60 dp) and one muted sentence; never a blank card.

## 7. Behaviour
- Success of a punch: vibrate, toast, return to Home. Errors stay on the screen with a retry.
- Anything that can fail offline says so (Pending note) and recovers by itself.
- Permissions are asked in context, once; a denied permission shows a clear next step, not a crash.
- Loading: spinner in teal, 20 to 24 dp, with a short verb ("Saving to the server…").
- Do not use `OutlinedTextField` inside themed sheets until the typography null issue is retested; use the themed `BasicTextField`.

## 8. Accessibility
- Contrast at least 4.5 : 1 for text; do not rely on colour alone (chips carry text, errors carry an icon).
- Icons that act have a `contentDescription`; decorative icons use `null`.
- Layout survives font scale 1.3 and a 360 dp wide screen.

## 9. Checklist before a screen is done
- [ ] Uses only theme tokens and `ElintText`; no raw colours except status tints.
- [ ] Top bar and bottom bar match the rules (or a proper sub-flow header).
- [ ] Loading, success, error and empty states exist.
- [ ] All text from `AppStrings`, translated in 4 languages.
- [ ] One primary button, 40 dp+ touch targets.
- [ ] Tested on a small (5.5") and a large (6.7") phone, and with font scale 1.3.
- [ ] No crash path: run the screen with logcat open.
