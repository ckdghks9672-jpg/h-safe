"""Replace black logo canvases with white plates so dark brand text stays readable."""
from PIL import Image, ImageDraw
from pathlib import Path

assets = Path(r"C:\Users\ckdgh\.cursor\projects\C-Users-ckdgh-AndroidStudioProjects-MyApplication\assets")
out_dir = Path(r"C:\Users\ckdgh\AndroidStudioProjects\MyApplication\automotive\src\main\res\drawable-xxhdpi")

mapping = {
    "logo_kb.png": "2e65e3ac-20e2-4865-b3ed-91e4b0fbe83c.png",
    "logo_lotte.png": "1490142c-b457-4a41-ae45-44da1b25881c.png",
    "logo_meritz.png": "ee3b4dfe-70f0-4b4b-a63d-048a86b29eac.png",
    "logo_nh.png": "55bcf372-af39-4f78-bc4f-2a5d5c04d2ac.png",
    "logo_samsungfire.png": "ce322edf-a699-4f40-8fa1-ad6eb0c95c5a.png",
    "logo_yebyeol.png": "cfb80a0b-460c-4d56-8440-09806c40f026.png",
    "logo_aig.png": "ba552566-1556-4af8-9115-e842ba060c2c.png",
    "logo_axa.png": "0c177823-5e69-43a1-8e16-fe33ceb30145.png",
    "logo_carrot.png": "bb08c923-207a-489e-8039-5000c27ee34c.png",
    "logo_db.png": "64885f24-9861-4721-bb55-8281f9b9e0c0.png",
    "logo_hana.png": "01a3d43f-8b30-467f-9177-929ab1fca181.png",
    "logo_hanwha.png": "d855309b-45bd-4938-84c2-1b404d5d3bc3.png",
    "logo_heungkuk.png": "3378be41-75b5-445d-a866-fda2cfc5938a.png",
    "logo_hyundai.png": "27a84add-f1b9-4642-9379-76975e42db15.png",
}


def black_to_white(im: Image.Image, thresh: int = 28) -> Image.Image:
    """Turn near-black canvas into white plate; keep brand ink (even dark gray)."""
    im = im.convert("RGBA")
    px = im.load()
    w, h = im.size
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            # Pure/near-black background only (avoid eating dark brand text ~40+)
            if r <= thresh and g <= thresh and b <= thresh:
                px[x, y] = (255, 255, 255, 255)
    return im


def content_bbox_nonwhite(im: Image.Image, pad: int = 10):
    """Crop away large empty white margins."""
    px = im.load()
    w, h = im.size
    l, t, r, b = w, h, 0, 0
    found = False
    for y in range(h):
        for x in range(w):
            r0, g0, b0, a0 = px[x, y]
            if a0 < 10:
                continue
            if r0 >= 250 and g0 >= 250 and b0 >= 250:
                continue
            found = True
            l = min(l, x)
            t = min(t, y)
            r = max(r, x)
            b = max(b, y)
    if not found:
        return (0, 0, w, h)
    return (
        max(0, l - pad),
        max(0, t - pad),
        min(w, r + pad + 1),
        min(h, b + pad + 1),
    )


def rounded_rect_mask(size, radius):
    mask = Image.new("L", size, 0)
    ImageDraw.Draw(mask).rounded_rectangle([0, 0, size[0] - 1, size[1] - 1], radius=radius, fill=255)
    return mask


def process_hanwha(src: Path) -> Image.Image:
    """Hanwha wordmark is ~RGB(5,0,0); lift it to charcoal before whitening bg."""
    im = Image.open(src).convert("RGBA")
    px = im.load()
    w, h = im.size
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            warm = r > 120 and r > g + 20 and r > b + 20  # orange rings
            peach = r > 160 and g > 120 and b > 80 and r >= g
            if warm or peach:
                continue
            if r + g + b == 0:
                px[x, y] = (255, 255, 255, 255)
            elif max(r, g, b) < 40:
                px[x, y] = (40, 40, 40, 255)  # readable text on white
            else:
                # leftover dark canvas → white
                if r <= 28 and g <= 28 and b <= 28:
                    px[x, y] = (255, 255, 255, 255)
    return im


def process(src: Path, dest: Path, canvas: int = 320, black_thresh: int = 28):
    if "hanwha" in dest.name:
        img = process_hanwha(src)
    else:
        img = black_to_white(Image.open(src).convert("RGBA"), thresh=black_thresh)

    img = img.crop(content_bbox_nonwhite(img, pad=12))

    pad = 24
    max_w = canvas - pad * 2
    max_h = canvas - pad * 2
    lw, lh = img.size
    scale = min(max_w / lw, max_h / lh)
    nw, nh = max(1, int(lw * scale)), max(1, int(lh * scale))
    img = img.resize((nw, nh), Image.Resampling.LANCZOS)

    base = Image.new("RGBA", (canvas, canvas), (255, 255, 255, 255))
    x = (canvas - nw) // 2
    y = (canvas - nh) // 2
    base.alpha_composite(img, (x, y))

    mask = rounded_rect_mask((canvas, canvas), 36)
    out = Image.new("RGBA", (canvas, canvas), (0, 0, 0, 0))
    out.paste(base, (0, 0))
    out.putalpha(mask)

    guide = Image.new("RGBA", (canvas, canvas), (0, 0, 0, 0))
    ImageDraw.Draw(guide).rounded_rectangle(
        [2, 2, canvas - 3, canvas - 3], radius=34, outline=(210, 214, 220, 255), width=2
    )
    out = Image.alpha_composite(out, guide)
    out.save(dest, optimize=True)


# Heungkuk purple text is ~40-60 luma; keep thresh 28 so text survives.
# Yebyeol dark gray ~60-80; survives.
for out_name, src_name in mapping.items():
    process(assets / src_name, out_dir / out_name)
    print("wrote", out_name)

print("DONE")
