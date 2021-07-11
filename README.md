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

The extracted data can be exported as JSON objects.

## Future (possible) additional functionality

- reading mastery values for Collections
- allow re-import of exported data to track changes between parses

## Known issues

- if an inappropriate parsing type is selected, the parsing may get stuck. This can be 'fixed' by going to the export/logs screen and back. Will be fixed in a future version.
- if there is too much text in the logs area, screen tearing will very likely occur. This is likely an issue with using TextArea as the method to display logs. Will switch to ListView in a future version.

## Notes

- this extractor assumes the folder structure obtained by extracting the game using the script `devtool_unpack_client.bat` found [here](https://gist.github.com/chrmoritz/c304dead49ce6a38653f). Deviations from this structure can lead to unsuccessful parsing.


