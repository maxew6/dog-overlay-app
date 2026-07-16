"""
One-off helper (not part of the app) used to generate simple placeholder
launcher icon PNGs so the project has no missing resources.
Run once locally: python3 tools_gen_icons.py
Feel free to delete this file and replace icons with your own art.
"""
from PIL import Image, ImageDraw
import os

SIZES = {
    "mdpi": 48,
    "hdpi": 72,
    "xhdpi": 96,
    "xxhdpi": 144,
    "xxxhdpi": 192,
}

COAT = (201, 138, 75, 255)
COAT_DARK = (169, 104, 47, 255)
EAR = (138, 90, 46, 255)
BELLY = (241, 217, 181, 255)
NOSE = (62, 39, 35, 255)
BG = (255, 224, 178, 255)

OUT_ROOT = "app/src/main/res"


def draw_dog_face(size: int) -> Image.Image:
    scale = 4
    s = size * scale
    img = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)

    # circular background
    d.ellipse((0, 0, s, s), fill=BG)

    cx, cy = s * 0.5, s * 0.56
    head_r = s * 0.30

    # ears
    d.polygon([
        (cx - head_r * 1.05, cy - head_r * 0.2),
        (cx - head_r * 1.55, cy - head_r * 1.55),
        (cx - head_r * 0.25, cy - head_r * 0.75),
    ], fill=EAR)
    d.polygon([
        (cx + head_r * 1.05, cy - head_r * 0.2),
        (cx + head_r * 1.55, cy - head_r * 1.55),
        (cx + head_r * 0.25, cy - head_r * 0.75),
    ], fill=EAR)

    # head
    d.ellipse((cx - head_r, cy - head_r, cx + head_r, cy + head_r), fill=COAT)

    # muzzle
    mz_w, mz_h = head_r * 1.05, head_r * 0.75
    d.ellipse(
        (cx - mz_w / 2, cy + head_r * 0.15, cx + mz_w / 2, cy + head_r * 0.15 + mz_h),
        fill=BELLY,
    )

    # nose
    nr = head_r * 0.16
    d.ellipse((cx - nr, cy + head_r * 0.35, cx + nr, cy + head_r * 0.35 + nr * 1.6), fill=NOSE)

    # eyes
    er = head_r * 0.09
    d.ellipse((cx - head_r * 0.42 - er, cy - er, cx - head_r * 0.42 + er, cy + er), fill=NOSE)
    d.ellipse((cx + head_r * 0.42 - er, cy - er, cx + head_r * 0.42 + er, cy + er), fill=NOSE)

    img = img.resize((size, size), Image.LANCZOS)
    return img


def circle_mask(img: Image.Image) -> Image.Image:
    size = img.size
    mask = Image.new("L", size, 0)
    d = ImageDraw.Draw(mask)
    d.ellipse((0, 0, size[0], size[1]), fill=255)
    out = img.copy()
    out.putalpha(mask)
    return out


def main():
    for density, px in SIZES.items():
        folder = os.path.join(OUT_ROOT, f"mipmap-{density}")
        os.makedirs(folder, exist_ok=True)
        square = draw_dog_face(px)
        square.save(os.path.join(folder, "ic_launcher.png"))
        circle_mask(square).save(os.path.join(folder, "ic_launcher_round.png"))
    print("Icons generated.")


if __name__ == "__main__":
    main()
