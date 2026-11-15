from PIL import Image, ImageDraw, ImageFont
import os

res = r"C:\Users\ckdgh\AndroidStudioProjects\MyApplication\automotive\src\main\res\drawable-xxhdpi"
font_path = r"C:\Windows\Fonts\malgunbd.ttf"
font = ImageFont.truetype(font_path, 30)
font_small = ImageFont.truetype(font_path, 18)

mapping = {
    "samsungfire": ("favicon_samsungfire2.png", "삼성화재", "#1428A0"),
    "hyundai": ("favicon_hyundai.png", "현대해상", "#C8102E"),
    "db": ("favicon_db.png", "DB손해보험", "#00A3E0"),
    "kb": ("favicon_kb.png", "KB손해보험", "#FFCC00"),
    "meritz": ("favicon_meritz.png", "메리츠화재", "#EE3124"),
    "hana": ("favicon_hana.png", "하나손해보험", "#008485"),
    "carrot": ("favicon_carrot.png", "캐롯손해보험", "#FF6A00"),
    "aig": ("favicon_aig.png", "AIG손해보험", "#0033A0"),
}

for company_id, (fav_name, title, accent) in mapping.items():
    fav_path = os.path.join(res, fav_name)
    if not os.path.exists(fav_path):
        print("skip", company_id)
        continue
    fav = Image.open(fav_path).convert("RGBA")
    # upscale tiny favicons with nearest then smooth
    fav = fav.resize((96, 96), Image.Resampling.NEAREST).resize((96, 96), Image.Resampling.LANCZOS)
    img = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((4, 4, 252, 252), radius=28, fill="#FFFFFF", outline=accent, width=4)
    # place favicon
    fx, fy = (256 - 96) // 2, 36
    img.alpha_composite(fav, (fx, fy))
    bbox = d.textbbox((0, 0), title, font=font)
    tw = bbox[2] - bbox[0]
    d.text(((256 - tw) / 2, 160), title, font=font, fill="#212121")
    out = os.path.join(res, f"logo_{company_id}.png")
    img.save(out, "PNG")
    print("composed", out, os.path.getsize(out))

# cleanup favicon temp files so they are not packaged as unused drawables with invalid names
for name in os.listdir(res):
    if name.startswith("favicon_"):
        os.remove(os.path.join(res, name))
        print("removed", name)
print("done")
