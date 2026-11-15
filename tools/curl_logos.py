# -*- coding: utf-8 -*-
import io, os, subprocess, tempfile
from PIL import Image

OUT = r"C:\Users\ckdgh\AndroidStudioProjects\MyApplication\automotive\src\main\res\drawable-xxhdpi"
SIZE = 512

# Public identification assets (Wikimedia / company favicon endpoints)
ITEMS = {
    "samsungfire": [
        "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://www.samsungfire.com&size=256",
        "https://www.google.com/s2/favicons?sz=128&domain=samsungfire.com",
    ],
    "hyundai": [
        "https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Hyundai_logo.svg/320px-Hyundai_logo.svg.png",
        "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://www.hi.co.kr&size=256",
    ],
    "db": [
        "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://www.idbins.com&size=256",
    ],
    "kb": [
        "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://www.kbinsure.co.kr&size=256",
    ],
    "meritz": [
        "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://www.meritzfire.com&size=256",
    ],
    "hanwha": [
        "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://www.hwgeneralins.com&size=256",
    ],
    "lotte": [
        "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://www.lotteins.co.kr&size=256",
    ],
    "heungkuk": [
        "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://www.heungkukfire.co.kr&size=256",
    ],
    "axa": [
        "https://upload.wikimedia.org/wikipedia/commons/thumb/9/94/AXA_Logo.svg/320px-AXA_Logo.svg.png",
        "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://www.axa.co.kr&size=256",
    ],
    "hana": [
        "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://www.hanainsure.co.kr&size=256",
    ],
    "carrot": [
        "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://www.carrotins.com&size=256",
    ],
    "nh": [
        "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://www.nhfire.co.kr&size=256",
    ],
    "yebyeol": [
        "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://www.mggeneralins.com&size=256",
    ],
    "aig": [
        "https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/AIG_logo.svg/320px-AIG_logo.svg.png",
        "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://www.aig.com&size=256",
    ],
    "kakao": [
        "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://www.kakaopay.com&size=256",
    ],
}

def curl(url: str) -> bytes | None:
    tmp = tempfile.NamedTemporaryFile(delete=False)
    tmp.close()
    try:
        r = subprocess.run(
            ["curl.exe", "-fsSL", "--max-time", "25", "-A", "Mozilla/5.0", "-o", tmp.name, url],
            capture_output=True, text=True
        )
        if r.returncode != 0:
            print(" curl fail", r.stderr.strip()[:120])
            return None
        with open(tmp.name, "rb") as f:
            data = f.read()
        return data if len(data) > 400 else None
    finally:
        try: os.unlink(tmp.name)
        except OSError: pass

def save(data: bytes, path: str) -> bool:
    try:
        img = Image.open(io.BytesIO(data)).convert("RGBA")
    except Exception as e:
        print(" decode", e)
        return False
    if min(img.size) < 64:
        print(" small", img.size)
        return False
    bbox = img.getbbox()
    if bbox: img = img.crop(bbox)
    canvas = Image.new("RGBA", (SIZE, SIZE), (255, 255, 255, 255))
    img.thumbnail((SIZE - 72, SIZE - 72), Image.Resampling.LANCZOS)
    canvas.alpha_composite(img, ((SIZE - img.width)//2, (SIZE - img.height)//2))
    out = Image.new("RGB", (SIZE, SIZE), (255, 255, 255))
    out.paste(canvas, mask=canvas.split()[-1])
    out.save(path, "PNG", optimize=True)
    print(" wrote", os.path.basename(path), img.size, os.path.getsize(path))
    return True

ok = 0
for key, urls in ITEMS.items():
    print(key)
    path = os.path.join(OUT, f"logo_{key}.png")
    done = False
    for url in urls:
        print(" ", url[:100])
        data = curl(url)
        if data and save(data, path):
            done = True; ok += 1; break
    if not done:
        print(" KEEP")
print("done", ok)
