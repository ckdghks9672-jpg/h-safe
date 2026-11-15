"""Replace picto_* with user hi-res assets. No redraw — black→transparent + white-alpha for tint."""
from pathlib import Path
from shutil import copyfile

import numpy as np
from PIL import Image

assets = Path(r"C:\Users\ckdgh\.cursor\projects\C-Users-ckdgh-AndroidStudioProjects-MyApplication\assets")
out = Path(r"C:\Users\ckdgh\AndroidStudioProjects\MyApplication\automotive\src\main\res\drawable-xxhdpi")

# Identified from the 23 uploads (skip car+flame #7, orange warning #12, duplicate head-on #18)
ordered = [
    ("14b86f8d-55cf-4c14-ada7-513571f7b49c.png", "picto_unconscious.png"),
    ("2dda62fb-b35a-406d-bcbb-9a3cf3bf80e7.png", "picto_breathing.png"),
    ("b5bce636-ee3a-444f-84c5-3c277d238f09.png", "picto_bleeding.png"),
    ("6afaa5e6-7ea4-42d0-8609-8b343e1130f5.png", "picto_entrapment.png"),
    ("ce629f6d-b6c2-4a29-a5c4-6a5c5696f0fe.png", "picto_chest_pain.png"),
    ("124dfcf2-2ae4-4078-89ae-b417b73ff6a2.png", "picto_multi_casualty.png"),
    ("0c7cd17c-724c-44e4-a3a0-edcc452e5651.png", "picto_overheat.png"),
    ("76e9cac5-2caf-463f-9c63-91563feec454.png", "picto_battery_fire.png"),
    ("303eefe4-3b91-421b-b70f-093695a46064.png", "picto_fuel_leak.png"),
    ("6471171d-12da-4f63-a055-d48710199ecb.png", "picto_smoke.png"),
    ("bdeccb5b-6d41-4edf-aea5-e4a98c9c48f9.png", "picto_ambulance.png"),
    ("d3b2e8e7-2696-4e4c-8c59-01b931971923.png", "picto_fire.png"),
    ("2beb8a38-f6c7-42f4-8d3c-af61d4d1b617.png", "picto_head_on.png"),
    ("9a7fe442-848c-4a92-b99f-6f070cb5a93a.png", "picto_rear_end.png"),
    ("8a37c1d3-2aae-4ee4-b0cf-ea322066bab8.png", "picto_side_contact.png"),
    ("b0485cec-5373-4499-9cc1-6eb11f049df1.png", "picto_single_vehicle.png"),
    ("b0781fcc-b425-408d-8ba4-882127236a9e.png", "picto_property_damage.png"),
    ("3e4fb884-aa59-4765-b833-2da78fc01ba9.png", "picto_hit_and_run.png"),
    ("3d618abd-6a82-47ce-bb9e-cdb14a3f234f.png", "picto_person_injury.png"),
    ("445c03fe-143c-462f-9174-b8e6e00fc667.png", "picto_other.png"),
]


def to_tintable_hires(src: Path, dest: Path, canvas: int = 512):
    im = Image.open(src).convert("RGBA")
    arr = np.asarray(im).astype(np.float32)
    rgb = arr[..., :3]
    luma = rgb.mean(axis=2)
    # near-black canvas → transparent; stroke → white with alpha from luma
    alpha = np.where(luma < 28, 0, np.clip(40 + luma * 0.9, 0, 255))
    out_arr = np.zeros_like(arr)
    out_arr[..., 0] = 255
    out_arr[..., 1] = 255
    out_arr[..., 2] = 255
    out_arr[..., 3] = alpha
    im = Image.fromarray(out_arr.astype(np.uint8), "RGBA")

    bbox = im.split()[-1].getbbox()
    if bbox:
        im = im.crop(bbox)

    side = max(im.size) + max(24, int(max(im.size) * 0.06))
    square = Image.new("RGBA", (side, side), (0, 0, 0, 0))
    square.alpha_composite(im, ((side - im.size[0]) // 2, (side - im.size[1]) // 2))
    square.resize((canvas, canvas), Image.Resampling.LANCZOS).save(dest, optimize=True)
    print("wrote", dest.name, "from", src.name, "src", Image.open(src).size)


for src_name, dest_name in ordered:
    to_tintable_hires(assets / src_name, out / dest_name)

print("DONE", len(ordered))
