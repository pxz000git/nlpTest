package com.foda;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.trees.SimpleTree;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.simple.*;

import java.util.*;

@SuppressWarnings("unused")
class StanfordNlp {

    private StanfordCoreNLP pipeline;
    private Properties props;

    public StanfordNlp() throws Exception{
        // 配置nlp属性
        props = new Properties();
        props.load(IOUtils.readerFromString("./StanfordCoreNLP-chinese.properties"));
    }

    /**
     * 使用simpleNlp包中的类解析短文
     * @param text 需要解析的短文
     * @return 短文的中动词短语，以字符串存放，句号分割。
     */
    public String simpleNlp(String text) {
        Document doc = new Document(props, text);
        StringBuilder vps = new StringBuilder();

        for (Sentence sentence : doc.sentences()) {
            System.out.println("sentence: "+sentence);
            Tree tree = sentence.parse();
            tree.pennPrint();
            for (String s:getLongVPFromTree(tree)) {
                System.out.println(s);
                vps.append(s + "。");
            }
        }
        return vps.toString();
    }

    /**
     * 对一段话进行语法分析
     * @param text 需要分析的句子或文章
     */
    public String runChineseAnnotators(String text){
        pipeline = new StanfordCoreNLP(props);
        /*
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        parserOutput(document);
        */
        Annotation annotation = pipeline.process(text);
        //System.out.println(pipeline.timingInformation());
        return parserOutput(annotation);
    }

    private String parserOutput(Annotation document){
        long startTime;
        long endTime;
        StringBuilder vps = new StringBuilder();

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for(CoreMap sentence: sentences) {
            /*
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
            }
            */

            System.out.println("sentence: "+sentence);

            // 获取语法树
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            tree.pennPrint();

            // 获取动词短语
            for (String s:getLongVPFromTree(tree))
                vps.append(s+"。");
        }
        return vps.toString();
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
        Tree parentVP = new SimpleTree();
        ArrayList<String> list = new ArrayList<>();
        StringBuilder sb;

        for (Tree subtree : tree.subTreeList()) {
            if (subtree.value().toLowerCase().equals("vv") &&
                    parent.value().toLowerCase().equals("vp")) {

                sb = new StringBuilder();

                for (Word w:parent.yieldWords()){
                    sb.append(w.word());
                }
                if (!list.contains(sb.toString()) && !parentVP.contains(subtree)){
                    list.add(sb.toString());
                    parentVP = parent;
                }
//                System.out.print(sb.toString() + "\t");
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
