# Collection value calculation:

In this document, unless otherwise indicated with a `D` at the end of the number, all numbers are being represented in hexadecimal.

## The format:

### `aa bb`

This can be broken down into 2 parts; the `aa` part and the `bb` part.  
The `aa` part essentially gets added to the `bb` part, but there are specific rules that affect what multipliers have to be applied to the actual value calculation.

Part `bb` is dependent on part `aa`, so part `aa` will be outlined first.

### Part `aa`:

Ranges from `00` to `FF`. This can be split into 2 regions:
- `00 - 80`
- `81 - FF`

Note that `80` is `128D`.

The region `aa` is in affects the part determined by `aa`.
- in range `00` - `80`: the part determined by `aa` gets multiplied by 1.
- in range `81` - `FF`: the part determined by `aa` gets multiplied by 2.

### Part `bb`:

Seen with ranges from `42` to `43`. (possibly `44` as well)

`43` is used as a reference here. 
- `43` has a value of `128D`, per 1 increase in `bb` leads to quadruples the decimal value represented by `bb` (and `aa`);  
  so `42` (or `00 42`) has a value of `32D`, while `44` (or `00 44`) has a value of `512D`.
- the difference of `bb` from `43` also determines the overall multiplier of the total value of `aa bb`:
    - the total value gets divided by `4D^(43-bb)`; so if `bb` = `42`, the total value (`aa` + `bb`) gets divided by 4. (see examples below)

## Formulae:

### Case 1: `aa > 80`

`2D * (aa - 128D)/4D^(43-bb)`

### Case 2: `aa <= 80`

`(128D + aa)/4^(43-bb)`

## Examples:

### Example 1: `B4 42`

Since the first bit is larger than `80`, this falls under case 1. So we have

`128/4^(43-42) + 128/4 + 2*(180-128)/4 = 90`

### Example 2: `40 41`
Since the first bit is smaller than `80`, this falls under case 2. So we have

`128/4^(43-41) + 64 * (1/16) = 12`

### More examples: 

#### `20 42`
Decimal value: `128/4 + 32 * (1/4) = 40`

#### `C8 42`
Decimal value: `128/4 + 128/4 + 2*(200-128)/4 = 100`

#### `CC 3D`
Decimal value: `128/4^6 + 128/4^6 + 2*(204-128)/4^6 = 0.099609375` (= 0.1 when represented in-game)