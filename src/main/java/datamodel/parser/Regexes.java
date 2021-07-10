package datamodel.parser;

public class Regexes {

    public String gearStyleCatExtractor = "[0-9A-F]4 (?:0[0-7] )?08 [0-9A-F][0-9A-F] ((?:[0-9A-F][0-9A-F] )+?)BE 01 [0-9A-F]E ((?:[0-9A-F][0-9A-F] (?:[0-9A-F][0-9A-F] )?)?)00 ((?:[0-9A-F]4 (?:[0-9][0-9A-F] )?08 [0-9A-F][0-9A-F] (?:(?:[0-9A-F][0-9A-F] )+?)18 (?:00 |(?:[0-9A-F][0-9A-F] (?:(?:[0-9A-F][0-9A-F] )+?)))28 (?:00 |(?:[0-9A-F][0-9A-F] (?:(?:[0-9A-F][0-9A-F] )+?)))30 [0-9A-F][0-9A-F] BE 04 08 00 08 58 (?:00 |[0-9A-F][0-9A-F] (?:(?:[0-9A-F][0-9A-F] )+?))68 00 1E )*?)08 1E";
    public String gearStyleInfoExtractor = "[0-9A-F]4 (?:[0-9][0-9A-F] )?08 [0-9A-F][0-9A-F] ((?:[0-9A-F][0-9A-F] )+?)18 (00 |(?:[0-9A-F][0-9A-F] ((?:[0-9A-F][0-9A-F] )+?)))28 (00 |(?:[0-9A-F][0-9A-F] ((?:[0-9A-F][0-9A-F] )+?)))30 [0-9A-F][0-9A-F] BE 04 08 00 08 58 (00 |[0-9A-F][0-9A-F] ((?:[0-9A-F][0-9A-F] )+?))68 00 1E";

    public String langSplitter = "BE 03 0[0-9A-F] 0[0-9A-F] 0[0-9A-F] 1E";
    public String langExtractor = "08 ([0-9A-F][0-9A-F] )+?(24 ([0-9A-F][0-9A-F] )+?)18 (00 |([0-9A-F][0-9A-F]) (0[0-9A-F] )?(([0-9A-F][0-9A-F] )+))";

    public String itemNDExtractor = "[0-9]8 ([0-9A-F][0-9A-F]) (24 ([0-9A-F][0-9A-F] )+)([0-9A-F][0-9A-F] )+?(00 |([0-9A-F][0-9A-F]) (24 ([0-9A-F][0-9A-F] )+))68 00 80";
    public String itemBpExtractor = "[0-9]8 ([0-9A-F][0-9A-F]) ((?:(?:2F|3[0-9]|4[0-9A-F]|5[0-9ABDF]|6[1-9A-F]|7[0-9A]) )+?)2E 62 6C 75 65 70 72 69 6E 74";
    public String itemBpExtractorLowAcc = "[0-9]8 ([0-9A-F][0-9A-F]) (((3[0-9]|4[0-9A-F]|5[0-9ABDF]|6[1-9A-F]|7[0-9A]) )+)";

    public String skinInfoExtractor = "(08 1E 1E 08 BE 03 2E 00 |24 ([0-9A-F][0-9A-F] ){5})08 48 (([0-9A-F][0-9A-F]) (([0-9A-F][0-9A-F] )+))58 (([0-9A-F][0-9A-F]) (([0-9A-F][0-9A-F] )+))68 (([0-9A-F][0-9A-F]) (([0-9A-F][0-9A-F] )*))1E$";

    public String collIndexCatExtractor = "[0-9A-F]4 (?:0[0-7] )?08 [0-9A-F][0-9A-F] ((?:[0-9A-F][0-9A-F] )+?)18 [0-9A-F][0-9A-F] ((?:[0-9A-F][0-9A-F] )+?)20 0[0-9] BE 03 [0-9A-F]E ([0-9A-F][0-9A-F] (?:[0-9A-F][0-9A-F] )?)?00 ((?:[0-9A-F][0-9A-F] )*?)08 40 0[0-9A-F] 1E";
    public String collIndexInfoExtractor = "[0-9A-F]4 (?:[0-9A-F][0-9A-F] )?08 [0-9A-F][0-9A-F] ((?:[0-9A-F][0-9A-F] )+?)BE 01 (?:[0-9A-F][0-9A-F] ){4}(00 |[0-9A-F][0-9A-F] ((?:[0-9A-F][0-9A-F] )+?))BE 03 [0-9]8 00 (?:[0-9A-F][0-9A-F] )*?08 1E";
}
