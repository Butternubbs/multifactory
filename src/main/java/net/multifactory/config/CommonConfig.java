package net.multifactory.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;

public class CommonConfig {
    
    private static final Builder CONFIG_BUILDER = new Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ConfigValue<List<? extends String>> RECIPES;

    static{
        RECIPES = CONFIG_BUILDER.comment("List of recipes for the scanner. To add a recipe, add \n" +
                                   "<shape> -> <item> <quantity>\n" +
                                   "where shape is the name of a .nbt file in the structures folder.")
                            .defineList("recipe_list", defaultRecipeList(), entry -> true);
        SPEC = CONFIG_BUILDER.build();
    }

    public static List<String> defaultRecipeList(){
        List<String> list = new ArrayList<String>();
        list.add("hopper -> minecraft:hopper 1");
        return list;
    }
}
