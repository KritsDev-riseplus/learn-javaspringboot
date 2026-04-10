#!/bin/bash
# Create simple icon images for email template
# Using ImageMagick to create small PNG icons

# Create icons directory
mkdir -p /Users/natthida/Desktop/learn-tdid/learn-javaspringboot/src/main/resources/static/images/icons

# Computer icon (36x36)
convert -size 36x36 xc:transparent \
  -stroke "#000000" -strokewidth 2 -fill none \
  -draw "rectangle 2,6 34,28" \
  -draw "rectangle 14,28 22,32" \
  -draw "rectangle 10,32 26,34" \
  -fill "#000000" -draw "circle 30,10 31.5,10" \
  /Users/natthida/Desktop/learn-tdid/learn-javaspringboot/src/main/resources/static/images/icons/icon-computer.png

# eToken/USB icon (36x36)
convert -size 36x36 xc:transparent \
  -stroke "#000000" -strokewidth 2 -fill none \
  -draw "rectangle 12,4 24,32 round 2,2" \
  -draw "rectangle 15,8 21,16 round 1,1" \
  -strokewidth 1.5 \
  -draw "line 12,18 24,18" \
  -fill "#000000" -draw "circle 18,24 20,24" \
  /Users/natthida/Desktop/learn-tdid/learn-javaspringboot/src/main/resources/static/images/icons/icon-etoken.png

# HSM icon (36x36)
convert -size 36x36 xc:transparent \
  -stroke "#000000" -strokewidth 2 -fill none \
  -draw "rectangle 4,8 32,28 round 2,2" \
  -draw "circle 12,18 15,18" \
  -draw "circle 24,18 27,18" \
  -strokewidth 1.5 \
  -draw "line 12,14 12,12" \
  -draw "line 24,14 24,12" \
  -draw "line 12,22 12,24" \
  -draw "line 24,22 24,24" \
  /Users/natthida/Desktop/learn-tdid/learn-javaspringboot/src/main/resources/static/images/icons/icon-hsm.png

echo "Icons created successfully"
