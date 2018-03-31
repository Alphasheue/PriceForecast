package com.sheue.ml.rnn.gru;

import com.sheue.app.bean.Data;
import com.sheue.app.dao.PriceDAO;
import com.sheue.ml.dataset.DataText;
import com.sheue.ml.layers.MatIniter;
import com.sheue.ml.utils.LossFunction;
import org.jblas.DoubleMatrix;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Language Model using GRU
public class GRUPredict {
    private GRU gru;
    private DataText dataText;
    private DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    public GRUPredict(int inSize, int outSize, MatIniter initer) {
        this.gru = new GRU(inSize, outSize, initer);
    }

    public static GRUPredict init(String itemName, double lr, double acc) {
        DataText dt = new DataText(itemName);
        int hiddenSize = 100;
        GRUPredict gru = new GRUPredict(dt.getCharIndex().size(), hiddenSize, new MatIniter(MatIniter.Type.Uniform, 0.1, 0, 0));
        gru.dataText = dt;
        gru.train(itemName, lr, acc);
        return gru;
    }

    private void train(String itemName, double lr, double acc) {
        Map<Integer, String> indexChar = dataText.getIndexChar();
        Map<String, DoubleMatrix> charVector = dataText.getCharVector();
        Map<String, Integer> charIndex = dataText.getCharIndex();
        List<String> sequenceList = dataText.getSequence();
        for (int i = 0; i < 400; i++) {
            double error = 0;
            double num = sequenceList.size();
            double wrong = 0;
            Map<String, DoubleMatrix> acts = new HashMap<>();
            List<String> predict = new ArrayList<>();
            for (int t = 0; t < sequenceList.size() - 1; t++) {
                String sequence = sequenceList.get(t);
//                System.out.print("real:" + sequence);
                DoubleMatrix xt = charVector.get(sequence);
                acts.put("x" + t, xt);

                gru.active(t, acts);

                DoubleMatrix predcitYt = gru.decode(acts.get("h" + t));
                acts.put("py" + t, predcitYt);
                DoubleMatrix trueYt = charVector.get(sequenceList.get(t + 1));
                acts.put("y" + t, trueYt);

//                System.out.println("--predict:" + indexChar.get(predcitYt.argmax()));
                error += LossFunction.getMeanCategoricalCrossEntropy(predcitYt, trueYt);
                predict.add(indexChar.get(predcitYt.argmax()));

            }

            for (int j = 0; j < sequenceList.size() - 1; j++) {
                if (sequenceList.size() - 1 != predict.size()) {
                    System.out.println("error at " + j);
                    break;
                }
                if (!sequenceList.get(j).equals(predict.get(j))) {
                    wrong++;
                }
            }

            gru.bptt(acts, sequenceList.size() - 2, lr);

            System.out.println("第" + (i + 1) + "次训练，误差=" + error + "，训练用例数=" + num + "，预测错误数=" + wrong);
            if (wrong / num < acc) {
                System.out.println("模型训练完成！");
                break;
            }
            if (i == 399) {
                System.out.println("已达到训练次数上限" + (i + 1) + "次！");
            }
        }

        System.out.println("开始测试：");
        List<Data> list = PriceDAO.getTest(itemName);
        double error = 0;
        double num = list.size() - 1;
        double wrong = 0;
        Map<String, DoubleMatrix> acts = new HashMap<>();
        for (int t = 0; t < num; t++) {
            String sequence = String.valueOf(list.get(t).getPrice());
            sequence = indexChar.get(convertSequence(charIndex, sequence));
            DoubleMatrix xt = charVector.get(sequence);
            acts.put("x" + t, xt);
            gru.active(t, acts);
            DoubleMatrix predcitYt = gru.decode(acts.get("h" + t));
            acts.put("py" + t, predcitYt);
            DoubleMatrix trueYt = charVector.get(String.valueOf(list.get(t + 1).getPrice()));
            acts.put("y" + t, trueYt);

            error += LossFunction.getMeanCategoricalCrossEntropy(predcitYt, trueYt);

            double predict = Double.parseDouble(indexChar.get(predcitYt.argmax()));
            double real = Double.parseDouble(indexChar.get(trueYt.argmax()));
            System.out.println("iter" + (t + 1) + ",predict=" + predict + ",real=" + real);
            dataset.addValue(predict, "预测价格", new Integer(t));
            dataset.addValue(real, "实际价格", new Integer(t));
            if (Math.abs(predict - real) > 0.2) {
                wrong++;
            }
        }
//        JFreeChart jFreeChart = LineChartUtil.createLineChart("错误率", "次数", "大小", dataset);
//        LineChartUtil.draw("./data/GRUResult.jpeg", jFreeChart, 1280, 720);

        System.out.println("误差=" + error + "，测试用例数=" + num + "，预测错误数=" + wrong + "，准确率"
                + String.format("%.2f", (1 - wrong / num) * 100) + "%");
    }

    public List<String> predict(String itemName, int time) {
        Map<Integer, String> indexChar = dataText.getIndexChar();
        Map<String, DoubleMatrix> charVector = dataText.getCharVector();
        Map<String, Integer> charIndex = dataText.getCharIndex();
        List<String> result = new ArrayList<>();

        List<Data> list = PriceDAO.getPage(itemName, 1);
        String sequence = String.valueOf(list.get(0).getPrice());
        Integer index = convertSequence(charIndex, sequence);
        sequence = indexChar.get(index);
        Map<String, DoubleMatrix> acts = new HashMap<>();
        for (int t = 0; t < time; t++) {
            DoubleMatrix xt = charVector.get(sequence);
            acts.put("x" + t, xt);
            gru.active(t, acts);
            DoubleMatrix predcitYt = gru.decode(acts.get("h" + t));
            acts.put("py" + t, predcitYt);
            index++;
            sequence = indexChar.get(index);
            DoubleMatrix trueYt = charVector.get(sequence);
            acts.put("y" + t, trueYt);

            result.add(indexChar.get(predcitYt.argmax()));
        }
        return result;
    }

    private Integer convertSequence(Map<String, Integer> charIndex, String sequence) {
        if (charIndex.containsKey(sequence)) {
            return charIndex.get(sequence);
        } else {
            for (int i = 1; i <= 5; i++) {
                String sequence2 = String.valueOf(Double.parseDouble(sequence) - 0.1 * i);
                if (charIndex.containsKey(sequence2)) {
                    return charIndex.get(sequence2);
                } else {
                    sequence2 = String.valueOf(Double.parseDouble(sequence) + 0.1 * i);
                    if (charIndex.containsKey(sequence2)) {
                        return charIndex.get(sequence2);
                    }
                }
            }
        }
        System.out.println("convert sequence failed:seq=" + sequence);
        return null;
    }

    public GRU getGru() {
        return gru;
    }

    public DefaultCategoryDataset getDataset() {
        return dataset;
    }

    public DataText getDataText() {
        return dataText;
    }

    public static void main(String[] args) {
        String itemName = "西兰花";
        DataText dt = new DataText(itemName);
        double lr = 1;      //学习速率，越高模型训练速度越快，准度越低
        double acc = 0.33;   // 要求模型拥有的最高错误率
        int hiddenSize = 100;
        GRUPredict gru = new GRUPredict(dt.getCharIndex().size(), hiddenSize, new MatIniter(MatIniter.Type.Uniform, 0.1, 0, 0));
        gru.dataText = dt;
        gru.train(itemName, lr, acc);

    }

}
