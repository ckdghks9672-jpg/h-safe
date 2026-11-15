"""Install H-SAFE launcher + in-app logos from provided assets."""
from PIL import Image
from pathlib import Path

assets = Path(r"C:\Users\ckdgh\.cursor\projects\C-Users-ckdgh-AndroidStudioProjects-MyApplication\assets")
res = Path(r"C:\Users\ckdgh\AndroidStudioProjects\MyApplication\automotive\src\main\res")

# Prefer latest uploads
wordmark_src = assets / "1e4027c6-bf06-496e-b76f-0219a5241b2a.png"
icon_src = assets / "c1fc8caf-c2f6-4b0e-a1ad-841da1ef179d.png"
if not wordmark_src.exists():
    wordmark_src = assets / "f206cd35-7fd4-4cad-827e-ffcb2e1959c1.png"
if not icon_src.exists():
    icon_src = assets / "db238b5b-178e-47b0-b9e3-6f5b365c0a41.png"


def knock_black_to_transparent(im: Image.Image, thresh: int = 18) -> Image.Image:
    im = im.convert("RGBA")
    px = im.load()
    w, h = im.size
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            if r <= thresh and g <= thresh and b <= thresh:
                px[x, y] = (0, 0, 0, 0)
    return im


def content_bbox(im: Image.Image, pad: int = 8):
    bbox = im.split()[-1].getbbox()
    if not bbox:
        return (0, 0, im.width, im.height)
    l, t, r, b = bbox
    return max(0, l - pad), max(0, t - pad), min(im.width, r + pad), min(im.height, b + pad)


def fit_square(im: Image.Image, size: int, bg=(0, 27, 68, 255)) -> Image.Image:
    """Place image in square canvas. Default navy matches brand icon."""
    canvas = Image.new("RGBA", (size, size), bg)
    im = im.convert("RGBA")
    # scale to fit with small margin
    margin = int(size * 0.08)
    max_side = size - margin * 2
    scale = min(max_side / im.width, max_side / im.height)
    nw, nh = max(1, int(im.width * scale)), max(1, int(im.height * scale))
    im = im.resize((nw, nh), Image.Resampling.LANCZOS)
    canvas.alpha_composite(im, ((size - nw) // 2, (size - nh) // 2))
    return canvas


# --- App icon (squircle H mark) ---
icon = Image.open(icon_src).convert("RGBA")
# Already has navy plate; just normalize to square PNG densities
xx = res / "drawable-xxhdpi"
xx.mkdir(parents=True, exist_ok=True)
icon_mark = icon.copy()
# Crop near edges if huge padding
# Use as-is for brand fidelity
icon_512 = fit_square(icon_mark, 512, bg=(0, 27, 68, 255))
icon_512.save(xx / "logo_hsafe_icon.png", optimize=True)
icon_512.save(res / "drawable" / "ic_launcher_foreground.png", optimize=True)

# mipmap densities (legacy + round)
densities = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}
for folder, size in densities.items():
    d = res / folder
    d.mkdir(parents=True, exist_ok=True)
    out = fit_square(icon_mark, size, bg=(0, 27, 68, 255)).convert("RGBA")
    # save png (replace webp)
    out.save(d / "ic_launcher.png", optimize=True)
    out.save(d / "ic_launcher_round.png", optimize=True)
    # remove old webp to avoid conflict if both present - Android prefers one
    for old in (d / "ic_launcher.webp", d / "ic_launcher_round.webp"):
        if old.exists():
            old.unlink()

# solid navy background drawable replacement - keep xml color if possible; also write png
navy = Image.new("RGBA", (108, 108), (0, 27, 68, 255))
navy.save(res / "drawable" / "ic_launcher_background.png", optimize=True)

# --- Wordmark for in-app (navy on transparent, then white plate version) ---
wm = knock_black_to_transparent(Image.open(wordmark_src))
wm = wm.crop(content_bbox(wm, pad=12))
# transparent wordmark
wm_h = 160
scale = wm_h / wm.height
wm_resized = wm.resize((max(1, int(wm.width * scale)), wm_h), Image.Resampling.LANCZOS)
wm_resized.save(xx / "logo_hsafe_wordmark.png", optimize=True)

# white-plate wordmark for dark/ambiguous surfaces
pad = 24
plate = Image.new("RGBA", (wm_resized.width + pad * 2, wm_resized.height + pad * 2), (255, 255, 255, 255))
plate.alpha_composite(wm_resized, (pad, pad))
plate.save(xx / "logo_hsafe_wordmark_plate.png", optimize=True)

# compact mark only for header chip (extract left H from icon already)
fit_square(icon_mark, 192, bg=(0, 27, 68, 255)).save(xx / "logo_hsafe_mark.png", optimize=True)

print("icon", icon_src.name)
print("wordmark", wordmark_src.name)
print("DONE")
