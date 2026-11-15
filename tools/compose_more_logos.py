from PIL import Image, ImageDraw, ImageFont
import os

res = r"C:\Users\ckdgh\AndroidStudioProjects\MyApplication\automotive\src\main\res\drawable-xxhdpi"
font = ImageFont.truetype(r"C:\Windows\Fonts\malgunbd.ttf", 30)

mapping = {
    "nh": ("favicon_nh.png", "농협손해보험", "#0B6E4F"),
    "kakao": ("favicon_kakao.png", "카카오페이손보", "#FAE100"),
}

for company_id, (fav_name, title, accent) in mapping.items():
    fav_path = os.path.join(res, fav_name)
    if not os.path.exists(fav_path):
        print("skip", company_id)
        continue
    fav = Image.open(fav_path).convert("RGBA").resize((96, 96), Image.Resampling.LANCZOS)
    img = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((4, 4, 252, 252), radius=28, fill="#FFFFFF", outline=accent, width=4)
    img.alpha_composite(fav, ((256 - 96) // 2, 36))
    bbox = d.textbbox((0, 0), title, font=font)
    tw = bbox[2] - bbox[0]
    d.text(((256 - tw) / 2, 160), title, font=font, fill="#212121")
    out = os.path.join(res, f"logo_{company_id}.png")
    img.save(out, "PNG")
    print("composed", out)

for name in list(os.listdir(res)):
    if name.startswith("favicon_"):
        os.remove(os.path.join(res, name))
        print("removed", name)
print("done")
