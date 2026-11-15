# -*- coding: utf-8 -*-
import io, os, re, subprocess, tempfile
from PIL import Image, ImageDraw, ImageFont

OUT = r"C:\Users\ckdgh\AndroidStudioProjects\MyApplication\automotive\src\main\res\drawable-xxhdpi"
SIZE = 512

SITES = {
    "samsungfire": ["https://www.samsungfire.com", "https://direct.samsungfire.com"],
    "hyundai": ["https://www.hi.co.kr", "https://direct.hi.co.kr"],
    "db": ["https://www.idbins.com", "https://www.dbins.com"],
    "kb": ["https://www.kbinsure.co.kr"],
    "meritz": ["https://www.meritzfire.com"],
    "hanwha": ["https://www.hwgeneralins.com", "https://www.hanwhageneral.co.kr"],
    "lotte": ["https://www.lotteins.co.kr"],
    "heungkuk": ["https://www.heungkukfire.co.kr"],
    "axa": ["https://www.axa.co.kr"],
    "hana": ["https://www.hanainsure.co.kr"],
    "carrot": ["https://www.carrotins.com"],
    "nh": ["https://www.nhfire.co.kr"],
    "yebyeol": ["https://www.mggeneralins.com"],
    "aig": ["https://www.aig.co.kr", "https://www.aig.com"],
    "kakao": ["https://www.kakaopay.com", "https://insurance.kakaopay.com"],
}

FALLBACK_COLORS = {
    "samsungfire": ("#1428A0", "삼성화재"),
    "hyundai": ("#C8102E", "현대해상"),
    "db": ("#00A3E0", "DB손보"),
    "kb": ("#FFCC00", "KB손보"),
    "meritz": ("#EE3124", "메리츠"),
    "hanwha": ("#F37321", "한화손보"),
    "lotte": ("#E60012", "롯데손보"),
    "heungkuk": ("#004B87", "흥국화재"),
    "axa": ("#00008F", "AXA"),
    "hana": ("#008485", "하나손보"),
    "carrot": ("#FF6A00", "캐롯"),
    "nh": ("#0B6E4F", "NH손보"),
    "yebyeol": ("#5B2C6F", "예별"),
    "aig": ("#0033A0", "AIG"),
    "kakao": ("#FAE100", "카카오페이"),
    "custom": ("#607D8B", "직접입력"),
}

def curl_bytes(url: str, timeout=20) -> bytes | None:
    tmp = tempfile.NamedTemporaryFile(delete=False); tmp.close()
    try:
        r = subprocess.run(["curl.exe","-fsSL","--max-time",str(timeout),"-L","-A","Mozilla/5.0","-o",tmp.name,url], capture_output=True, text=True)
        if r.returncode != 0: return None
        data = open(tmp.name,"rb").read()
        return data if len(data) > 200 else None
    finally:
        try: os.unlink(tmp.name)
        except OSError: pass

def candidate_icon_urls(base: str) -> list[str]:
    base = base.rstrip("/")
    return [
        f"{base}/apple-touch-icon.png",
        f"{base}/apple-touch-icon-precomposed.png",
        f"{base}/apple-touch-icon-180x180.png",
        f"{base}/favicon-196x196.png",
        f"{base}/favicon-32x32.png",
        f"{base}/favicon.ico",
    ]

def parse_html_icons(html: bytes, base: str) -> list[str]:
    try: text = html.decode("utf-8", errors="ignore")
    except Exception: return []
    urls = []
    for m in re.finditer(r'<link[^>]+rel=["\'][^"\']*icon[^"\']*["\'][^>]*>', text, re.I):
        tag = m.group(0)
        hm = re.search(r'href=["\']([^"\']+)["\']', tag, re.I)
        if not hm: continue
        href = hm.group(1)
        if href.startswith("//"): href = "https:" + href
        elif href.startswith("/"): href = base.rstrip("/") + href
        elif not href.startswith("http"): href = base.rstrip("/") + "/" + href
        urls.append(href)
    return urls

def to_square(data: bytes, path: str) -> bool:
    try:
        img = Image.open(io.BytesIO(data)).convert("RGBA")
    except Exception:
        return False
    if min(img.size) < 48: return False
    bbox = img.getbbox()
    if bbox: img = img.crop(bbox)
    canvas = Image.new("RGBA", (SIZE, SIZE), (255,255,255,255))
    img.thumbnail((SIZE-64, SIZE-64), Image.Resampling.LANCZOS)
    canvas.alpha_composite(img, ((SIZE-img.width)//2, (SIZE-img.height)//2))
    out = Image.new("RGB", (SIZE, SIZE), (255,255,255))
    out.paste(canvas, mask=canvas.split()[-1])
    out.save(path, "PNG", optimize=True)
    print("  OK", os.path.basename(path), img.size, os.path.getsize(path))
    return True

def wordmark(path: str, color: str, title: str):
    img = Image.new("RGB", (SIZE, SIZE), (255,255,255))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((16,16,SIZE-16,SIZE-16), radius=48, outline="#E5E7EB", width=4)
    d.rounded_rectangle((16,16,SIZE-16,140), radius=48, fill=color)
    d.rectangle((16,80,SIZE-16,140), fill=color)
    try:
        f = ImageFont.truetype(r"C:\Windows\Fonts\malgunbd.ttf", 64)
        f2 = ImageFont.truetype(r"C:\Windows\Fonts\malgun.ttf", 36)
    except Exception:
        f = ImageFont.load_default(); f2 = f
    bbox = d.textbbox((0,0), title, font=f)
    tw = bbox[2]-bbox[0]
    # contrast text on brand bar
    ink = "#111111" if color.upper() in ("#FFCC00", "#FAE100") else "#FFFFFF"
    d.text(((SIZE-tw)/2, 48), title, font=f, fill=ink)
    sub = "공식 브랜드 식별용"
    bbox2 = d.textbbox((0,0), sub, font=f2)
    d.text(((SIZE-(bbox2[2]-bbox2[0]))/2, 280), sub, font=f2, fill="#6B7280")
    img.save(path, "PNG")
    print("  WORDMARK", os.path.basename(path))

ok=0
for key, bases in SITES.items():
    print(key)
    path = os.path.join(OUT, f"logo_{key}.png")
    done=False
    urls=[]
    for base in bases:
        urls += candidate_icon_urls(base)
        html = curl_bytes(base, timeout=15)
        if html:
            urls = parse_html_icons(html, base) + urls
    # prefer larger sounding assets first
    urls = list(dict.fromkeys(urls))
    for url in urls:
        data = curl_bytes(url)
        if data and to_square(data, path):
            done=True; ok+=1; break
    if not done:
        c,t = FALLBACK_COLORS[key]
        wordmark(path, c, t)

# custom
wordmark(os.path.join(OUT,"logo_custom.png"), "#607D8B", "직접입력")
print("downloaded", ok)
