##Gear:

Since these are mostly manual, to keep things uniform, a format should be adhered to. 
This is the current format.

### Name: `Gear.name`

- is a string
- example: `gear_crystal_hat_lvl_5_name`
- breakdown:
    - `gear`/`module` prefix   - used to identify that these are gear (and that they are manual)
    - `crystal_hat_lvl_5`    - used to identify the object
    - `name`                 - used to identify that it's a name

### Description: `Gear.desc` 

- is a string
- example: `gear_crystal_hat_lvl_5_description`
- breakdown:
    - `gear`/`module` prefix
    - `crystal_hat_lvl_5`
    - `description`          - used to identify that it's a description

### Upgrade recipe: `Gear.recipe` 

- is a string array
- example: `recipe_gear_crystal_hat_lvl_5_upgrade_ii`
- breakdown:
    - `recipe` prefix    - used to identify that it's a recipe (duh)
    - `gear`/`module`
    - `crystal_hat_lvl_5`
    - `upgrade_ii  `      - used to identify tier `ii`'s upgrade, including the leading 0

### Stats upon upgrade: `Gear.stats` 
- is a string array
- example:`item_gear_crystal_hat_lvl_5_upgrade_ii`
- breakdown:
    - `item` - included for better functioning with the recipe class
    - `gear`/`module`
    - `crystal_hat_lvl_5`
    - `upgrade_ii`

Note that the key difference between `Gear.recipe` and `Gear.stats` is that recipe shows the upgrade **costs**,
while stat upgrade shows the **outcome** of the upgrade.

When processing the database, items with the `item_gear` prefix should never be shown, as they are
not obtainable, and are simply constructs to make things function better with modules and gear.