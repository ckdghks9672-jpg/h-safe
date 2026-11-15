# -*- coding: utf-8 -*-
from PIL import Image, ImageDraw, ImageFont
import os

out = r"C:/Users/ckdgh/AndroidStudioProjects/MyApplication/automotive/src/main/res/drawable-xxhdpi"
items = [
    ("samsungfire", "#1428A0", "삼성화재"),
    ("hyundai", "#C8102E", "현대해상"),
    ("db", "#00A3E0", "DB손해보험"),
    ("kb", "#FFCC00", "KB손해보험"),
    ("meritz", "#EE3124", "메리츠화재"),
    ("hanwha", "#F37321", "한화손보"),
    ("lotte", "#E60012", "롯데손보"),
    ("heungkuk", "#004B87", "흥국화재"),
    ("axa", "#00008F", "AXA손보"),
    ("hana", "#008485", "하나손보"),
    ("carrot", "#FF6A00", "캐롯손보"),
    ("nh", "#0B6E4F", "NH손보"),
    ("yebyeol", "#5B2C6F", "예별손보"),
    ("aig", "#0033A0", "AIG손보"),
    ("kakao", "#FAE100", "카카오페이"),
    ("custom", "#607D8B", "직접입력"),
]
S = 512
try:
    f1 = ImageFont.truetype(r"C:/Windows/Fonts/malgunbd.ttf", 58)
except Exception:
    f1 = ImageFont.load_default()

for id_, color, title in items:
    img = Image.new("RGB", (S, S), (255, 255, 255))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((12, 12, S - 12, S - 12), radius=44, outline="#E5E7EB", width=3)
    d.rounded_rectangle((12, 12, S - 12, 168), radius=44, fill=color)
    d.rectangle((12, 90, S - 12, 168), fill=color)
    ink = "#111111" if color.upper() in ("#FFCC00", "#FAE100") else "#FFFFFF"
    bb = d.textbbox((0, 0), title, font=f1)
    tw = bb[2] - bb[0]
    d.text(((S - tw) / 2, 70), title, font=f1, fill=ink)
    # large monogram circle
    mono = title[:1]
    d.ellipse((176, 250, 336, 410), fill=color)
    mink = ink
    mbb = d.textbbox((0, 0), mono, font=f1)
    mw, mh = mbb[2] - mbb[0], mbb[3] - mbb[1]
    d.text((256 - mw / 2, 300 - mh / 2), mono, font=f1, fill=mink)
    path = os.path.join(out, f"logo_{id_}.png")
    img.save(path, "PNG", optimize=True)
    print(id_, os.path.getsize(path))
print("done")
