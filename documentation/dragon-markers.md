# Dragon buff identifiers:

Stats always start with `24` ($), then the middle numbers are variable, such that they have the format of

`24 aa bb [marker]`

## The markers:

```
mh: (flat/%)         6D 61 78 68 65 61 6C 74 68 46                   (maxhealthF)
crit dmg:            63 72 69 74 68 69 74 64 61 6D 61 67 65 46       (crithitdamageF)
crit dmg - old:      63 72 69 74 68 69 74 64 6D 67 46                (crithitdmgF)

crit hit:            63 72 69 74 63 68 61 6E 63 65 46                (critchanceF)
crit hit - old:      63 72 69 74 68 69 74 63 68 61 6E 63 65 46       (crithitchanceF)

phys dmg:            70 68 79 73 69 63 61 6C 64 61 6D 61 67 65 46    (physicaldamageF)
phys dmg - old:      64 61 6D 61 67 65 70 68 79 73 46                (damagephysF)

magi dmg:            73 70 65 6C 6C 64 61 6D 61 67 65 46             (spelldamageF)
magi dmg - old:      64 61 6D 61 67 65 73 70 65 6C 6C 46             (damagespellF)
magi dmg:            6D 61 67 69 63 64 61 6D 61 67 65 46             (magicdamageF)

attk spd:            61 74 74 61 63 6B 73 70 65 65 64 46             (attackspeedF)

mf:                  6D 61 67 69 63 66 69 6E 64 46                   (magicfindF)
mf - old:            6D 66 46                                        (mfF)

nrg reg: (flat/%)    65 6E 65 72 67 79 72 65 67 65 6E 46             (energyregenF)
max nrg:             6D 61 78 65 6E 65 72 67 79 46                   (maxenergyF)

hp reg:              68 65 61 6C 74 68 72 65 67 65 6E 46             (healthregenF)
hp reg - old:        68 70 72 65 67 65 6E 46                         (hpregenF)

mh %:                6D 61 78 68 65 61 6C 74 68 32 46                (maxhealth2F)
light:               6C 69 67 68 74 46                               (lightF)
jump:                6A 75 6D 70 46                                  (jumpF)
laser:               6D 69 6E 69 6E 67 46                            (miningF)

CD CC indicates that it's a multiplier (mh%, er%, hr%), shows up immediately after 24
note that attack speed, crit dmg, crit hit are not multipliers (%) here

if both mh and mh% are granted, then maxhealth2 kicks in, where mh2 is the %;
otherwise it is dependent on whether CD CC shows up
```