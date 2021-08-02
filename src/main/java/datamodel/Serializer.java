package datamodel;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import datamodel.objects.Collection;
import datamodel.objects.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class Serializer {

    public Gson getSerializer(boolean usePrettyPrint) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Bench.class, new BenchSerializer());
        builder.registerTypeAdapter(Collection.class, new CollectionSerializer());
        builder.registerTypeAdapter(CollectionIndex.class, new CollectionIndexSerializer());
        builder.registerTypeAdapter(GearStyleType.class, new GearStyleTypeSerializer());
        builder.registerTypeAdapter(Item.class, new ItemSerializer());
        builder.registerTypeAdapter(Placeable.class, new PlaceableSerializer());
        builder.registerTypeAdapter(Recipe.class, new RecipeSerializer());
        builder.registerTypeAdapter(Skin.class, new SkinSerializer());
        builder.registerTypeAdapter(Strings.class, new StringsSerializer());

        builder.serializeNulls();
        if (usePrettyPrint) builder.setPrettyPrinting();

        return builder.create();
    }

    public void writeBenches(JsonWriter writer, Gson serializer, Map<String, Bench> benches) throws IOException {
        writer.name("benches");
        writer.beginObject();
        for (Map.Entry<String, Bench> entry : benches.entrySet()) {
            writer.name(entry.getKey()).jsonValue(serializer.toJson(entry.getValue()));
        }
        writer.endObject();
    }

    public void writeCollections(JsonWriter writer, Gson serializer, Map<String, Collection> collections) throws IOException {
        writer.name("collections");
        writer.beginObject();
        for (Map.Entry<String, Collection> entry : collections.entrySet()) {
            writer.name(entry.getKey()).jsonValue(serializer.toJson(entry.getValue()));
        }
        writer.endObject();
    }

    public void writeCollectionIndices(JsonWriter writer, Gson serializer, Map<String, CollectionIndex> indices) throws IOException {
        writer.name("collection_indices");
        writer.beginObject();
        for (Map.Entry<String, CollectionIndex> entry : indices.entrySet()) {
            writer.name(entry.getKey()).jsonValue(serializer.toJson(entry.getValue()));
        }
        writer.endObject();
    }

    public void writeGearStyles(JsonWriter writer, Gson serializer, Map<String, GearStyleType> styles) throws IOException {
        writer.name("gear_styles");
        writer.beginObject();
        for (Map.Entry<String, GearStyleType> entry : styles.entrySet()) {
            writer.name(entry.getKey()).jsonValue(serializer.toJson(entry.getValue()));
        }
        writer.endObject();
    }

    public void writeItems(JsonWriter writer, Gson serializer, Map<String, Item> items) throws IOException {
        writer.name("items");
        writer.beginObject();
        for (Map.Entry<String, Item> entry : items.entrySet()) {
            writer.name(entry.getKey()).jsonValue(serializer.toJson(entry.getValue()));
        }
        writer.endObject();
    }

    public void writePlaceables(JsonWriter writer, Gson serializer, Map<String, Placeable> placeables) throws IOException {
        writer.name("placeables");
        writer.beginObject();
        for (Map.Entry<String, Placeable> entry : placeables.entrySet()) {
            writer.name(entry.getKey()).jsonValue(serializer.toJson(entry.getValue()));
        }
        writer.endObject();
    }

    public void writeRecipes(JsonWriter writer, Gson serializer, Map<String, Recipe> recipes) throws IOException {
        writer.name("recipes");
        writer.beginObject();
        for (Map.Entry<String, Recipe> entry : recipes.entrySet()) {
            writer.name(entry.getKey()).jsonValue(serializer.toJson(entry.getValue()));
        }
        writer.endObject();
    }

    public void writeSkins(JsonWriter writer, Gson serializer, Map<String, Skin> skins) throws IOException {
        writer.name("skins");
        writer.beginObject();
        for (Map.Entry<String, Skin> entry : skins.entrySet()) {
            writer.name(entry.getKey()).jsonValue(serializer.toJson(entry.getValue()));
        }
        writer.endObject();
    }

    public void writeStrings(JsonWriter writer, Gson serializer, Strings strings) throws IOException {
        writer.name("strings").jsonValue(serializer.toJson(strings));
    }

    public static class BenchSerializer implements JsonSerializer<Bench>, JsonDeserializer<Bench> {

        @Override
        public JsonElement serialize(Bench bench, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
            obj.add("rel_path", new JsonPrimitive(bench.getRPath()));
            obj.add("name", new JsonPrimitive(bench.getName()));

            JsonObject categories = new JsonObject();
            for (Map.Entry<String, List<String>> entry : bench.getCategories().entrySet()) {
                String benchIndex = bench.getOrder().get(entry.getKey()).toString(); // note that this will throw an NullPointerException if no such key-value pair exists

                JsonObject value = new JsonObject();
                value.add("name_id", new JsonPrimitive(entry.getKey()));
                JsonArray recipes = new JsonArray();
                entry.getValue().forEach(recipes::add);
                value.add("recipes", recipes);

                categories.add(benchIndex, value);
            }
            obj.add("categories", categories);
            return obj;
        }

        @Override
        public Bench deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();
            String rPath = obj.get("rel_path").getAsString();
            String name = obj.get("name").getAsString();
            Map<String, List<String>> categories = new HashMap<>();
            Map<String, Integer> order = new HashMap<>();
            JsonObject catsObj = obj.get("categories").getAsJsonObject();

            catsObj.entrySet().forEach(e -> {
                JsonObject val = e.getValue().getAsJsonObject();
                int catOrder = Integer.parseInt(e.getKey());
                String catName = val.get("name_id").getAsString();
                List<String> recipes = new ArrayList<>();
                val.get("recipes").getAsJsonArray().forEach(rec -> recipes.add(rec.getAsString()));
                order.put(catName, catOrder);
                categories.put(catName, recipes);
            });

            return new Bench(name, rPath, order, categories);
        }
    }

    public static class CollectionSerializer implements JsonSerializer<Collection>, JsonDeserializer<Collection> {

        @Override
        public JsonElement serialize(Collection collection, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
            obj.add("rel_path", new JsonPrimitive(collection.getRPath()));
            obj.add("name", new JsonPrimitive(collection.getName()));
            obj.add("desc", collection.getDesc() != null ? new JsonPrimitive(collection.getDesc()) : null);
            obj.add("blueprint", collection.getBlueprint() != null ? new JsonPrimitive(collection.getBlueprint()) : null);
            obj.add("trove_mr", new JsonPrimitive(collection.getTroveMR()));
            obj.add("geode_mr", new JsonPrimitive(collection.getGeodeMR()));

            JsonArray types = new JsonArray();
            collection.getTypes().forEach(i -> types.add(i.toString()));

            JsonObject properties = new JsonObject();
            collection.getProperties().forEach((k, v) -> properties.add(k.toString(), new JsonPrimitive(v)));

            JsonObject buffs = new JsonObject();
            collection.getBuffs().forEach((k, v) -> buffs.add(k.toString(), new JsonPrimitive(v)));

            obj.add("types", types);
            obj.add("properties", properties);
            obj.add("buffs", buffs);

            return obj;
        }

        @Override
        public Collection deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();
            String rPath = obj.get("rel_path").getAsString();
            String name = obj.get("name").getAsString();
            String desc = obj.get("desc").isJsonNull() ? null : obj.get("desc").getAsString();
            String blueprint = obj.get("blueprint").isJsonNull() ? null : obj.get("blueprint").getAsString();
            int troveMR = obj.get("trove_mr").getAsInt();
            int geodeMR = obj.get("geode_mr").getAsInt();
            List<Enums.Type> types = new ArrayList<>();
            Map<Enums.Property, Double> properties = new HashMap<>();
            Map<Enums.Buff, Double> buffs = new HashMap<>();

            JsonArray typesArr = obj.get("types").getAsJsonArray();
            typesArr.forEach(t -> types.add(Enums.Type.valueOf(t.getAsString())));

            JsonObject propsObj = obj.get("properties").getAsJsonObject();
            propsObj.entrySet().forEach(e -> properties.put(Enums.Property.valueOf(e.getKey()), e.getValue().getAsDouble()));

            JsonObject buffsObj = obj.get("buffs").getAsJsonObject();
            buffsObj.entrySet().forEach(e -> buffs.put(Enums.Buff.valueOf(e.getKey()), e.getValue().getAsDouble()));

            return new Collection(name, desc, rPath, troveMR, geodeMR, blueprint, types, properties, buffs);
        }
    }

    public static class CollectionIndexSerializer implements JsonSerializer<CollectionIndex>, JsonDeserializer<CollectionIndex> {

        @Override
        public JsonElement serialize(CollectionIndex index, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
            obj.add("rel_path", new JsonPrimitive(index.getRPath()));
            obj.add("type", new JsonPrimitive(index.getType()));

            JsonObject categories = new JsonObject();
            for (Map.Entry<String, String> nameEntry : index.getNames().entrySet()) {
                String key = nameEntry.getKey();

                JsonObject category = new JsonObject();
                category.add("display_name", new JsonPrimitive(nameEntry.getValue()));

                JsonArray entries = new JsonArray();
                JsonObject additionalInfo = new JsonObject();
                for (Map.Entry<String, String> infoEntry : index.getCategories().get(key).entrySet()) {
                    entries.add(infoEntry.getKey());
                    if (infoEntry.getValue() != null) {
                        additionalInfo.add(infoEntry.getKey(), new JsonPrimitive(infoEntry.getValue()));
                    }
                }
                if (entries.size() > 0) category.add("entries", entries);
                if (additionalInfo.size() > 0) category.add("additional_info", additionalInfo);
                categories.add(key, category);
            }
            obj.add("categories", categories);
            return obj;
        }

        @Override
        public CollectionIndex deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();
            String rPath = obj.get("rel_path").getAsString();
            String ciType = obj.get("type").getAsString();
            JsonObject catObj = obj.get("categories").getAsJsonObject();
            Map<String, String> names = new HashMap<>();
            Map<String, Map<String, String>> categories = new HashMap<>();

            catObj.entrySet().forEach(e -> {
                String key = e.getKey();
                JsonObject val = e.getValue().getAsJsonObject();
                names.put(key, val.get("display_name").getAsString());
                Map<String, String> additionalInfo = new HashMap<>();
                List<String> entries = new ArrayList<>();

                if (val.keySet().contains("entries")) {
                    JsonArray entriesArr = val.get("entries").getAsJsonArray();
                    if (val.keySet().contains("additional_info")) {
                        JsonObject addInfoObj = val.get("additional_info").getAsJsonObject();
                        Set<String> addInfoKeys = addInfoObj.keySet();
                        entriesArr.forEach(i -> {
                            String currKey = i.getAsString();
                            if (!addInfoKeys.contains(currKey))
                                entries.add(currKey);
                            else
                                additionalInfo.put(currKey, addInfoObj.get(currKey).getAsString());
                        });
                    } else {
                        entriesArr.forEach(i -> entries.add(i.getAsString()));
                    }
                    entries.forEach(k -> additionalInfo.put(k, null));
                }
                categories.put(key, additionalInfo);
            });

            return new CollectionIndex(rPath, ciType, names, categories);
        }
    }

    public static class GearStyleTypeSerializer implements JsonSerializer<GearStyleType>, JsonDeserializer<GearStyleType> {

        @Override
        public JsonElement serialize(GearStyleType gearStyleType, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
            obj.add("rel_path", new JsonPrimitive(gearStyleType.getRPath()));
            obj.add("type", new JsonPrimitive(gearStyleType.getType()));

            JsonObject styles = new JsonObject();
            for (Map.Entry<String, Map<String, GearStyleEntry>> entry : gearStyleType.getStyles().entrySet()) {
                String category = entry.getKey();
                JsonObject categoryStyles = new JsonObject();
                for (Map.Entry<String, GearStyleEntry> styleEntry : entry.getValue().entrySet()) {
                    String blueprint = styleEntry.getKey();
                    GearStyleEntry value = styleEntry.getValue();
                    String name = value.getName();
                    String desc = value.getDesc();
                    String info = value.getAdditionalInfo();
                    JsonObject objectProps = new JsonObject();
                    objectProps.add("name", name != null ? new JsonPrimitive(name) : null);
                    objectProps.add("desc", desc != null ? new JsonPrimitive(desc) : null);
                    objectProps.add("additional_info", info != null ? new JsonPrimitive(info) : null);
                    categoryStyles.add(blueprint, objectProps);
                }
                styles.add(category, categoryStyles);
            }

            obj.add("styles", styles);
            return obj;
        }

        @Override
        public GearStyleType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();
            String rPath = obj.get("rel_path").getAsString();
            String gsType = obj.get("type").getAsString();
            Map<String, Map<String, GearStyleEntry>> styles = new HashMap<>();

            JsonObject categories = obj.get("styles").getAsJsonObject();
            categories.entrySet().forEach(c -> {
                String key = c.getKey();
                JsonObject entriesObj = c.getValue().getAsJsonObject();
                Map<String, GearStyleEntry> entries = new HashMap<>();
                entriesObj.entrySet().forEach(e -> {
                    String blueprint = e.getKey();
                    JsonObject val = e.getValue().getAsJsonObject();
                    String name = val.get("name").isJsonNull() ? null : val.get("name").getAsString();
                    String desc = val.get("desc").isJsonNull() ? null : val.get("desc").getAsString();
                    String additional = val.get("additional_info").isJsonNull() ? null : val.get("additional_info").getAsString();
                    entries.put(blueprint, new GearStyleEntry(name, desc, blueprint, additional));
                });
                styles.put(key, entries);
            });

            return new GearStyleType(rPath, gsType, styles);
        }
    }

    public static class ItemSerializer implements JsonSerializer<Item>, JsonDeserializer<Item> {

        @Override
        public JsonElement serialize(Item item, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
            obj.add("rel_path", new JsonPrimitive(item.getRPath()));
            obj.add("name", new JsonPrimitive(item.getName()));
            obj.add("desc", item.getDesc() != null ? new JsonPrimitive(item.getDesc()) : null);
            obj.add("blueprint", item.getBlueprint() != null ? new JsonPrimitive(item.getBlueprint()) : null);
            obj.add("tradable", new JsonPrimitive(item.getTradable()));
            obj.add("lootbox", new JsonPrimitive(item.getLootbox()));
            obj.add("decay", new JsonPrimitive(item.getDecay()));

            JsonArray unlocks = new JsonArray();
            item.getUnlocks().forEach(unlocks::add);

            obj.add("unlocks", unlocks);

            return obj;
        }

        @Override
        public Item deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();
            String rPath = obj.get("rel_path").getAsString();
            String name = obj.get("name").getAsString();
            String desc = obj.get("desc").isJsonNull() ? null : obj.get("desc").getAsString();
            String blueprint = obj.get("blueprint").isJsonNull() ? null : obj.get("blueprint").getAsString();
            boolean tradable = obj.get("tradable").getAsBoolean();
            boolean lootbox = obj.get("lootbox").getAsBoolean();
            boolean decay = obj.get("decay").getAsBoolean();
            JsonArray unlocksArray = obj.get("unlocks").getAsJsonArray();

            List<String> unlocks = new ArrayList<>();
            for (int i = 0; i < unlocksArray.size(); i++) unlocks.add(unlocksArray.get(i).getAsString());

            return new Item(name, desc, rPath, unlocks.toArray(new String[0]), blueprint, tradable, lootbox, decay);
        }
    }

    public static class PlaceableSerializer implements JsonSerializer<Placeable>, JsonDeserializer<Placeable> {

        @Override
        public JsonElement serialize(Placeable placeable, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
            obj.add("rel_path", new JsonPrimitive(placeable.getRPath()));
            obj.add("name", new JsonPrimitive(placeable.getName()));
            obj.add("desc", placeable.getDesc() != null ? new JsonPrimitive(placeable.getDesc()) : null);
            obj.add("blueprint", placeable.getBlueprint() != null ? new JsonPrimitive(placeable.getBlueprint()) : null);
            obj.add("tradable", new JsonPrimitive(placeable.getTradable() ? 1 : 0));

            return obj;
        }

        @Override
        public Placeable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();
            String rPath = obj.get("rel_path").getAsString();
            String name = obj.get("name").getAsString();
            String desc = obj.get("desc").isJsonNull() ? null : obj.get("desc").getAsString();
            String blueprint = obj.get("blueprint").isJsonNull() ? null : obj.get("blueprint").getAsString();
            boolean tradable = obj.get("tradable").getAsBoolean();

            return new Placeable(name, desc, rPath, blueprint, tradable);
        }
    }

    public static class RecipeSerializer implements JsonSerializer<Recipe>, JsonDeserializer<Recipe> {

        @Override
        public JsonElement serialize(Recipe recipe, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
            obj.add("rel_path", new JsonPrimitive(recipe.getRPath()));
            obj.add("name", new JsonPrimitive(recipe.getName()));

            JsonObject costs = new JsonObject();
            recipe.getCosts().forEach((k, v) -> costs.add(k, new JsonPrimitive(v)));

            JsonObject output = new JsonObject();
            recipe.getOutput().forEach((k, v) -> output.add(k, new JsonPrimitive(v)));

            obj.add("costs", costs);
            obj.add("output", output);

            return obj;
        }

        @Override
        public Recipe deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();
            String rPath = obj.get("rel_path").getAsString();
            String name = obj.get("name").getAsString();
            JsonObject costsObj = obj.get("costs").getAsJsonObject();
            JsonObject outputObj = obj.get("output").getAsJsonObject();

            Map<String, Integer> costs = new HashMap<>();
            Map<String, Integer> output = new HashMap<>();

            costsObj.entrySet().forEach(e -> costs.put(e.getKey(), e.getValue().getAsInt()));
            outputObj.entrySet().forEach(e -> output.put(e.getKey(), e.getValue().getAsInt()));

            return new Recipe(name, rPath, costs, output);
        }
    }

    public static class SkinSerializer implements JsonSerializer<Skin>, JsonDeserializer<Skin> {

        @Override
        public JsonElement serialize(Skin skin, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
            obj.add("rel_path", new JsonPrimitive(skin.getRPath()));
            obj.add("name", new JsonPrimitive(skin.getName()));
            obj.add("desc", skin.getDesc() != null ? new JsonPrimitive(skin.getDesc()) : null);
            obj.add("blueprint", new JsonPrimitive(skin.getBlueprint()));

            return obj;
        }

        @Override
        public Skin deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();
            String rPath = obj.get("rel_path").getAsString();
            String name = obj.get("name").getAsString();
            String desc = obj.get("desc").isJsonNull() ? null : obj.get("desc").getAsString();
            String blueprint = obj.get("blueprint").getAsString();

            return new Skin(rPath, name, desc, blueprint);
        }
    }

    public static class StringsSerializer implements JsonSerializer<Strings>, JsonDeserializer<Strings> {

        @Override
        public JsonElement serialize(Strings s, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
//            obj.add("lang", new JsonPrimitive(s.getLang() == null ? "n/a" : s.getLang()));

            JsonObject strings = new JsonObject();
            s.getStrings().forEach((k, v) -> strings.add(k, v == null ? null : new JsonPrimitive(v)));

            obj.add("strings", strings);

            return obj;
        }

        @Override
        public Strings deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();
//            String lang = obj.get("lang").getAsString();
            JsonObject strObj = obj.get("strings").getAsJsonObject();
            Map<String, String> strings = new HashMap<>();
            strObj.entrySet().forEach(e -> strings.put(e.getKey(), e.getValue().isJsonNull() ? null : e.getValue().getAsString()));

            return new Strings("n/a", strings);
        }
    }
}
