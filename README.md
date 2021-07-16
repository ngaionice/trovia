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

## Known issues

- If the folder structure required by the extractor is not satisfied, the extractor fails to extract any data even if the data itself is intact. Will fix in a future version.

## Notes

- this extractor assumes the folder structure obtained by extracting the game using the script `devtool_unpack_client.bat` found [here](https://gist.github.com/chrmoritz/c304dead49ce6a38653f). Deviations from this structure can lead to unsuccessful parsing.


