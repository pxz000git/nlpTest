package com.foda;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.simple.*;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class StanfordChineseNlpExample
{
    public static void main( String[] args )
    {
        StanfordChineseNlpExample example = new StanfordChineseNlpExample();
        try {
            example.runChineseAnnotators();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

    }

    public void runChineseAnnotators() throws Exception{

        String text = "孩提王国的童话光环不知何时被掷落在地，安徒生的天国花园大概早已沉入冰穹。"+
                "我想走入那密集汹涌的人潮，只为寻找一双赤子透澄明净的眼睛。"+
                "我为这年岁渐长，而心灵提前硬化的世界感到惶恐，我为这不再皓洁，蒙上尘埃的心灵之窗感到悲哀。";
        Annotation document = new Annotation(text);

        Properties props = new Properties();
        props.load(IOUtils.readerFromString("./StanfordCoreNLP-chinese.properties"));

        StanfordCoreNLP corenlp = new StanfordCoreNLP(props);
        corenlp.annotate(document);

        parserOutput(document);
    }

    public void parserOutput(Annotation document){
        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for(CoreMap sentence: sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

                System.out.println(word+"\t"+pos+"\t"+ne);
//                System.out.println(word);
            }

            // this is the parse tree of the current sentence
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            System.out.println("语法树：");
//            System.out.println(tree.toString());
//            tree.indentedListPrint();
//            tree.indentedXMLPrint();
            tree.pennPrint();

            // this is the Stanford dependency graph of the current sentence
//            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
//            System.out.println("依存句法：");
//            System.out.println(dependencies.toString());
        }

        // This is the coreference link graph
        // Each chain stores a set of mentions that link to each other,
        // along with a method for getting the most representative mention
        // Both sentence and token offsets start at 1!
//        Map<Integer, CorefChain> graph = document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
    }

}
