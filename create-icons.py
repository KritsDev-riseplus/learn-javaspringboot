#!/usr/bin/env python3
from PIL import Image, ImageDraw
import os

# Create icons directory
icon_dir = '/Users/natthida/Desktop/learn-tdid/learn-javaspringboot/src/main/resources/static/images/icons'
os.makedirs(icon_dir, exist_ok=True)

# Computer icon (36x36)
img = Image.new('RGBA', (36, 36), (0, 0, 0, 0))
draw = ImageDraw.Draw(img)
# Monitor body
draw.rectangle([2, 6, 34, 28], outline='#000000', width=2)
# Stand base
draw.rectangle([10, 32, 26, 34], fill='#000000')
# Stand neck
draw.rectangle([16, 28, 20, 32], fill='#000000')
# Power indicator dot
draw.ellipse([28, 8, 32, 12], fill='#4a6fa5')
img.save(os.path.join(icon_dir, 'icon-computer.png'), 'PNG')
print("Created icon-computer.png")

# eToken/USB icon (36x36)
img = Image.new('RGBA', (36, 36), (0, 0, 0, 0))
draw = ImageDraw.Draw(img)
# USB body
draw.rectangle([12, 4, 24, 32], outline='#000000', width=2)
# USB chip/screen
draw.rectangle([15, 8, 21, 16], outline='#000000', width=1)
# Divider line
draw.line([(12, 18), (24, 18)], fill='#000000', width=2)
# LED indicator
draw.ellipse([16, 22, 20, 26], fill='#4a6fa5')
img.save(os.path.join(icon_dir, 'icon-etoken.png'), 'PNG')
print("Created icon-etoken.png")

# HSM icon (36x36) 
img = Image.new('RGBA', (36, 36), (0, 0, 0, 0))
draw = ImageDraw.Draw(img)
# HSM body (server-like box)
draw.rectangle([4, 8, 32, 28], outline='#000000', width=2)
# Left module (circle)
draw.ellipse([9, 14, 15, 20], outline='#000000', width=1)
draw.ellipse([11, 16, 13, 18], fill='#4a6fa5')
# Right module (circle)
draw.ellipse([21, 14, 27, 20], outline='#000000', width=1)
draw.ellipse([23, 16, 25, 18], fill='#4a6fa5')
# Connection lines top
draw.line([(12, 14), (12, 10)], fill='#000000', width=1)
draw.line([(24, 14), (24, 10)], fill='#000000', width=1)
# Connection lines bottom
draw.line([(12, 20), (12, 26)], fill='#000000', width=1)
draw.line([(24, 20), (24, 26)], fill='#000000', width=1)
img.save(os.path.join(icon_dir, 'icon-hsm.png'), 'PNG')
print("Created icon-hsm.png")

print("\nAll icons created successfully with visible content!")
