#!/bin/bash

# Create simple placeholder icons using ImageMagick if available, otherwise create empty files
if command -v convert &> /dev/null; then
    # HDPI - 72x72
    convert -size 72x72 xc:'#6200EE' app/src/main/res/mipmap-hdpi/ic_launcher.png
    convert -size 72x72 xc:'#6200EE' app/src/main/res/mipmap-hdpi/ic_launcher_round.png
    
    # MDPI - 48x48
    convert -size 48x48 xc:'#6200EE' app/src/main/res/mipmap-mdpi/ic_launcher.png
    convert -size 48x48 xc:'#6200EE' app/src/main/res/mipmap-mdpi/ic_launcher_round.png
    
    # XHDPI - 96x96
    convert -size 96x96 xc:'#6200EE' app/src/main/res/mipmap-xhdpi/ic_launcher.png
    convert -size 96x96 xc:'#6200EE' app/src/main/res/mipmap-xhdpi/ic_launcher_round.png
    
    # XXHDPI - 144x144
    convert -size 144x144 xc:'#6200EE' app/src/main/res/mipmap-xxhdpi/ic_launcher.png
    convert -size 144x144 xc:'#6200EE' app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png
    
    # XXXHDPI - 192x192
    convert -size 192x192 xc:'#6200EE' app/src/main/res/mipmap-xxxhdpi/ic_launcher.png
    convert -size 192x192 xc:'#6200EE' app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png
else
    # Create 1x1 PNG as placeholder
    echo -e '\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01\x08\x02\x00\x00\x00\x90wS\xde\x00\x00\x00\x0cIDATx\x9cc\x00\x01\x00\x00\x05\x00\x01\r\n-\xb4\x00\x00\x00\x00IEND\xaeB`\x82' > app/src/main/res/mipmap-hdpi/ic_launcher.png
    cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-hdpi/ic_launcher_round.png
    cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-mdpi/ic_launcher.png
    cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-mdpi/ic_launcher_round.png
    cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-xhdpi/ic_launcher.png
    cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-xhdpi/ic_launcher_round.png
    cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-xxhdpi/ic_launcher.png
    cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png
    cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-xxxhdpi/ic_launcher.png
    cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png
fi

echo "Icons created"
