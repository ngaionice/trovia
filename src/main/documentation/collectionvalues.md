## Collection value calculation:

format: `aa bb`
`H` at the end denotes hexadecimal representation of the value; `D` is decimal

### `bb` bit:
- reference base = `43H`; for `00H - 80H` in aa, per increase of 1 in `aa` = `+1D` overall value
- `43` in hex = x1 multiplier for `00 - 80`
- `43` = base value of `128D`; 42 = base value `32D`; 44 = base value `512D`
- per 1 decrease in bb, overall value is divided by `4D`

### `aa` bit:
- `00 - 80`; `81 - FF`
- `00 - 80` bit gets a multiplier of x1
- `81 - FF` bit gets a multiplier of x2

### overall value calculation:

#### case 1: aa > 80 (H)
- value = `128D/4D^(43H-bbH) + 128D/4D^(43H-bbH) + 2D * (aaD - 128D)/4D^(43H-bbH)`
- value (without the notation) = `128/4^(43-bb) + 128/4^(43-bb) + 2 * (aa - 128)/4^(43-bb)`

#### case 2: aa <= 80 (H)
- value = `128D/4D^(43H-bbH) + aaH * 1D/(2D^(43H-bbH))`
- value (without the notation) = `128/4^(43-bb) + aa * 1/(4^(43-bb))`

### examples:
- `B4 42` -> case 1
- `128/4^(43-42) + 128/4 + 2*(180-128)/4 = 90`

- `40 41` -> case 2
- `128/4^(43-41) + 64 * (1/16) = 12`

- `20 42`
- `128/4 + 32 * (1/4) = 40`

- `C8 42`
- `128/4 + 128/4 + 2*(200-128)/4 = 100`

- `CC 3D`
- `128/4^6 + 128/4^6 + 2*(204-128)/4^6 = 0.099609375` (= 0.1 when represented in-game)