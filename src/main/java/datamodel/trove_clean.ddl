create table Article (
    rel_path TEXT primary key,
    updated TEXT
) without rowid;
--GO
create table Object (
    rel_path TEXT references Article primary key,
    updated TEXT,
    check (rel_path NOT LIKE 'collections/%')
) without rowid;
--GO
create table ExtractedStringID (
    id TEXT primary key,
    updated TEXT
) without rowid;
--GO
create table CustomStringID (
    id TEXT primary key,
    updated TEXT
) without rowid;
--GO
create table Language (
    code VARCHAR(10) primary key,
    updated TEXT
) without rowid;
--GO
create table ExtractedString (
    lang VARCHAR(10) references Language(code) on update cascade on delete restrict,
    id TEXT references ExtractedStringID(id) on update cascade on delete cascade,
    content TEXT not null,
    updated TEXT,
    primary key (lang, id)
) without rowid;
--GO
create table CustomString (
    lang VARCHAR(10) references Language(code) on update cascade on delete restrict,
    id TEXT references CustomStringID(id) on update cascade on delete cascade,
    content TEXT not null,
    updated TEXT,
    primary key (lang, id)
) without rowid;
--GO
create table Bench (
    rel_path TEXT references Object(rel_path) on update cascade on delete cascade primary key,
    name_id TEXT unique references ExtractedStringID(id) on update cascade on delete set null,
    profession_name TEXT,
    updated TEXT
) without rowid;
--GO
create table BenchCategory (
    rel_path TEXT references Bench(rel_path) on update cascade on delete cascade,
    name_id TEXT unique references ExtractedStringID(id) on update cascade on delete cascade,
    bench_index INT not null,
    updated TEXT,
    primary key (rel_path, name_id),
    check (bench_index >= 0)
) without rowid;
--GO
create table Recipe (
    rel_path TEXT not null,
    bench_id TEXT references Bench(name_id) on update cascade on delete cascade, --note that SQLite allows nulls in keys
    cat_id TEXT references BenchCategory(name_id) on update cascade on delete set null,
    name TEXT unique not null,
    updated TEXT,
    primary key (rel_path, bench_id)
);
--GO
create table RecipeCost (
    rel_path TEXT references Recipe(rel_path) on update cascade on delete cascade,
    input_rel TEXT references Object(rel_path) on update cascade on delete restrict,
    input_count INT not null,
    updated TEXT,
    primary key (rel_path, input_rel),
    check (input_count >= 1)
) without rowid;
--GO
create table RecipeOutput (
    rel_path TEXT references Recipe(rel_path) on update cascade on delete cascade primary key,
    output_rel TEXT references Article(rel_path) on update cascade on delete restrict not null,
    output_count INT not null,
    updated TEXT,
    check (output_count >= 0)
) without rowid;
--GO
create table Collection (
    rel_path TEXT references Article(rel_path) on update cascade on delete cascade primary key,
    name_id TEXT references ExtractedStringID(id) on update cascade on delete set null,
    desc_id TEXT references ExtractedStringID(id) on update cascade on delete set null,
    trove_mr INT not null,
    geode_mr INT not null,
    updated TEXT,
    check (trove_mr >= 0),
    check (geode_mr >= 0)
) without rowid;
--GO
create table CollectionType (
    rel_path TEXT references Collection(rel_path) on update cascade on delete cascade,
    type TEXT,
    updated TEXT,
    primary key (rel_path, type),
    check (type in ('MOUNT', 'WINGS', 'BOAT', 'DRAGON', 'MAG'))
) without rowid;
--GO
create table CollectionProperty (
    rel_path TEXT references Collection(rel_path) on update cascade on delete cascade,
    prop TEXT,
    prop_val REAL not null,
    updated TEXT,
    primary key (rel_path, prop),
    check (prop in ('GROUND_MS', 'AIR_MS', 'GLIDE', 'WATER_MS', 'TURN_RATE',
                    'ACCEL', 'MAG_MS', 'POWER_RANK')),
    check (prop_val > 0)
) without rowid;
--GO
create table CollectionBuff (
    rel_path TEXT references Collection(rel_path) on update cascade on delete cascade,
    buff TEXT,
    buff_val REAL not null,
    updated TEXT,
    primary key (rel_path, buff),
    check (buff in ('PD', 'MD', 'MH', 'MH_PCT', 'CD', 'CH', 'AS', 'MF', 'EN',
                    'ER', 'ER_PCT', 'HR', 'HR_PCT', 'JP', 'LS', 'LT', 'PR')),
    check (buff_val > 0)
) without rowid;
--GO
create table Item (
    rel_path TEXT references Object(rel_path) on update cascade on delete cascade primary key,
    name_id TEXT references ExtractedStringID(id) on update cascade on delete set null,
    desc_id TEXT references ExtractedStringID(id) on update cascade on delete set null,
    tradable INTEGER default 1 not null,
    updated TEXT,
    check (tradable in (0, 1))
) without rowid;
--GO
create table Unlock (
    item_rel TEXT references Item(rel_path) on update cascade on delete cascade,
    col_rel TEXT references Collection(rel_path) on update cascade on delete cascade,
    updated TEXT,
    primary key (item_rel, col_rel)
) without rowid;
--GO
create table Lootbox (
    rel_path TEXT references Item(rel_path) on update cascade on delete cascade,
    rarity TEXT,
    output_rel TEXT references Object(rel_path) on update cascade on delete cascade,
    output_count INT,
    updated TEXT,
    primary key (rel_path, rarity, output_rel, output_count),
    check (rarity in ('COMMON', 'UNCOMMON', 'RARE')),
    check (output_count >= 1)
) without rowid;
--GO
create table Placeable (
    rel_path TEXT references Object(rel_path) on update cascade on delete cascade primary key,
    name_id TEXT references ExtractedStringID(id) on update cascade on delete set null,
    desc_id TEXT references ExtractedStringID(id) on update cascade on delete set null,
    tradable INTEGER default 1 not null,
    updated TEXT,
    check (tradable in (0, 1))
) without rowid;
--GO
create table Notes (
    rel_path TEXT references Article(rel_path) on update cascade on delete cascade,
    lang VARCHAR(10) references Language(code) on update cascade on delete restrict,
    string_id TEXT references CustomStringID(id) on update cascade on delete cascade not null,
    updated TEXT,
    primary key (rel_path, lang)
) without rowid;
--GO
create table Decon (
    input_rel TEXT references Object(rel_path) on update cascade on delete cascade,
    output_rel TEXT references Object(rel_path) on update cascade on delete cascade,
    output_count INT not null,
    updated TEXT,
    primary key (input_rel, output_rel),
    check (output_count > 0)
) without rowid;