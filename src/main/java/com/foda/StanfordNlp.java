package com.foda;

import com.opencsv.CSVReader;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.simple.*;
import edu.stanford.nlp.trees.SimpleTree;
import edu.stanford.nlp.trees.TreeFactory;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.Word;

import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.logging.Filter;

import java.util.Properties;

public class StanfordNlp {

    private StanfordCoreNLP corenlp;

    public StanfordNlp() throws Exception{
        // 配置nlp属性
        Properties props = new Properties();
        props.load(IOUtils.readerFromString("./StanfordCoreNLP-chinese.properties"));
        corenlp = new StanfordCoreNLP(props);
    }


    /**
     * 对一段话进行语法分析
     * @param text 需要分析的句子或文章
     */
    public void runChineseAnnotators(String text){
        Annotation document = new Annotation(text);
        corenlp.annotate(document);
        parserOutput(document);
    }

    private void parserOutput(Annotation document){
        long startTime;
        long endTime;

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for(CoreMap sentence: sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
//            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//                // this is the text of the token
//                String word = token.get(CoreAnnotations.TextAnnotation.class);
//                // this is the POS tag of the token
//                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
//                // this is the NER label of the token
//                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
//                System.out.println(word+"\t"+pos+"\t"+ne);
//            }

            System.out.println("sentence: "+sentence);
            // this is the parse tree of the current sentence
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

            startTime = System.currentTimeMillis();    //获取开始时间
            System.out.println("语法树：");
            tree.pennPrint();
            endTime = System.currentTimeMillis();    //获取结束时间
            System.out.println("解析成语法树耗时：" + (endTime - startTime) + "ms");    //输出程序运行时
            System.out.println("-----------------------------------");

            // 分析动词短语
            startTime = System.currentTimeMillis();    //获取开始时间
            List<String> vvs = getLongVPFromTree(tree);
            endTime = System.currentTimeMillis();    //获取结束时间
            System.out.println("获取动词短语耗时：" + (endTime - startTime) + "ms");    //输出程序运行时
            System.out.println("-----------------------------------");

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

    /**
     * 对于复杂语法分析nlp分析得不准确，暂不使用此方法
     * 从一个语法树中获取一个精简的动词短语
     * @param tree 语法树
     * @return 返回一个字符串列表
     */
    private List<String> getVVFromTree(Tree tree) {
        Tree parent = tree;
        ArrayList<String> list = new ArrayList<>();
        StringBuilder sb;

        for (Tree subtree : tree.subTreeList()) {
            if (subtree.value().toLowerCase().equals("vv") &&
                    parent.value().toLowerCase().equals("vp")) {
                sb = new StringBuilder();
                sb.append(subtree.firstChild().value());
                sb.append(getNNFromVP(parent));
//                    for (Word word:parent.yieldWords()) {
//                        sb.append(word.toString());
//                    }
                list.add(sb.toString());
//                System.out.println(sb.toString());
                System.out.print(sb.toString() + "\t");
            }
            parent = subtree;
        }
        System.out.println();
        return list;
    }

    /**
     * 从一个语法树中获取一个未精简的动词短语
     * @param tree 语法树
     * @return 返回动词短语列表
     */
    private List<String> getLongVPFromTree(Tree tree) {
        Tree parent = tree;
        ArrayList<String> list = new ArrayList<>();
        StringBuilder sb;

        for (Tree subtree : tree.subTreeList()) {
            if (subtree.value().toLowerCase().equals("vv") &&
                    parent.value().toLowerCase().equals("vp")) {
                sb = new StringBuilder();
                for (Word w:parent.yieldWords()){
                    sb.append(w.word());
                }
                list.add(sb.toString());
                System.out.print(sb.toString() + "\t");
            }
            parent = subtree;
        }
        System.out.println();
        return list;
    }

    /**
     * 判断一个语法树中是否还有动词短语结构
     * @param tree 语法树
     * @return True or False
     */
    private boolean hasVP(Tree tree) {
        boolean result = false;
        Tree parent = tree;

        for (Tree subtree : tree.subTreeList()) {
            if (subtree.value().toLowerCase().equals("vv") &&
                    parent.value() !=null &&
                    parent.value().toLowerCase().equals("vp")) {
                result = true;
            }
            parent = subtree;
        }
        return result;
    }

    /**
     * 从一个动词短语的树中返回一个名词短语
     * @param tree 动词短语语法树
     * @return 返回一个名词短语字符串
     */
    private String getNNFromVP(Tree tree)
    {
        Tree parent=tree;
        StringBuilder sb=new StringBuilder();
        for (Tree subtree:tree.subTreeList()) {
            if (subtree.value().toLowerCase().equals("nn")){
                for (Word word:parent.yieldWords()) {
                    sb.append(word.toString());
                }
                break;
            }
            if (subtree.value().toLowerCase().equals("np"))
                parent=subtree;
        }
        return sb.toString();
    }

}
