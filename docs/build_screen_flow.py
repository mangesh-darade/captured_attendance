import re
p = r'C:\wamp64\www\captured_attendance\docs\build_flows.py'
t = open(p, encoding='utf-8').read()

# business flow: move the three note boxes below the lanes
t = t.replace("Svg(1900, 1250, 'ElintOmAT - Business Flow (who does what)')", "Svg(1900, 1290, 'ElintOmAT - Business Flow (who does what)')")
for x in ('100', '700', '1300'):
    t = t.replace("s.box(%s, 1000," % x, "s.box(%s, 1060," % x)

head = t[:t.index('# =========================================================== SCREEN FLOW')]

screen = r'''# =========================================================== SCREEN FLOW
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
s.arrow([(1725, 220), (1725, 270), (400, 270), (400, 220)], 'a', True, label='next day / check in again')

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
s.box(40, 760, 1830, 380, ['Step by step', '', '', '', '', '', '', ''], '#fff', TEAL, size=14)
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
    s.text(70, 825 + i * 38, ln, 14, DARK)
s.text(40, 1170, 'Teal arrows = normal flow   Red = exception / check-out   Orange = request or offline   Purple = visit tools', 13, DARK)
s.save(r'C:\wamp64\www\captured_attendance\docs\ElintOmAT_Screen_Flow.svg')
print('ok')
'''
open(p, 'w', encoding='utf-8').write(head + screen)
