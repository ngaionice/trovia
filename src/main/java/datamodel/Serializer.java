package datamodel;

import com.google.gson.*;
import datamodel.objects.*;
import javafx.collections.ObservableList;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class Serializer {

    public static Gson getSerializer(boolean usePrettyPrint) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Bench.class, new BenchSerializer());
        builder.registerTypeAdapter(Collection.class, new CollectionSerializer());
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

    public static class BenchSerializer implements JsonSerializer<Bench> {

        @Override
        public JsonElement serialize(Bench bench, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
            obj.add("rel_path", new JsonPrimitive(bench.getRPath()));
            obj.add("name", new JsonPrimitive(bench.getName()));

            JsonObject categories = new JsonObject();
            for (Map.Entry<List<String>, ObservableList<String>> entry : bench.getCategories().entrySet()) {
                String bench_index = entry.getKey().get(1);

                JsonObject value = new JsonObject();
                value.add("name_id", new JsonPrimitive(entry.getKey().get(0)));
                JsonArray recipes = new JsonArray();
                entry.getValue().forEach(recipes::add);
                value.add("recipes", recipes);

                categories.add(bench_index, value);
            }
            obj.add("categories", categories);
            return obj;
        }
    }

    public static class CollectionSerializer implements JsonSerializer<Collection> {

        @Override
        public JsonElement serialize(Collection collection, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
            obj.add("rel_path", new JsonPrimitive(collection.getRPath()));
            obj.add("name", new JsonPrimitive(collection.getName()));
            obj.add("desc", collection.getDesc() != null ? new JsonPrimitive(collection.getDesc()) : null);
            obj.add("trove_mr", new JsonPrimitive(collection.getTroveMR()));
            obj.add("geode_mr", new JsonPrimitive(collection.getGeodeMR()));
//            obj.add("bp_index", new JsonPrimitive(collection.getBlueprintIndex()));

//            JsonArray blueprints = new JsonArray();
//            collection.getPossibleBlueprints().forEach(blueprints::add);

            JsonArray types = new JsonArray();
            collection.getTypes().forEach(i -> types.add(i.toString()));

            JsonObject properties = new JsonObject();
            collection.getProperties().forEach((k, v) -> properties.add(k.toString(), new JsonPrimitive(v)));

            JsonObject buffs = new JsonObject();
            collection.getBuffs().forEach((k, v) -> buffs.add(k.toString(), new JsonPrimitive(v)));

//            obj.add("blueprints", blueprints);
            obj.add("types", types);
            obj.add("properties", properties);
            obj.add("buffs", buffs);

            return obj;
        }
    }

    public static class GearStyleTypeSerializer implements JsonSerializer<GearStyleType> {

        @Override
        public JsonElement serialize(GearStyleType gearStyleType, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
            obj.add("rel_path", new JsonPrimitive(gearStyleType.getRPath()));
            obj.add("type", new JsonPrimitive(gearStyleType.getType()));

            JsonObject styles = new JsonObject();
            for (Map.Entry<String, Map<String, String[]>> entry: gearStyleType.getStyles().entrySet()) {
                String category = entry.getKey();
                JsonObject categoryStyles = new JsonObject();
                for (Map.Entry<String, String[]> styleEntry: entry.getValue().entrySet()) {
                    String blueprint = styleEntry.getKey();
                    String[] values = styleEntry.getValue();
                    String name = values[0];
                    String desc = values[1];
                    String info = values[2];
                    JsonObject objectProps = new JsonObject();
                    objectProps.add("name", values[0] != null ? new JsonPrimitive(name) : null);
                    objectProps.add("desc", values[0] != null ? new JsonPrimitive(desc) : null);
                    objectProps.add("additional_info", values[0] != null ? new JsonPrimitive(info) : null);
                    categoryStyles.add(blueprint, objectProps);
                }
                styles.add(category, categoryStyles);
            }

            obj.add("styles", styles);
            return obj;
        }
    }

    public static class ItemSerializer implements JsonSerializer<Item> {

        @Override
        public JsonElement serialize(Item item, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
            obj.add("rel_path", new JsonPrimitive(item.getRPath()));
            obj.add("name", new JsonPrimitive(item.getName()));
            obj.add("desc", item.getDesc() != null ? new JsonPrimitive(item.getDesc()) : null);
            obj.add("tradable", new JsonPrimitive(item.isTradable() ? 1 : 0));
            obj.add("bp_index", new JsonPrimitive(item.getBlueprintIndex()));

            JsonArray blueprints = new JsonArray();
            item.getPossibleBlueprints().forEach(blueprints::add);

            JsonArray unlocks = new JsonArray();
            item.getUnlocks().forEach(unlocks::add);

            obj.add("blueprints", blueprints);
            obj.add("unlocks", unlocks);

            JsonObject loot = new JsonObject();
            boolean lootFlag = false;
            JsonObject lootCommon = new JsonObject();
            if (item.getLootCommon() != null) {
                item.getLootCommon().forEach((k, v) -> lootCommon.add(k, new JsonPrimitive(v)));
                loot.add("common", lootCommon);
                lootFlag = true;
            }

            JsonObject lootUncommon = new JsonObject();
            if (item.getLootUncommon() != null) {
                item.getLootUncommon().forEach((k, v) -> lootUncommon.add(k, new JsonPrimitive(v)));
                loot.add("uncommon", lootUncommon);
                lootFlag = true;
            }

            JsonObject lootRare = new JsonObject();
            if (item.getLootRare() != null) {
                item.getLootRare().forEach((k, v) -> lootRare.add(k, new JsonPrimitive(v)));
                loot.add("rare", lootRare);
                lootFlag = true;
            }

            if (lootFlag) obj.add("loot", loot);

            return obj;
        }
    }

    public static class PlaceableSerializer implements JsonSerializer<Placeable> {

        @Override
        public JsonElement serialize(Placeable placeable, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
            obj.add("rel_path", new JsonPrimitive(placeable.getRPath()));
            obj.add("name", new JsonPrimitive(placeable.getName()));
            obj.add("desc", placeable.getDesc() != null ? new JsonPrimitive(placeable.getDesc()) : null);
            obj.add("tradable", new JsonPrimitive(placeable.isTradable() ? 1 : 0));
//            obj.add("bp_index", new JsonPrimitive(placeable.getBlueprintIndex()));
//
//            JsonArray blueprints = new JsonArray();
//            placeable.getPossibleBlueprints().forEach(blueprints::add);
//
//            obj.add("blueprints", blueprints);
            return obj;
        }
    }

    public static class RecipeSerializer implements JsonSerializer<Recipe> {

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
    }

    public static class SkinSerializer implements JsonSerializer<Skin> {

        @Override
        public JsonElement serialize(Skin skin, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
            obj.add("rel_path", new JsonPrimitive(skin.getRPath()));
            obj.add("name", new JsonPrimitive(skin.getName()));
            obj.add("desc", skin.getDesc() != null ? new JsonPrimitive(skin.getDesc()) : null);
            obj.add("blueprint", new JsonPrimitive(skin.getBlueprint()));

            return obj;
        }
    }

    public static class StringsSerializer implements JsonSerializer<Strings> {

        @Override
        public JsonElement serialize(Strings s, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject obj = new JsonObject();
            obj.add("lang", new JsonPrimitive(s.getLang()));

            JsonObject strings = new JsonObject();
            s.getStrings().forEach((k, v) -> strings.add(k, new JsonPrimitive(v)));

            obj.add("strings", strings);

            return obj;
        }
    }
}
