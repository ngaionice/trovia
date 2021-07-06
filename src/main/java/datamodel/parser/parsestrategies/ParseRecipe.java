package datamodel.parser.parsestrategies;

import datamodel.objects.Article;
import datamodel.objects.Recipe;
import datamodel.parser.Parser;
import local.Markers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseRecipe implements ParseStrategy{

    @Override
    public Article parseObject(String splitString, String absPath) throws ParseException {

        Markers m = new Markers();

        String path = absPath.substring(absPath.lastIndexOf("\\")+1, absPath.indexOf(m.endFile));
        String rPath = absPath.substring(absPath.indexOf("prefabs\\")+8, absPath.indexOf(m.endFile));
        rPath = rPath.replaceAll("\\\\", "/");

        Map<String, Integer> costs = new HashMap<>();
        Map<String, Integer> output = new HashMap<>();

        String divider = "08 BE 01 AE 0[0-9A-F] 00";
        String inputDivider = "(28 00 AE 03 00 01 18 00)";
        String inputExtract = "([0-6]4 08 [0-9A-F][0-9A-F] )(([0-9A-F][0-9A-F] )+)(10 )(([0-9A-F][0-9A-F] ){1,5})(28 00 AE 03 00 01 18 00)";

        String outputSplitter = "(28 00 1E 40 0[0-9] 1E)";
        String outputDivider = "([0-6]4 08 )(([0-9A-F][0-9A-F] )+)(28 00 1E 40 0[0-9] 1E)";
        String outputObject = "([0-9A-F][0-9A-F] )(([0-9A-F][0-9A-F] )+)(10 )(([0-9A-F][0-9A-F] ){1,4})(28 00 AE)";
        String outputCollection = "(00 10 0[0-9] 28 00 AE 03 00 [0-9A-F][0-9A-F] 18 )([0-9A-F][0-9A-F] )(([0-9A-F][0-9A-F] )+)";
        String outputClass = "(00 10 00 28 )([0-9A-F][0-9A-F] )(([0-9A-F][0-9A-F] )+)(AE 03 00 01 18 00)";

        String initialTrim;
        try {
            initialTrim = splitString.substring(12, splitString.length() - 102);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ParseException("Recipe formatting problematic at: " + absPath + "; string too short for trimming.");
        }

        Pattern pDivider = Pattern.compile(divider);
        Matcher mDivider = pDivider.matcher(initialTrim);
        if (!mDivider.find()) {
            throw new ParseException("Recipe formatting problematic at: " + absPath + "; cannot split into input/output.");
        }
        int splitIndex = mDivider.start();
        String inputs = initialTrim.substring(0, splitIndex);
        String outputs = initialTrim.substring(splitIndex + 18);

        Matcher mInputDivider = Pattern.compile(inputDivider).matcher(inputs);
        Pattern pInputExtract = Pattern.compile(inputExtract);

        String inputHexPath;
        String inputHexVal;
        int inputStartIndex = 0;
        int inputEndIndex;
        Matcher mInputExtract;
        while (mInputDivider.find()) {
            inputEndIndex = mInputDivider.end() + 1;
            mInputExtract = pInputExtract.matcher(inputs.substring(inputStartIndex, inputEndIndex));
            if (!mInputExtract.find()) throw new ParseException("Input parsing errored at:" + absPath);
            inputHexPath = mInputExtract.group(2);
            inputHexVal = mInputExtract.group(5);
            costs.put(Parser.hexToAscii(inputHexPath), Parser.recipeH2D(inputHexVal, absPath));
            inputStartIndex = inputEndIndex;
        }

        Matcher mOutputSplitter = Pattern.compile(outputSplitter).matcher(outputs);
        Pattern pOutputExtract = Pattern.compile(outputDivider);
        Pattern pOutputObject = Pattern.compile(outputObject); // trim
        Pattern pOutputCollection = Pattern.compile(outputCollection);
        Pattern pOutputClass = Pattern.compile(outputClass); // trim

        String content;
        int outputStartIndex = 0;
        int outputEndIndex;
        Matcher mOutputExtract;
        while (mOutputSplitter.find()) {
            outputEndIndex = mOutputSplitter.end();
            mOutputExtract = pOutputExtract.matcher(outputs.substring(outputStartIndex, outputEndIndex));
            if (!mOutputExtract.find()) throw new ParseException("Output parsing errored at: " + absPath + "; no matching sequence found.");
            char type = mOutputExtract.group(4).charAt(13);
            content = mOutputExtract.group(2);

            switch (type) {
                case '0':
                    Matcher mOutputObject = pOutputObject.matcher(content);
                    if (!mOutputObject.find()) {
                        throw new ParseException("Output parsing errored at: " + absPath + "; attempted to parse as object but no matching sequence found.");
                    }
                    output.put(Parser.hexToAscii(mOutputObject.group(2)), Parser.recipeH2D(mOutputObject.group(5), absPath));
                    break;
                case '2':
                    Matcher mOutputClass = pOutputClass.matcher(content);
                    if (!mOutputClass.find()) {
                        throw new ParseException("Output parsing errored at: " + absPath + "; attempted to parse as class unlock but no matching sequence found.");
                    }
                    output.put(Parser.hexToAscii(mOutputClass.group(3)), 0);
                    break;
                case '4':
                    Matcher mOutputCollection = pOutputCollection.matcher(content);
                    if (!mOutputCollection.find()) {
                        throw new ParseException("Output parsing errored at: " + absPath + "; attempted to parse as collection unlock but no matching sequence found.");
                    }
                    output.put(Parser.hexToAscii(mOutputCollection.group(3)), 0);
                    break;
                default:
                    throw new ParseException("Unexpected output type " + type + " at: " + absPath);
            }
            outputStartIndex = outputEndIndex;
        }

        return new Recipe(path, rPath, costs, output);
    }
}

// trim the first 12 characters, and the last 102 characters
// [3E AE 05 00 ]
// [ 08 24 00 00 A0 40 30 02 50 00 70 00 80 01 00 98 01 00 AE 0A 08 04 6E 75 6C 6C AE 01 1E 1E B0 01 00 1E]

// split the rest into 2 chunks: input and output, using [08 BE 01 AE 0x 00]

// processing input:
// split into string array using [28 00 1E 40 0? 1E], then trim
// for each chunk, ignore first 6 characters, they serve as an index
// the next 2 characters indicate how long the actual string is,
// so use that and extract the next # * 3 characters, then trim, then convert to ascii
// identify [10 ??var 28 00 AE], where ??var is a variable amount of chars to be passed into the H2D function for the qty
// discard the rest

// processing output:
// each bit starts with [x4 08], and ends with [28 00 1E 40 0? 1E]
// identify how many [28 00 1E 40 0? 1E]'s there are, and extract the different output using this (don't just split directly)

// [x4 08] is index, discard

// if the next chars are not [00 10 00], grants something into inventory.
// for each chunk, ignore first 6 characters, they serve as an index
// the next 2 characters indicate how long the actual string is,
// so use that and extract the next # * 3 characters, then trim, then convert to ascii
// identify [10 ??var 28 00 AE], where ??var is a variable amount of chars to be passed into the H2D function for the qty
// discard the rest

// if the next chars are [00 10 0[0-9]], unlocks something (either a class or something in collections).
// we can determine whether it unlocks a class or something else by the characters immediately after [28 ]:
// if they are [00 ...], then they unlock something in collections. in which case, we discard the 21 chars starting from [28]
// the next 2 characters indicate how long the actual string is,
// so use that and extract the next # * 3 characters, then trim, then convert to ascii

// for the [28 00 1E 40 0? 1E], they can be discarded. however, the ? can be mapped into 3 types:
// 0: normal item/placeable
// 2: unlocks a class
// 4: unlocks something in the collections, costumes/styles/etc

// additional information:
// in unlocks, most are [00 10 00], however there are a few that are [00 10 02].
// their common properties are that they are all tomes;
// cases include all the delve carpet tomes, bb tome, crystal core tome, and cosmic dust tome.