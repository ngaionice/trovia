package datamodel.parser.parsestrategies;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import datamodel.objects.Article;
import datamodel.objects.Recipe;
import datamodel.parser.Markers;
import datamodel.parser.Parser;

import java.util.HashMap;
import java.util.Map;

public class ParseRecipe implements ParseStrategy {

    @Override
    public Article parseObject(String splitString, String absPath, boolean useRPath) throws ParseException {
        try {
            Markers m = new Markers();

            String path = absPath.substring(absPath.lastIndexOf("\\") + 1, absPath.indexOf(m.endFile));
            String rPath = useRPath ? Parser.extractRPath(absPath) : absPath.replace("\\", "/");

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
                throw new ParseException("Recipe formatting problematic at: " + rPath + "; string too short for trimming.");
            }

            Pattern pDivider = Pattern.compile(divider);
            Matcher mDivider = pDivider.matcher(initialTrim);
            if (!mDivider.find()) {
                throw new ParseException("Recipe formatting problematic at: " + rPath + "; cannot split into input/output.");
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
                if (!mOutputExtract.find())
                    throw new ParseException("Output parsing errored at: " + rPath + "; no matching sequence found.");
                char type = mOutputExtract.group(4).charAt(13);
                content = mOutputExtract.group(2);

                switch (type) {
                    case '0':
                        Matcher mOutputObject = pOutputObject.matcher(content);
                        if (!mOutputObject.find()) {
                            throw new ParseException("Output parsing errored at: " + rPath + "; attempted to parse as object but no matching sequence found.");
                        }
                        output.put(Parser.hexToAscii(mOutputObject.group(2)), Parser.recipeH2D(mOutputObject.group(5), absPath));
                        break;
                    case '2':
                        Matcher mOutputClass = pOutputClass.matcher(content);
                        if (!mOutputClass.find()) {
                            throw new ParseException("Output parsing errored at: " + rPath + "; attempted to parse as class unlock but no matching sequence found.");
                        }
                        output.put(Parser.hexToAscii(mOutputClass.group(3)), 0);
                        break;
                    case '4':
                        Matcher mOutputCollection = pOutputCollection.matcher(content);
                        if (!mOutputCollection.find()) {
                            throw new ParseException("Output parsing errored at: " + rPath + "; attempted to parse as collection unlock but no matching sequence found.");
                        }
                        output.put(Parser.hexToAscii(mOutputCollection.group(3)), 0);
                        break;
                    default:
                        throw new ParseException("Unexpected output type " + type + " at: " + rPath);
                }
                outputStartIndex = outputEndIndex;
            }

            return new Recipe(path, rPath, costs, output);
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }
}