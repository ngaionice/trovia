# Trovia - Data Extractor

This application allows the extraction of data from `.binfab` files, which are obtained by extracting the archives of
Trove.

## Current functionality

Extraction of

- benches & professions
    - categories in a bench
    - recipes in each category
- collections
    - name and description
    - inherent properties of the Collection (acting as wings, mounts, or stats granted, etc.)
    - blueprint
- equipment styles & skins
    - name and description
    - blueprint
- items
    - name and description
    - blueprint
    - collections unlocked by consuming this Item
    - additional properties (whether this is a lootbox, if it will decay after logout)
    - tradability
- placeables
    - name and description
    - blueprint
    - tradability
- strings/language files (English only currently)

The extracted data can be exported as JSON objects. Exported data can be re-imported into the application as a base for later extractions to compare against.

## Future (possible) additional functionality

- reading mastery values for Collections

## Notes

- this extractor works best with the folder structure obtained by extracting the game using the script `devtool_unpack_client.bat` found [here](https://gist.github.com/chrmoritz/c304dead49ce6a38653f).  
  If such folder structure is not available, users can select the 'non-standard folder structure' option on application start-up. However, comparison of existing and future data with data exported with this option selected using the application will be impossible unless they have the same absolute file paths.


