# ElintOmAT Web Design Rules (HR / manager screens)

Applies to every new web screen of the companion app: Attendance requests, App Home messages, Visit plans and anything added later.
Source of truth in code: `themes/default/assets/human_resources/css/elintat.css` (classes start with `eat-`). Do not write page-specific colours or sizes; use the tokens below.

## 1. Principles
1. One screen = one job. Title says the job; the first card does it; the table below shows the result.
2. Label above the field, always. Never use the placeholder as the label or as help text.
3. One primary button per card. Everything else is secondary (outline).
4. Show state with a chip (colour + text), never colour alone.
5. Destructive actions need a confirm and live behind an icon button, never next to the primary action.
6. Fewer words: sentence case, verbs on buttons ("Add visit", not "Submit").
7. The screen must work at 1366 px and collapse to one column under 900 px.

## 2. Tokens
These match the existing Human Resources screens (`/human_resources`), so new screens look like part of the same module.

| Token | Value | Use |
|---|---|---|
| `--eat-primary` / hover / edge | #1a5f66 / #134a50 / #134a50 | primary button, table header, links, focus ring |
| `--eat-primary-tint` | #e6f1f2 | default / info chip, icon tiles |
| `--eat-ink` / body / muted | #1e293b / #334155 / #64748b | titles, text, help text |
| `--eat-line` / soft / subtle | #e2e8f0 / #f1f5f9 / #f8fafc | borders, row lines, toolbar and hover |
| ok / warn / danger | #059669 (#ecfdf5) / #d97706 (#fef3c7) / #dc2626 (#fee2e2) | KPI icons and status chips |
| Card | white, 1 px `--eat-line`, radius 10, shadow `0 1px 3px rgba(15,23,42,.04)` | page head, KPI, form, table |
| Input / button radius | 6 | |
| Spacing | 4 / 8 / 12 / 16 / 20 / 32 | `--eat-s1` to `--eat-s6` |

Type scale: 11 (table head, chips, KPI label), 12 (labels, help), 13 (body, inputs, table, buttons), 15 (card title), 20 (page title), 26 (KPI value). Weights 400 / 600 / 700.

## 3. Layout
```
.eat-page
  .eat-head            white bar: title (20/700) + one line of purpose, actions on the right
  .eat-alert           success / error, directly under the head
  .eat-kpis            four KPI cards (label, value, coloured icon tile), like the HR list
  .eat-card            the form (card head = what you are doing)
  .eat-card            toolbar (filters) + table with solid #1a5f66 header
```
- Full width inside the ERP content area, 20 px between blocks.
- Form: 12-column grid, 16 gap. Short fields span 3-4 columns, long text spans 12. Related fields on one row.
- Form footer: top border, buttons right-aligned, live preview or helper text on the left.

## 4. Components
- **Input / select**: 40 px high, 10 radius, 1 px border `#CBD5D1`, focus = teal border + 3 px ring (18 % teal).
- **Textarea**: min 110 px; add class `skip` so the ERP does not turn it into the rich-text editor.
- **Help text**: 12/16 muted under the field.
- **Segmented control** for 2-4 exclusive choices (replaces a dropdown when options are few).
- **Buttons**: primary (filled teal), secondary (outline), small (32 px) in tables, icon (32 x 32, with `title` and `aria-label`).
- **Chips** (with a dot, like `.hr-badge`): default and info blue tint, ok green, warn amber, muted grey, danger red.
- **KPI card**: label 11 px uppercase muted, value 26 px bold, 48 px icon tile on the right (blue, green, amber, grey, red).
- **Table**: solid #1a5f66 header, white 11 px uppercase text (same as the employee list); cells 12/16 padding, 1 px row line; hover tint; secondary text under the main text in 12 px muted; actions in the last column, right aligned; hidden rows are 55 % opacity.
- **Toolbar** above a table: filters left, view switches (segmented) right.
- **Empty state**: icon + one sentence, centred; never an empty table body.
- **Alert**: one line, icon + message, green or red tint, no close button.

## 5. Content rules
- Page title = menu name. Subtitle explains in one sentence what the screen controls in the app.
- Date format `Fri, 30 Oct 2026`; time `9:40 AM`.
- Status words are the same everywhere: Sent, Pending, Approved, Rejected, Planned, Done, Active, Hidden.
- Dates and bulk entry: prefer one action that fills many days (one line per day) over repeating a form.

## 6. Accessibility and behaviour
- Every input has a `<label for>`; every icon-only button has `title` and `aria-label`.
- Text contrast at least 4.5 : 1 (ink on white 12 : 1, muted on white 5.2 : 1).
- Visible keyboard focus (teal ring) on inputs, buttons and the segmented control.
- Forms post with the CSRF token; admin-only actions are checked on the server, the UI only hides them.
- Filters that do not need a server round trip (type filter) run in the browser; month / date filters reload the page.

## 7. Checklist before shipping a screen
- [ ] Only `eat-` classes and tokens, no inline colours.
- [ ] One primary button per card.
- [ ] Labels above fields, help text under them.
- [ ] Empty state, success alert and error alert are present.
- [ ] Works at 1366 px and under 900 px.
- [ ] Destructive action asks for confirmation.
- [ ] Textareas have class `skip`.
