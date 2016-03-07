## Overview
DataDumper will dump block or item information as soon as Forge finishes loading. There is no GUI, so all possible exports will be saved in the dump folder of your Minecraft installation.

## Requirements
Minecraft 1.7.10
FML 1.7.10-10.13.4.1448
Although, this can probably be adapted to other versions with little effort.

## Installation
Place the .jar in yours mods directory.

## Usage
### jMc2Obj
The blocks.conf and texsplit_1.6.conf files can be used instead of the originals to export vanilla and modded blocks with textures. Note that the file is texsplit_1.6.conf however this actually works with 1.7.10 by default. This is not perfect, however it should get you well on your way to a modded export. Currently, anything that's not a normal block has to be hardcoded to export as such. Some blocks, such as saplings, can be matched automatically, but if I don't support the block yet, I'm sure you'll find it easy to modify the exported conf files. Finally, you'll need to make a texture pack where you insert all of the mods' folders (really, you can delete everything besides assets/*/textures/blocks) into a single .zip for jMc2Obj to load. It's best to use the single texture and single material options.