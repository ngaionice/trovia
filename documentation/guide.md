# Usage Guide

This extractor extracts data from `binfab`s with specific formats; using an incorrect parser may lead to misclassification of data, or worse, failure to extract the data. 

So this guide aims to provide a quick overview on which files/directories can be used with each parse type (in the parse section of the application). Each header in this article corresponds to one parse type in the application.

`binfab` is the file format used to store data in the game, and the article will use `binfab` as a noun to refer to such files.

**Important note**: this guide assumes the standard structure obtained from using the unpacking tool linked in the readme.

## Benches

Should be used in the directory `prefabs/placeables` on binfabs with names ending in `_interactive`.

## Collections

Should be used in the sub-directories of `prefabs/collections`.

## Collection indices

Should be used in the files in `prefabs/collections` with a file name starting in `collection_`. It should however not be used on files in this directory's sub-directories.

## Gear styles

Should be used in these files in `prefabs/loot` (all with extensions of `.binfab`):
```
hat
face
weapon_bow
weapon_fist
weapon_melee
weapon_pistol
weapon_spear
weapon_staff
```

## Items

Should be used in the files in `prefabs/item`.

## Placeables

Should be used in the files in `prefabs/placeable`. Note that there will be a lot of parse failures in this directory, as a lot of files have irregular formats for objects that are not for player use. It also does not work on files ending in `_interactive` (use the Benches parser for these files).

## Professions

Should be used in the files in `prefabs/professions`.

## Recipes

Should be used in the files in `prefabs/recipes`.

## Skins

Should be used in the files in `prefabs/skins`, but not the files in its sub-directory `prefabs/skins/secondaryskins`.

## Strings

Should be used in the files in `languages`.  
In v0.1.0, there is an issue with the file `prefabs_item_tome` due to an oversight from the developers of Trove; this issue was bypassed for English strings but not other languages in v0.1.0, and this fix has been generalized to all languages in an upcoming version.