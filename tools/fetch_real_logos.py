# -*- coding: utf-8 -*-
from __future__ import annotations

import io
import os
import urllib.request

from PIL import Image, ImageDraw, ImageFont

OUT = r"C:\Users\ckdgh\AndroidStudioProjects\MyApplication\automotive\src\main\res\drawable-xxhdpi"
SIZE = 512

# domain -> drawable id
DOMAINS = {
    "samsungfire": "samsungfire.com",
    "hyundai": "hi.co.kr",
    "db": "idbins.com",
    "kb": "kbinsure.co.kr",
    "meritz": "meritzfire.com",
    "hanwha": "hwgeneralins.com",
    "lotte": "lotteins.co.kr",
    "heungkuk": "heungkukfire.co.kr",
    "axa": "axa.co.kr",
    "hana": "hanainsure.co.kr",
    "carrot": "carrotins.com",
    "nh": "nhfire.co.kr",
    "yebyeol": "mggeneralins.com",
    "aig": "aig.com",
    "kakao": "kakaopay.com",
}

# Extra high-quality Wikimedia / public brand assets when available
DIRECT = {
    "samsungfire": [
        "https://upload.wikimedia.org/wikipedia/commons/thumb/2/24/Samsung_Fire_%26_Marine_Insurance_logo.svg/512px-Samsung_Fire_%26_Marine_Insurance_logo.svg.png",
    ],
    "hyundai": [
        "https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Hyundai_logo.svg/512px-Hyundai_logo.svg.png",
    ],
    "axa": [
        "https://upload.wikimedia.org/wikipedia/commons/thumb/9/94/AXA_Logo.svg/512px-AXA_Logo.svg.png",
    ],
    "aig": [
        "https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/AIG_logo.svg/512px-AIG_logo.svg.png",
    ],
    "kb": [
        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Kookmin_Bank_logo.svg/512px-Kookmin_Bank_logo.svg.png",
    ],
    "kakao": [
        "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/KakaoTalk_logo.svg/512px-KakaoTalk_logo.svg.png",
    ],
}


def urls_for(key: str, domain: str) -> list[str]:
    out = list(DIRECT.get(key, []))
    out += [
        f"https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://{domain}&size=256",
        f"https://www.google.com/s2/favicons?sz=256&domain_url=https://{domain}",
        f"https://icons.duckduckgo.com/ip3/{domain}.ico",
        f"https://logo.clearbit.com/{domain}",
    ]
    return out


def fetch(url: str) -> bytes | None:
    try:
        req = urllib.request.Request(
            url,
            headers={
                "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
                "Accept": "image/avif,image/webp,image/apng,image/*,*/*;q=0.8",
            },
        )
        with urllib.request.urlopen(req, timeout=25) as resp:
            data = resp.read()
            ctype = resp.headers.get("Content-Type", "")
            if len(data) < 400:
                return None
            if "svg" in ctype and not data[:20].lstrip().startswith(b"<"):
                return None
            return data
    except Exception as e:
        print("  fail", e)
        return None


def to_square_png(data: bytes, path: str) -> bool:
    try:
        img = Image.open(io.BytesIO(data)).convert("RGBA")
    except Exception as e:
        print("  decode", e)
        return False
    if min(img.size) < 64:
        print("  small", img.size)
        return False
    # remove mostly-transparent / empty
    bbox = img.getbbox()
    if bbox:
        img = img.crop(bbox)
    canvas = Image.new("RGBA", (SIZE, SIZE), (255, 255, 255, 255))
    img.thumbnail((SIZE - 64, SIZE - 64), Image.Resampling.LANCZOS)
    x = (SIZE - img.width) // 2
    y = (SIZE - img.height) // 2
    canvas.alpha_composite(img, (x, y))
    out = Image.new("RGB", (SIZE, SIZE), (255, 255, 255))
    out.paste(canvas, mask=canvas.split()[-1])
    out.save(path, "PNG", optimize=True)
    print("  wrote", os.path.basename(path), out.size, os.path.getsize(path))
    return True


def make_custom():
    img = Image.new("RGB", (SIZE, SIZE), (255, 255, 255))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((32, 32, SIZE - 32, SIZE - 32), radius=56, outline=(96, 125, 139), width=10)
    try:
        f = ImageFont.truetype(r"C:\Windows\Fonts\malgunbd.ttf", 160)
    except Exception:
        f = ImageFont.load_default()
    bbox = d.textbbox((0, 0), "+", font=f)
    tw, th = bbox[2] - bbox[0], bbox[3] - bbox[1]
    d.text(((SIZE - tw) / 2, (SIZE - th) / 2 - 30), "+", fill=(96, 125, 139), font=f)
    img.save(os.path.join(OUT, "logo_custom.png"), "PNG")


def main():
    os.makedirs(OUT, exist_ok=True)
    ok = 0
    for key, domain in DOMAINS.items():
        path = os.path.join(OUT, f"logo_{key}.png")
        print(key)
        done = False
        for url in urls_for(key, domain):
            print(" ", url[:90])
            data = fetch(url)
            if data and to_square_png(data, path):
                done = True
                ok += 1
                break
        if not done:
            print("  KEEP")
    make_custom()
    print("done", ok, "/", len(DOMAINS))


if __name__ == "__main__":
    main()
