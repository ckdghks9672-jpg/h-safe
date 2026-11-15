from PIL import Image, ImageDraw, ImageFont
import math
import os

out_dir = r"C:\Users\ckdgh\AndroidStudioProjects\MyApplication\automotive\src\main\res\drawable-xxhdpi"
os.makedirs(out_dir, exist_ok=True)
for junk in ["test_sf.svg"]:
    p = os.path.join(out_dir, junk)
    if os.path.exists(p):
        os.remove(p)

candidates = [
    r"C:\Windows\Fonts\malgunbd.ttf",
    r"C:\Windows\Fonts\malgun.ttf",
    r"C:\Windows\Fonts\arialbd.ttf",
    r"C:\Windows\Fonts\arial.ttf",
]
font_path = next((p for p in candidates if os.path.exists(p)), None)
print("font", font_path)


def font(size):
    if font_path:
        return ImageFont.truetype(font_path, size)
    return ImageFont.load_default()


logos = [
    ("samsungfire", "#1428A0", "#FFFFFF", "삼성화재", "SF", "shield"),
    ("hyundai", "#C8102E", "#FFFFFF", "현대해상", "HI", "wave"),
    ("db", "#00A3E0", "#FFFFFF", "DB손보", "DB", "bars"),
    ("kb", "#FFCC00", "#111111", "KB손보", "KB", "star"),
    ("meritz", "#EE3124", "#FFFFFF", "메리츠", "M", "flame"),
    ("hanwha", "#F37321", "#FFFFFF", "한화손보", "H", "sun"),
    ("lotte", "#E60012", "#FFFFFF", "롯데손보", "L", "diamond"),
    ("heungkuk", "#004B87", "#FFFFFF", "흥국화재", "HK", "shield"),
    ("axa", "#00008F", "#FFFFFF", "AXA", "AXA", "triangle"),
    ("hana", "#008485", "#FFFFFF", "하나손보", "하나", "circle"),
    ("carrot", "#FF6A00", "#FFFFFF", "캐롯", "C", "carrot"),
    ("nh", "#0B6E4F", "#FFFFFF", "NH손보", "NH", "leaf"),
    ("yebyeol", "#5B2C6F", "#FFFFFF", "예별", "YB", "moon"),
    ("aig", "#0033A0", "#FFFFFF", "AIG", "AIG", "bars"),
    ("kakao", "#FAE100", "#3C1E1E", "카카오페이", "k", "speech"),
    ("custom", "#607D8B", "#FFFFFF", "직접입력", "+", "plus"),
]


def draw_mark(draw, kind, cx, cy, color, size=34):
    if kind == "shield":
        pts = [
            (cx, cy - size),
            (cx + size * 0.7, cy - size * 0.55),
            (cx + size * 0.55, cy + size * 0.2),
            (cx, cy + size),
            (cx - size * 0.55, cy + size * 0.2),
            (cx - size * 0.7, cy - size * 0.55),
        ]
        draw.polygon(pts, fill=color)
    elif kind == "wave":
        draw.arc((cx - size, cy - size // 2, cx + size, cy + size), 200, 340, fill=color, width=8)
        draw.arc((cx - size, cy - size // 2 + 12, cx + size, cy + size + 12), 200, 340, fill=color, width=8)
    elif kind == "bars":
        for i, h in enumerate([size, size * 0.7, size * 1.1]):
            x = cx - 28 + i * 20
            draw.rounded_rectangle((x, cy + size - h, x + 12, cy + size), radius=3, fill=color)
    elif kind == "star":
        draw.ellipse((cx - size * 0.35, cy - size * 0.35, cx + size * 0.35, cy + size * 0.35), fill=color)
        draw.rectangle((cx - 4, cy - size, cx + 4, cy + size), fill=color)
        draw.rectangle((cx - size, cy - 4, cx + size, cy + 4), fill=color)
    elif kind == "flame":
        pts = [
            (cx, cy - size),
            (cx + size * 0.55, cy),
            (cx + size * 0.25, cy + size * 0.2),
            (cx, cy + size),
            (cx - size * 0.25, cy + size * 0.2),
            (cx - size * 0.55, cy),
        ]
        draw.polygon(pts, fill=color)
    elif kind == "sun":
        draw.ellipse((cx - 18, cy - 18, cx + 18, cy + 18), fill=color)
        for a in range(0, 360, 45):
            rad = math.radians(a)
            draw.line(
                (
                    cx + math.cos(rad) * 22,
                    cy + math.sin(rad) * 22,
                    cx + math.cos(rad) * 34,
                    cy + math.sin(rad) * 34,
                ),
                fill=color,
                width=5,
            )
    elif kind == "diamond":
        pts = [(cx, cy - size), (cx + size * 0.7, cy), (cx, cy + size), (cx - size * 0.7, cy)]
        draw.polygon(pts, fill=color)
    elif kind == "triangle":
        pts = [(cx, cy - size), (cx + size, cy + size * 0.7), (cx - size, cy + size * 0.7)]
        draw.polygon(pts, fill=color)
    elif kind == "circle":
        draw.ellipse((cx - size * 0.7, cy - size * 0.7, cx + size * 0.7, cy + size * 0.7), outline=color, width=8)
        draw.ellipse((cx - size * 0.35, cy - size * 0.35, cx + size * 0.35, cy + size * 0.35), fill=color)
    elif kind == "carrot":
        pts = [(cx, cy + size), (cx - size * 0.45, cy - size * 0.2), (cx + size * 0.45, cy - size * 0.2)]
        draw.polygon(pts, fill=color)
        draw.line((cx, cy - size * 0.2, cx, cy - size), fill="#2E7D32", width=6)
    elif kind == "leaf":
        draw.ellipse((cx - size * 0.2, cy - size, cx + size * 0.9, cy + size * 0.2), fill=color)
        draw.line((cx, cy + size * 0.1, cx + size * 0.35, cy - size * 0.5), fill="#FFFFFF", width=4)
    elif kind == "moon":
        draw.ellipse((cx - size * 0.7, cy - size * 0.7, cx + size * 0.7, cy + size * 0.7), fill=color)
        draw.ellipse((cx - size * 0.15, cy - size * 0.85, cx + size * 0.95, cy + size * 0.25), fill="#FFFFFF")
    elif kind == "speech":
        draw.rounded_rectangle((cx - size, cy - size * 0.7, cx + size, cy + size * 0.35), radius=14, fill=color)
        draw.polygon([(cx - 8, cy + size * 0.35), (cx + 8, cy + size * 0.35), (cx - 14, cy + size * 0.85)], fill=color)
    elif kind == "plus":
        draw.rectangle((cx - 8, cy - size, cx + 8, cy + size), fill=color)
        draw.rectangle((cx - size, cy - 8, cx + size, cy + 8), fill=color)


for id_, bg, fg, title, mark, kind in logos:
    img = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((8, 8, 248, 248), radius=36, fill="#FFFFFF", outline="#E0E0E0", width=3)
    d.rounded_rectangle((8, 8, 248, 96), radius=36, fill=bg)
    d.rectangle((8, 60, 248, 96), fill=bg)
    draw_mark(d, kind, 128, 52, fg, size=28)
    f1 = font(34)
    bbox = d.textbbox((0, 0), title, font=f1)
    tw = bbox[2] - bbox[0]
    d.text(((256 - tw) / 2, 128), title, font=f1, fill="#212121")
    f2 = font(22)
    bbox2 = d.textbbox((0, 0), mark, font=f2)
    tw2 = bbox2[2] - bbox2[0]
    d.text(((256 - tw2) / 2, 178), mark, font=f2, fill=bg)
    path = os.path.join(out_dir, f"logo_{id_}.png")
    img.save(path, "PNG")
    print("wrote", path, os.path.getsize(path))

print("done")
