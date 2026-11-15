"""Convert black-bg line pictograms to white-alpha drawables for ColorFilter.tint."""
from PIL import Image
from pathlib import Path

assets = Path(r"C:\Users\ckdgh\.cursor\projects\C-Users-ckdgh-AndroidStudioProjects-MyApplication\assets")
out = Path(r"C:\Users\ckdgh\AndroidStudioProjects\MyApplication\automotive\src\main\res\drawable-xxhdpi")

# attachment order → drawable name
ordered = [
    ("7ed955c6-5e7e-4d67-b677-5c767baf452f.png", "picto_single_vehicle.png"),
    ("49677cc9-4e69-40ee-9250-3fc8a3822df2.png", "picto_property_damage.png"),
    ("ff47a1c0-db1b-4639-9e0e-37e14362c710.png", "picto_person_injury.png"),
    ("ea76bd22-baea-4c87-9e74-7c0173b19d7f.png", "picto_hit_and_run.png"),
    ("7308ba7a-c955-4d25-a17e-c0a21c66c791.png", "picto_other.png"),
    ("ef141f94-fcbe-40f0-b9eb-63635acfa8c7.png", "picto_unconscious.png"),
    ("7fb09387-023c-4a14-a6f9-59e6a7e6bfc3.png", "picto_breathing.png"),
    ("ab262095-1195-4346-8a28-4e816538dc32.png", "picto_ambulance.png"),
    ("b23b213e-a46b-4e67-90da-2353e86e9991.png", "picto_fire.png"),
    ("d9c17961-83a5-4e21-b069-5dfbfae223ad.png", "picto_rear_end.png"),
    ("8f1c2fae-46be-4dce-b25c-b02c56be541a.png", "picto_head_on.png"),
    ("8015ed3e-11e0-4da5-a725-943d8fd56b17.png", "picto_side_contact.png"),
    ("507d3b44-a121-4fbf-ade3-cb216e521f11.png", "picto_multi_casualty.png"),
    ("7d46ede7-952a-4e93-a428-0cfa9f6fa97e.png", "picto_battery_fire.png"),
    ("0a9c9709-4545-4ec7-a794-755c753ea432.png", "picto_overheat.png"),
    ("7e493caf-02e2-4d17-894c-a8375f395296.png", "picto_bleeding.png"),
    ("9880f9b5-dfeb-4536-872e-3304754cbf3c.png", "picto_fuel_leak.png"),
    ("36bb9aeb-7b68-449a-a895-3931df0ed318.png", "picto_chest_pain.png"),
    ("1f0ccdd6-f444-49f5-a7a3-e9f1606990d4.png", "picto_entrapment.png"),
]


def to_tintable(src: Path, dest: Path, canvas: int = 192):
    im = Image.open(src).convert("RGBA")
    px = im.load()
    w, h = im.size
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            luma = (r + g + b) / 3
            if luma < 28:
                px[x, y] = (0, 0, 0, 0)
            else:
                # keep stroke strength via alpha from luma
                alpha = min(255, int(40 + luma * 0.9))
                px[x, y] = (255, 255, 255, alpha)

    # crop to content
    bbox = im.split()[-1].getbbox()
    if bbox:
        im = im.crop(bbox)

    # pad to square then scale
    side = max(im.size) + 16
    square = Image.new("RGBA", (side, side), (0, 0, 0, 0))
    square.alpha_composite(im, ((side - im.size[0]) // 2, (side - im.size[1]) // 2))
    out_im = square.resize((canvas, canvas), Image.Resampling.LANCZOS)
    out_im.save(dest, optimize=True)
    print("wrote", dest.name)


for src_name, dest_name in ordered:
    to_tintable(assets / src_name, out / dest_name)

# smoke shares overheat art
from shutil import copyfile
copyfile(out / "picto_overheat.png", out / "picto_smoke.png")
print("wrote picto_smoke.png (from overheat)")
print("DONE", len(ordered) + 1)
