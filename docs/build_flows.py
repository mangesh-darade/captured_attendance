import html

TEAL, DARK, MINT, AMB, BLU, PUR, RED = '#0E6B5C', '#1F3A33', '#E7F2EE', '#B46B00', '#2F7CF6', '#7C3AED', '#B91C1C'


class Svg:
    def __init__(self, w, h, title):
        self.w, self.h = w, h
        self.p = ['<svg xmlns="http://www.w3.org/2000/svg" width="%d" height="%d" viewBox="0 0 %d %d" font-family="Segoe UI, Arial, sans-serif">' % (w, h, w, h),
                  '<defs><marker id="a" markerWidth="10" markerHeight="10" refX="9" refY="5" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#0E6B5C"/></marker>'
                  '<marker id="r" markerWidth="10" markerHeight="10" refX="9" refY="5" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#B91C1C"/></marker>'
                  '<marker id="o" markerWidth="10" markerHeight="10" refX="9" refY="5" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#B46B00"/></marker></defs>',
                  '<rect width="%d" height="%d" fill="#F7F6F2"/>' % (w, h),
                  '<text x="%d" y="42" text-anchor="middle" font-size="28" font-weight="700" fill="%s">%s</text>' % (w // 2, DARK, html.escape(title))]

    def box(self, x, y, w, h, lines, fill='#fff', stroke=TEAL, size=13, bold_first=True, color=DARK, rx=12):
        self.p.append('<rect x="%d" y="%d" width="%d" height="%d" rx="%d" fill="%s" stroke="%s" stroke-width="2"/>' % (x, y, w, h, rx, fill, stroke))
        if isinstance(lines, str):
            lines = [lines]
        n = len(lines)
        y0 = y + h / 2 - (n - 1) * (size + 3) / 2 + size / 3
        for i, ln in enumerate(lines):
            wt = 'font-weight="700"' if (i == 0 and bold_first) else ''
            self.p.append('<text x="%d" y="%d" text-anchor="middle" font-size="%d" fill="%s" %s>%s</text>' % (x + w / 2, y0 + i * (size + 3), size, color, wt, html.escape(ln)))

    def text(self, x, y, t, size=13, color=DARK, bold=False, anchor='start'):
        self.p.append('<text x="%d" y="%d" font-size="%d" fill="%s" text-anchor="%s" %s>%s</text>' % (x, y, size, color, anchor, 'font-weight="700"' if bold else '', html.escape(t)))

    def arrow(self, pts, kind='a', dash=False, label=None):
        d = 'M' + ' L'.join('%d,%d' % p for p in pts)
        col = {'a': TEAL, 'r': RED, 'o': AMB}[kind]
        self.p.append('<path d="%s" fill="none" stroke="%s" stroke-width="2.4" %s marker-end="url(#%s)"/>' % (d, col, 'stroke-dasharray="7 5"' if dash else '', kind))
        if label:
            mx = (pts[0][0] + pts[-1][0]) / 2
            my = (pts[0][1] + pts[-1][1]) / 2
            self.p.append('<rect x="%d" y="%d" width="%d" height="18" fill="#F7F6F2"/>' % (mx - len(label) * 3.6, my - 12, len(label) * 7.2))
            self.text(mx, my + 1, label, 12, col, True, 'middle')

    def num(self, x, y, n):
        self.p.append('<circle cx="%d" cy="%d" r="13" fill="%s"/><text x="%d" y="%d" text-anchor="middle" font-size="13" font-weight="700" fill="#fff">%s</text>' % (x, y, TEAL, x, y + 5, n))

    def save(self, path):
        open(path, 'w', encoding='utf-8').write('\n'.join(self.p) + '</svg>')


# =========================================================== BUSINESS FLOW (swimlanes)
s = Svg(1900, 1290, 'ElintOmAT - Business Flow (who does what)')
lanes = [('EMPLOYEE (App)', 90), ('ANDROID APP', 330), ('API + DATABASE', 570), ('MANAGER / HR (Web)', 810)]
for name, y in lanes:
    s.p.append('<rect x="20" y="%d" width="1860" height="220" rx="14" fill="#fff" stroke="#CBE4DC" stroke-width="1.5"/>' % y)
    s.p.append('<rect x="20" y="%d" width="46" height="220" rx="14" fill="%s"/>' % (y, TEAL))
    s.p.append('<text x="43" y="%d" text-anchor="middle" font-size="15" font-weight="700" fill="#fff" transform="rotate(-90 43 %d)">%s</text>' % (y + 110, y + 110, name))

# lane 1 employee
s.box(100, 130, 200, 90, ['1 Open app', 'Login (user + password)'], size=13)
s.box(340, 130, 200, 90, ['2 Check in', 'Look at camera'], size=13)
s.box(580, 130, 200, 90, ['3 Works / visits', 'note, photo, voice'], size=13)
s.box(820, 130, 200, 90, ['4 Needs a day off?', 'Taps Late / WFH / Leave'], size=13)
s.box(1060, 130, 200, 90, ['5 End visit', 'Check out (face)'], size=13)
s.box(1300, 130, 200, 90, ['6 Day complete', 'note + mood, Save'], size=13)
s.box(1540, 130, 300, 90, ['7 Records', 'hours, leave balance, requests'], size=13)
# lane 2 app
s.box(100, 370, 200, 90, ['Home screen', 'status, stats, plan'], MINT, size=13)
s.box(340, 370, 200, 90, ['Face + GPS capture', 'site matched'], MINT, size=13)
s.box(580, 370, 200, 90, ['Timer + visit card', 'Today\'s plan'], MINT, size=13)
s.box(820, 370, 200, 90, ['Quick update sheet', 'Undo available'], MINT, size=13)
s.box(1060, 370, 200, 90, ['Geofence tracking', 'stops at check-out'], MINT, size=13)
s.box(1300, 370, 200, 90, ['Pending sync note', 'if offline'], MINT, size=13)
s.box(1540, 370, 300, 90, ['Reminders / alerts', 'approval, left site'], MINT, size=13)
# lane 3 api
s.box(100, 610, 200, 90, ['Token auth', 'home_summary'], '#EAF2FE', BLU, size=13)
s.box(340, 610, 200, 90, ['Face match + site', 'saves attendance'], '#EAF2FE', BLU, size=13)
s.box(580, 610, 200, 90, ['visit_context / notes', 'visit_plans, notes'], '#EAF2FE', BLU, size=13)
s.box(820, 610, 200, 90, ['quick_update', 'attendance_requests'], '#EAF2FE', BLU, size=13)
s.box(1060, 610, 200, 90, ['store(check_out)', 'area_log, plan done'], '#EAF2FE', BLU, size=13)
s.box(1300, 610, 200, 90, ['day_wrapup', 'records / my_day'], '#EAF2FE', BLU, size=13)
s.box(1540, 610, 300, 90, ['balances, shifts', 'announcements, messages'], '#EAF2FE', BLU, size=13)
# lane 4 manager
s.box(100, 850, 200, 90, ['Admin setup', 'sites, shift, leave'], '#FFF6E5', AMB, size=13)
s.box(340, 850, 200, 90, ['Reviews face-less', 'check-ins (manual)'], '#FFF6E5', AMB, size=13)
s.box(580, 850, 200, 90, ['Visit plans screen', 'assign stops'], '#FFF6E5', AMB, size=13)
s.box(820, 850, 200, 90, ['Attendance requests', 'Approve / Reject + note'], '#FFF6E5', AMB, size=13)
s.box(1060, 850, 200, 90, ['Correction requests', 'apply check-out'], '#FFF6E5', AMB, size=13)
s.box(1300, 850, 200, 90, ['App Home messages', 'daily line, thought'], '#FFF6E5', AMB, size=13)
s.box(1540, 850, 300, 90, ['Announcements', 'needs-ack + read receipts'], '#FFF6E5', AMB, size=13)
# horizontal employee flow
for x1, x2 in [(300, 340), (540, 580), (780, 820), (1020, 1060), (1260, 1300), (1500, 1540)]:
    s.arrow([(x1, 175), (x2, 175)])
# vertical links down per column
for cx in [200, 440, 680, 920, 1160, 1400, 1690]:
    s.arrow([(cx, 220), (cx, 370)], 'a', True)
    s.arrow([(cx, 460), (cx, 610)], 'a', True)
for cx in [440, 680, 920, 1160, 1400, 1690]:
    s.arrow([(cx, 700), (cx, 850)], 'o', True)
s.arrow([(200, 850), (200, 700)], 'o', True)
# notes
s.box(100, 1060, 560, 190, ['Exceptions handled', '- Face fails 3 times: "Check in without face scan" + manual request to manager',
                            '- No internet: punch saved as Pending, syncs later, "Synced" toast',
                            '- Unknown site: allowed and saved as new location', '- Approved WFH day: punch is marked WFH'], '#fff', RED, size=13, bold_first=True)
s.box(700, 1060, 560, 190, ['Request lifecycle', 'Sent -> Pending -> Approved / Rejected (or Undone)',
                            'Leave approved: balance reduced', 'Correction approved: check-out applied',
                            'Late / can\'t come clear after check-in'], '#fff', TEAL, size=13)
s.box(1300, 1060, 540, 190, ['Business value', '- Trusted attendance (face + GPS + geofence)', '- Managers decide in one place, with context',
                              '- Employee always knows the status', '- HR controls content without an app update'], '#fff', PUR, size=13)
s.save(r'C:\wamp64\www\captured_attendance\docs\ElintOmAT_Business_Flow.svg')

# =========================================================== SCREEN FLOW
s = Svg(1900, 1180, 'ElintOmAT - Screen Flow (what the user sees)')
# main path (row at y=100)
Y, H = 100, 120
s.box(40, Y, 170, H, ['LOGIN', 'user + password'], '#fff', TEAL, size=14)
s.box(270, Y, 260, H, ['HOME', '(not checked in)', 'card, tiles, stats'], MINT, TEAL, size=14)
s.box(600, Y, 260, H, ['CAPTURE', 'face camera + location', 'IN / OUT, notes'], MINT, TEAL, size=14)
s.box(930, Y, 290, H, ['HOME (checked in)', 'Checked in - 3h 12m', 'visit card + live timer'], '#E3F1EB', TEAL, size=14)
s.box(1290, Y, 220, H, ['END VISIT', 'opens check-out scan'], '#fff', RED, size=14)
s.box(1580, Y, 290, H, ['DAY COMPLETE', 'worked time, visits,', 'outside time, note + mood'], '#E3F1EB', TEAL, size=14)
for x1, x2, lab in [(210, 270, 'login ok'), (530, 600, 'Check In'), (860, 930, 'success'), (1220, 1290, ''), (1510, 1580, 'checked out')]:
    s.arrow([(x1, Y + 60), (x2, Y + 60)], 'a' if lab != '' else 'r', label=lab or None)
s.num(40, 90, '1'); s.num(270, 90, '2'); s.num(600, 90, '3'); s.num(930, 90, '4'); s.num(1290, 90, '5'); s.num(1580, 90, '6')
# return loop (check-out goes back)
s.arrow([(1725, 220), (1725, 270), (400, 270), (400, 220)], 'a', True)
s.p.append('<rect x="1180" y="256" width="230" height="22" fill="#F7F6F2"/>')
s.text(1295, 272, 'next day / check in again', 13, TEAL, True, 'middle')

# column under HOME (not checked in)
items = [('QUICK UPDATE SHEET', 'Late / WFH / Cant come / Leave (+ voice)', AMB, '#FFF6E5'),
         ('MAPS app', 'View on Map: pin at live location', BLU, '#fff'),
         ('MY DAY', 'punches and breaks', TEAL, '#fff'),
         ('RECORDS', 'month, leave balance, requests, missed punch', TEAL, '#fff'),
         ('ANNOUNCEMENTS', 'list and acknowledge (bell icon)', PUR, '#fff'),
         ('PROFILE', 'face, permissions, language, logout', TEAL, '#fff')]
s.text(270, 318, 'From Home (top bar and bottom tabs)', 14, TEAL, True)
yy = 335
for i, (a, b, col, fill) in enumerate(items):
    s.box(300, yy, 300, 62, [a, b], fill, col, size=12)
    s.p.append('<line x1="270" y1="%d" x2="300" y2="%d" stroke="%s" stroke-width="2.4"/>' % (yy + 31, yy + 31, TEAL))
    yy += 74
s.p.append('<line x1="270" y1="220" x2="270" y2="%d" stroke="%s" stroke-width="2.4"/>' % (yy - 74 + 31, TEAL))

# exceptions under CAPTURE
s.text(660, 318, 'From Capture (exceptions)', 14, RED, True)
s.box(660, 335, 300, 90, ['Face fails 3 times', '"Check in without face scan?"', 'Yes -> punch saved + manual request'], '#fff', RED, size=12)
s.box(660, 440, 300, 90, ['No internet', 'saved as Pending, syncs later', '"Synced" toast, Pending note clears'], '#fff', AMB, size=12)
s.box(660, 545, 300, 90, ['No GPS / no camera permission', 'prompt to allow; punch not saved'], '#fff', RED, size=12)
s.p.append('<line x1="660" y1="220" x2="640" y2="220" stroke="%s" stroke-width="2.4"/>' % RED)
s.p.append('<line x1="640" y1="220" x2="640" y2="590" stroke="%s" stroke-width="2.4"/>' % RED)
for yv in (380, 485, 590):
    s.p.append('<line x1="640" y1="%d" x2="660" y2="%d" stroke="%s" stroke-width="2.4"/>' % (yv, yv, RED))

# tools under HOME (checked in)
s.text(1020, 318, 'While checked in', 14, PUR, True)
tools = [('Add note', 'text saved to the visit'), ('Photo', 'camera, uploaded to the visit'), ('Voice note', 'up to 60 seconds'),
         ('Details', 'customer, task, today\'s notes'), ("TODAY'S PLAN", 'Done / Now / next stops, See all')]
yy = 335
for i, (a, b) in enumerate(tools):
    s.box(1050, yy, 300, 62, [a, b], '#fff', PUR if i < 4 else BLU, size=12)
    s.p.append('<line x1="1020" y1="%d" x2="1050" y2="%d" stroke="%s" stroke-width="2.4"/>' % (yy + 31, yy + 31, PUR))
    yy += 74
s.p.append('<line x1="1020" y1="220" x2="1020" y2="%d" stroke="%s" stroke-width="2.4"/>' % (yy - 74 + 31, PUR))

# day complete notes
s.box(1420, 335, 450, 130, ['After check-out', 'Home shows DAY COMPLETE (not "Not checked in")', 'Save note + mood: "Saved" toast', 'Visit timer is cleared'], '#fff', TEAL, size=12)
s.box(1420, 485, 450, 130, ['Manager side (web)', 'Approve / reject requests, correction, announcements,', 'daily line + thought, visit plans'], '#FFF6E5', AMB, size=12)

# steps
s.box(40, 800, 1830, 350, [''], '#fff', TEAL, size=14)
s.text(70, 832, 'Step by step', 17, TEAL, True)
steps = [
    '1   Login with user and password. Allow camera, location and notification permissions (Profile > Permissions shows what is missing).',
    '2   Home opens: check-in card with live location, four quick tiles, announcements, thought of the day, weekly stats.',
    '3   Tap Check In: Capture opens, look at the camera. On success the app returns to Home automatically.',
    '4   Home becomes "checked in": header shows the elapsed time, visit card has a live timer, plus Add note / Photo / Voice note / Details and Today\'s plan.',
    '5   Late, working from home, cannot come or need leave? Tap a tile, fill the sheet and Send. The manager decides on the web; Undo is possible until then.',
    '6   Tap End visit and scan the face to check out. Day complete shows worked time, visits and time outside the site. Add a note and mood, then Save.',
    '7   Records shows the month (days, hours, overtime, late days), leave balance and your requests. A missed check-out can be corrected in about 10 seconds.',
    '8   Profile: register or update face, language (EN / MR / HI / GU), permissions, auto submit, diagnostics, log out.',
]
for i, ln in enumerate(steps):
    s.text(70, 868 + i * 34, ln, 14, DARK)
s.text(40, 1175, 'Teal arrows = normal flow   Red = exception / check-out   Orange = request or offline   Purple = visit tools', 13, DARK)
s.save(r'C:\wamp64\www\captured_attendance\docs\ElintOmAT_Screen_Flow.svg')
print('ok')
